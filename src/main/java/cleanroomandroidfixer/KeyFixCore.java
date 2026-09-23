package cleanroomandroidfixer;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Ядро фикса. Всё общение с Display/Keyboard - через рефлексию, чтобы мод
 * собирался обычным Forge MDK без зависимости от lwjglxx.
 *
 * Идея: экранные кнопки Mojo шлют key-события БЕЗ символа, а Display
 * из lwjglxx для печатных клавиш ждёт склейки key+char через charMods
 * и теряет нажатие. Поэтому оборачиваем GLFW key-callback: печатная
 * клавиша без Ctrl/Alt/Super сразу кладётся в очередь как key-событие,
 * а символ тут же синтезируется из фолбэк-таблицы US-раскладки.
 * Gboard и hardware-клавиатуры идут своим путём и не затрагиваются.
 */
public final class KeyFixCore {
    private KeyFixCore() {
    }

    private static final String TAG = "[Cleanroom-AndroidFixer]";

    private static final long STALE_INGREDIENT_NANOS = 10_000_000L; // 10 мс

    // --- рефлексия (инициализируется один раз) ---
    // На устройстве активен ТРАНСФОРМИРОВАННЫЙ класс org.lwjgl.opengl.Display
    // (LWJGLTransformer мержит в него lwjglx), а голый org.lwjglx.* лежит
    // мёртвым грузом с нулевым handle. Поэтому пробуем оба неймспейса
    // и работаем с тем, у которого isCreated() == true.
    private static boolean initTried = false;
    private static boolean logOnceFailed = false;

    private static final class Ref {
        final String ns;
        Method mIsCreated;
        Method mGetWindow;
        Method mAddGlfwKeyEvent;
        Method mAddCharEvent;
        Method mAddKeyEvent;
        Method mIsKeyDown;
        Method mAreRepeatEventsEnabled;
        Field fKeyCallback;
        Field fIngredient;
        Field fCancelNextChar;
        Field feNano; // KeyEvent.nano
        boolean ok;

        Ref(String ns) {
            this.ns = ns;
        }
    }

    private static final Ref refLwjgl = new Ref("org.lwjgl.");
    private static final Ref refLwjglx = new Ref("org.lwjglx.");
    private static Ref activeRef = null;

    // Текущий набор (копия из activeRef, чтобы не таскать ref по коду).
    private static Method mIsCreated;
    private static Method mGetWindow;
    private static Method mAddGlfwKeyEvent;
    private static Method mAddCharEvent;
    private static Method mAddKeyEvent;
    private static Method mIsKeyDown;
    private static Method mAreRepeatEventsEnabled;

    private static Field fKeyCallback;
    private static Field fIngredient;
    private static Field fCancelNextChar;
    private static Field feNano; // KeyEvent.nano

    // --- состояние ---
    private static boolean installed = false;
    private static long installedHandle = 0L;

    private static GLFWKeyCallback origKeyCb;

    private static volatile boolean capsOn = false;

    private static volatile long cntSplit = 0L;
    private static volatile long cntStale = 0L;
    private static volatile long frameCount = 0L;

    private static void debug(String msg) {
        System.out.println(TAG + " " + msg);
    }

    // Обёртка (держим сильную ссылку, иначе GC убьёт нативный колбэк).
    private static final GLFWKeyCallback keyWrapper = new GLFWKeyCallback() {
        @Override
        public void invoke(long window, int key, int scancode, int action, int mods) {
            try {
                if (key == GLFW.GLFW_KEY_CAPS_LOCK && action == GLFW.GLFW_PRESS) {
                    capsOn = !capsOn;
                }
                boolean printable = (key > GLFW.GLFW_KEY_SPACE && key <= GLFW.GLFW_KEY_GRAVE_ACCENT)
                        || (key >= GLFW.GLFW_KEY_KP_0 && key <= GLFW.GLFW_KEY_KP_EQUAL);
                boolean combo = (mods & (GLFW.GLFW_MOD_CONTROL | GLFW.GLFW_MOD_ALT | GLFW.GLFW_MOD_SUPER)) != 0;
                if (printable && !combo && action != GLFW.GLFW_RELEASE && origKeyCb != null) {
                    // key-событие сразу, символ - сразу следом, ничего не ждём.
                    mAddGlfwKeyEvent.invoke(null, window, key, scancode, action, mods, '\0');
                    // Как в оригинале в начале keyCallback: сбрасываем чужой cancel.
                    fCancelNextChar.setBoolean(null, false);
                    drainStaleIngredientQuiet();
                    if (action != GLFW.GLFW_REPEAT || repeatEnabled()) {
                        int lwjgl = FallbackChars.glfwToLwjgl(key);
                        if (lwjgl != FallbackChars.KEY_NONE) {
                            char c = FallbackChars.toChar(lwjgl, shiftDown(), capsOn);
                            if (c != 0) {
                                mAddCharEvent.invoke(null, FallbackChars.KEY_NONE, c);
                            }
                        }
                    }
                    cntSplit++;
                    return;
                }
            } catch (Throwable t) {
                // ниже - делегирование оригиналу
            }
            try {
                if (origKeyCb != null) {
                    origKeyCb.invoke(window, key, scancode, action, mods);
                }
            } catch (Throwable t) {
                // колбэк не должен ронять игру
            }
        }
    };

    /** Вызывается каждый кадр из RenderTickEvent.START (main thread). */
    public static void onFrame() {
        if (!ensureInit()) {
            return;
        }
        if (!pickNamespace()) {
            return;
        }
        ensureInstalled();
        if (!installed) {
            return;
        }
        try {
            drainStaleIngredient();
        } catch (Throwable t) {
            // ignore
        }
    }

    private static boolean ensureInit() {
        if (initTried) {
            return refLwjgl.ok || refLwjglx.ok;
        }
        initTried = true;
        resolveRef(refLwjgl);
        resolveRef(refLwjglx);
        debug("reflection ready lwjgl=" + refLwjgl.ok + " lwjglx=" + refLwjglx.ok);
        if (!refLwjgl.ok && !refLwjglx.ok && !logOnceFailed) {
            logOnceFailed = true;
            System.out.println(TAG + " reflection failed, fix disabled");
        }
        return refLwjgl.ok || refLwjglx.ok;
    }

    private static void resolveRef(Ref r) {
        try {
            Class<?> displayCls = Class.forName(r.ns + "opengl.Display");
            Class<?> keyboardCls = Class.forName(r.ns + "input.Keyboard");
            Class<?> keyEventCls = Class.forName(r.ns + "input.KeyEvent");
            Class<?> windowCls = Class.forName(r.ns + "opengl.Display$Window");

            r.mIsCreated = displayCls.getMethod("isCreated");
            r.mGetWindow = displayCls.getMethod("getWindow");
            r.mAddGlfwKeyEvent = keyboardCls.getMethod("addGlfwKeyEvent",
                    long.class, int.class, int.class, int.class, int.class, char.class);
            r.mAddCharEvent = keyboardCls.getMethod("addCharEvent", int.class, char.class);
            r.mAddKeyEvent = keyboardCls.getMethod("addKeyEvent", keyEventCls);
            r.mIsKeyDown = keyboardCls.getMethod("isKeyDown", int.class);
            r.mAreRepeatEventsEnabled = keyboardCls.getMethod("areRepeatEventsEnabled");

            r.fKeyCallback = windowCls.getDeclaredField("keyCallback");
            r.fKeyCallback.setAccessible(true);

            r.fIngredient = displayCls.getDeclaredField("ingredientKeyEvent");
            r.fCancelNextChar = displayCls.getDeclaredField("cancelNextChar");
            r.fIngredient.setAccessible(true);
            r.fCancelNextChar.setAccessible(true);

            r.feNano = keyEventCls.getField("nano");
            r.ok = true;
        } catch (Throwable t) {
            debug("reflection ns " + r.ns + " unavailable: " + t);
        }
    }

    /** Выбирает неймспейс, где Display реально создан. Копирует его мемберы в работу. */
    private static boolean pickNamespace() {
        if (activeRef != null) {
            try {
                if (((Boolean) activeRef.mIsCreated.invoke(null)).booleanValue()) {
                    return true;
                }
            } catch (Throwable t) {
                // упадём ниже на перевыбор
            }
            debug("namespace " + activeRef.ns + " lost, re-picking");
            activeRef = null;
            installed = false;
        }
        Ref[] order = new Ref[] { refLwjgl, refLwjglx };
        for (int i = 0; i < order.length; i++) {
            Ref r = order[i];
            if (!r.ok) {
                continue;
            }
            try {
                if (((Boolean) r.mIsCreated.invoke(null)).booleanValue()) {
                    activeRef = r;
                    mIsCreated = r.mIsCreated;
                    mGetWindow = r.mGetWindow;
                    mAddGlfwKeyEvent = r.mAddGlfwKeyEvent;
                    mAddCharEvent = r.mAddCharEvent;
                    mAddKeyEvent = r.mAddKeyEvent;
                    mIsKeyDown = r.mIsKeyDown;
                    mAreRepeatEventsEnabled = r.mAreRepeatEventsEnabled;
                    fKeyCallback = r.fKeyCallback;
                    fIngredient = r.fIngredient;
                    fCancelNextChar = r.fCancelNextChar;
                    feNano = r.feNano;
                    debug("using namespace " + r.ns);
                    return true;
                }
            } catch (Throwable t) {
                // пробуем следующий
            }
        }
        return false;
    }

    private static void ensureInstalled() {
        try {
            long handle = ((Long) mGetWindow.invoke(null)).longValue();
            if (handle == 0L) {
                return;
            }
            Object cur = fKeyCallback.get(null);
            if (installed && handle == installedHandle && cur == keyWrapper) {
                return;
            }
            GLFWKeyCallback prevKey = GLFW.glfwSetKeyCallback(handle, keyWrapper);
            if (prevKey == null) {
                // Оригинал ещё не стоит - откатываемся, попробуем позже.
                GLFW.glfwSetKeyCallback(handle, null);
                return;
            }
            origKeyCb = prevKey;
            fKeyCallback.set(null, keyWrapper);
            installed = true;
            installedHandle = handle;
            debug("key callback wrapped handle=" + handle + " orig=" + prevKey.getClass().getName());
        } catch (Throwable t) {
            System.out.println(TAG + " install failed: " + t);
        }
    }

    /** Протухший ingredient оригинала: настоящего char уже не будет - спасаем хоть key. */
    private static void drainStaleIngredient() throws Exception {
        Object ingr = fIngredient.get(null);
        if (ingr == null) {
            return;
        }
        long nano = feNano.getLong(ingr);
        if (System.nanoTime() - nano < STALE_INGREDIENT_NANOS) {
            return;
        }
        fIngredient.set(null, null);
        cntStale++;
        try {
            mAddKeyEvent.invoke(null, ingr);
        } catch (Throwable t) {
            // ignore
        }
    }

    /** Тихий вариант для вызова из key-колбэка (без проброса исключений). */
    private static void drainStaleIngredientQuiet() {
        try {
            drainStaleIngredient();
        } catch (Throwable t) {
            // ignore
        }
    }

    private static boolean shiftDown() {
        try {
            return ((Boolean) mIsKeyDown.invoke(null, FallbackChars.KEY_LSHIFT)).booleanValue()
                    || ((Boolean) mIsKeyDown.invoke(null, FallbackChars.KEY_RSHIFT)).booleanValue();
        } catch (Throwable t) {
            return false;
        }
    }

    private static boolean repeatEnabled() {
        try {
            return ((Boolean) mAreRepeatEventsEnabled.invoke(null)).booleanValue();
        } catch (Throwable t) {
            return true;
        }
    }
}

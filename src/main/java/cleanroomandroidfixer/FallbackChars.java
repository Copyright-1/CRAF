package cleanroomandroidfixer;

/**
 * Фолбэк-таблица US-раскладки: код клавиши LWJGL2 -> символ.
 * Используется ТОЛЬКО когда настоящий char от GLFW не пришёл
 * (экранные кнопки Mojo). Значения кодов совпадают с org.lwjglx.input.Keyboard.
 */
public final class FallbackChars {
    private FallbackChars() {
    }

    public static final int KEY_NONE = 0x00;
    public static final int KEY_1 = 0x02;
    public static final int KEY_2 = 0x03;
    public static final int KEY_3 = 0x04;
    public static final int KEY_4 = 0x05;
    public static final int KEY_5 = 0x06;
    public static final int KEY_6 = 0x07;
    public static final int KEY_7 = 0x08;
    public static final int KEY_8 = 0x09;
    public static final int KEY_9 = 0x0A;
    public static final int KEY_0 = 0x0B;
    public static final int KEY_MINUS = 0x0C;
    public static final int KEY_EQUALS = 0x0D;
    public static final int KEY_TAB = 0x0F;
    public static final int KEY_Q = 0x10;
    public static final int KEY_W = 0x11;
    public static final int KEY_E = 0x12;
    public static final int KEY_R = 0x13;
    public static final int KEY_T = 0x14;
    public static final int KEY_Y = 0x15;
    public static final int KEY_U = 0x16;
    public static final int KEY_I = 0x17;
    public static final int KEY_O = 0x18;
    public static final int KEY_P = 0x19;
    public static final int KEY_LBRACKET = 0x1A;
    public static final int KEY_RBRACKET = 0x1B;
    public static final int KEY_RETURN = 0x1C;
    public static final int KEY_A = 0x1E;
    public static final int KEY_S = 0x1F;
    public static final int KEY_D = 0x20;
    public static final int KEY_F = 0x21;
    public static final int KEY_G = 0x22;
    public static final int KEY_H = 0x23;
    public static final int KEY_J = 0x24;
    public static final int KEY_K = 0x25;
    public static final int KEY_L = 0x26;
    public static final int KEY_SEMICOLON = 0x27;
    public static final int KEY_APOSTROPHE = 0x28;
    public static final int KEY_GRAVE = 0x29;
    public static final int KEY_LSHIFT = 0x2A;
    public static final int KEY_BACKSLASH = 0x2B;
    public static final int KEY_Z = 0x2C;
    public static final int KEY_X = 0x2D;
    public static final int KEY_C = 0x2E;
    public static final int KEY_V = 0x2F;
    public static final int KEY_B = 0x30;
    public static final int KEY_N = 0x31;
    public static final int KEY_M = 0x32;
    public static final int KEY_COMMA = 0x33;
    public static final int KEY_PERIOD = 0x34;
    public static final int KEY_SLASH = 0x35;
    public static final int KEY_RSHIFT = 0x36;
    public static final int KEY_MULTIPLY = 0x37;
    public static final int KEY_SPACE = 0x39;
    public static final int KEY_NUMPAD7 = 0x47;
    public static final int KEY_NUMPAD8 = 0x48;
    public static final int KEY_NUMPAD9 = 0x49;
    public static final int KEY_SUBTRACT = 0x4A;
    public static final int KEY_NUMPAD4 = 0x4B;
    public static final int KEY_NUMPAD5 = 0x4C;
    public static final int KEY_NUMPAD6 = 0x4D;
    public static final int KEY_ADD = 0x4E;
    public static final int KEY_NUMPAD1 = 0x4F;
    public static final int KEY_NUMPAD2 = 0x50;
    public static final int KEY_NUMPAD3 = 0x51;
    public static final int KEY_NUMPAD0 = 0x52;
    public static final int KEY_DECIMAL = 0x53;
    public static final int KEY_NUMPADENTER = 0x9C;
    public static final int KEY_DIVIDE = 0xB5;
    public static final int KEY_NUMPADEQUALS = 0x8D;

    /** GLFW key -> LWJGL2 key для печатного диапазона. 0 = неизвестно. */
    public static int glfwToLwjgl(int glfwKey) {
        switch (glfwKey) {
            case 32: return KEY_SPACE;      // SPACE
            case 39: return KEY_APOSTROPHE; // '
            case 44: return KEY_COMMA;      // ,
            case 45: return KEY_MINUS;      // -
            case 46: return KEY_PERIOD;     // .
            case 47: return KEY_SLASH;      // /
            case 48: return KEY_0;
            case 49: return KEY_1;
            case 50: return KEY_2;
            case 51: return KEY_3;
            case 52: return KEY_4;
            case 53: return KEY_5;
            case 54: return KEY_6;
            case 55: return KEY_7;
            case 56: return KEY_8;
            case 57: return KEY_9;
            case 59: return KEY_SEMICOLON;  // ;
            case 61: return KEY_EQUALS;     // =
            case 65: return KEY_A;
            case 66: return KEY_B;
            case 67: return KEY_C;
            case 68: return KEY_D;
            case 69: return KEY_E;
            case 70: return KEY_F;
            case 71: return KEY_G;
            case 72: return KEY_H;
            case 73: return KEY_I;
            case 74: return KEY_J;
            case 75: return KEY_K;
            case 76: return KEY_L;
            case 77: return KEY_M;
            case 78: return KEY_N;
            case 79: return KEY_O;
            case 80: return KEY_P;
            case 81: return KEY_Q;
            case 82: return KEY_R;
            case 83: return KEY_S;
            case 84: return KEY_T;
            case 85: return KEY_U;
            case 86: return KEY_V;
            case 87: return KEY_W;
            case 88: return KEY_X;
            case 89: return KEY_Y;
            case 90: return KEY_Z;
            case 91: return KEY_LBRACKET;   // [
            case 92: return KEY_BACKSLASH;  // "\"
            case 93: return KEY_RBRACKET;   // ]
            case 96: return KEY_GRAVE;      // `
            case 320: return KEY_NUMPAD0;
            case 321: return KEY_NUMPAD1;
            case 322: return KEY_NUMPAD2;
            case 323: return KEY_NUMPAD3;
            case 324: return KEY_NUMPAD4;
            case 325: return KEY_NUMPAD5;
            case 326: return KEY_NUMPAD6;
            case 327: return KEY_NUMPAD7;
            case 328: return KEY_NUMPAD8;
            case 329: return KEY_NUMPAD9;
            case 330: return KEY_DECIMAL;
            case 331: return KEY_DIVIDE;
            case 332: return KEY_MULTIPLY;
            case 333: return KEY_SUBTRACT;
            case 334: return KEY_ADD;
            case 335: return KEY_NUMPADENTER;
            case 336: return KEY_NUMPADEQUALS;
            default: return KEY_NONE;
        }
    }

    /**
     * Символ для клавиши. Возвращает 0, если для клавиши символа нет
     * (тогда в очередь уйдёт только key-событие - бинды/движение работают).
     */
    public static char toChar(int lwjglKey, boolean shift, boolean caps) {
        switch (lwjglKey) {
            // Буквы
            case KEY_A: return shiftLetter('a', shift, caps);
            case KEY_B: return shiftLetter('b', shift, caps);
            case KEY_C: return shiftLetter('c', shift, caps);
            case KEY_D: return shiftLetter('d', shift, caps);
            case KEY_E: return shiftLetter('e', shift, caps);
            case KEY_F: return shiftLetter('f', shift, caps);
            case KEY_G: return shiftLetter('g', shift, caps);
            case KEY_H: return shiftLetter('h', shift, caps);
            case KEY_I: return shiftLetter('i', shift, caps);
            case KEY_J: return shiftLetter('j', shift, caps);
            case KEY_K: return shiftLetter('k', shift, caps);
            case KEY_L: return shiftLetter('l', shift, caps);
            case KEY_M: return shiftLetter('m', shift, caps);
            case KEY_N: return shiftLetter('n', shift, caps);
            case KEY_O: return shiftLetter('o', shift, caps);
            case KEY_P: return shiftLetter('p', shift, caps);
            case KEY_Q: return shiftLetter('q', shift, caps);
            case KEY_R: return shiftLetter('r', shift, caps);
            case KEY_S: return shiftLetter('s', shift, caps);
            case KEY_T: return shiftLetter('t', shift, caps);
            case KEY_U: return shiftLetter('u', shift, caps);
            case KEY_V: return shiftLetter('v', shift, caps);
            case KEY_W: return shiftLetter('w', shift, caps);
            case KEY_X: return shiftLetter('x', shift, caps);
            case KEY_Y: return shiftLetter('y', shift, caps);
            case KEY_Z: return shiftLetter('z', shift, caps);
            // Цифры верхнего ряда
            case KEY_1: return shift ? '!' : '1';
            case KEY_2: return shift ? '@' : '2';
            case KEY_3: return shift ? '#' : '3';
            case KEY_4: return shift ? '$' : '4';
            case KEY_5: return shift ? '%' : '5';
            case KEY_6: return shift ? '^' : '6';
            case KEY_7: return shift ? '&' : '7';
            case KEY_8: return shift ? '*' : '8';
            case KEY_9: return shift ? '(' : '9';
            case KEY_0: return shift ? ')' : '0';
            // Пунктуация
            case KEY_MINUS: return shift ? '_' : '-';
            case KEY_EQUALS: return shift ? '+' : '=';
            case KEY_LBRACKET: return shift ? '{' : '[';
            case KEY_RBRACKET: return shift ? '}' : ']';
            case KEY_SEMICOLON: return shift ? ':' : ';';
            case KEY_APOSTROPHE: return shift ? '"' : '\'';
            case KEY_GRAVE: return shift ? '~' : '`';
            case KEY_BACKSLASH: return shift ? '|' : '\\';
            case KEY_COMMA: return shift ? '<' : ',';
            case KEY_PERIOD: return shift ? '>' : '.';
            case KEY_SLASH: return shift ? '?' : '/';
            case KEY_SPACE: return ' ';
            case KEY_TAB: return '\t';
            case KEY_RETURN: return '\r';
            case KEY_NUMPADENTER: return '\r';
            // Нумпад (состояние NumLock не отслеживаем - цифры нужнее)
            case KEY_NUMPAD0: return '0';
            case KEY_NUMPAD1: return '1';
            case KEY_NUMPAD2: return '2';
            case KEY_NUMPAD3: return '3';
            case KEY_NUMPAD4: return '4';
            case KEY_NUMPAD5: return '5';
            case KEY_NUMPAD6: return '6';
            case KEY_NUMPAD7: return '7';
            case KEY_NUMPAD8: return '8';
            case KEY_NUMPAD9: return '9';
            case KEY_DECIMAL: return '.';
            case KEY_ADD: return '+';
            case KEY_SUBTRACT: return '-';
            case KEY_MULTIPLY: return '*';
            case KEY_DIVIDE: return '/';
            case KEY_NUMPADEQUALS: return '=';
            default: return 0;
        }
    }

    private static char shiftLetter(char base, boolean shift, boolean caps) {
        if (shift ^ caps) {
            return (char) (base - ('a' - 'A'));
        }
        return base;
    }
}

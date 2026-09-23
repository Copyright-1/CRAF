package cleanroomandroidfixer;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Патч-мод для Cleanroom (1.12.2) под MojoLauncher/Android.
 *
 * Проблема: экранные кнопки Mojo шлют key-события БЕЗ символа (codepoint = 0),
 * а Display из lwjglxx-cleanroom для печатных клавиш ждёт склейки key+char
 * через charMods и теряет нажатие. В итоге буквы/символы пустые.
 *
 * Мод ничего не меняет в Mojo: он оборачивает GLFW-колбэки, сразу кладёт
 * key-событие в очередь (режим "split", который ванильный 1.12.2 понимает:
 * key отдельно, char отдельно), а символ синтезирует сам из кода клавиши,
 * если настоящий char так и не пришёл (Gboard и hardware-клавиатуры шлют
 * настоящие символы - их мод не трогает и не дублирует).
 */
@Mod(modid = KeyFixMod.MODID,
        name = KeyFixMod.NAME,
        version = KeyFixMod.VERSION,
        acceptableRemoteVersions = "*",
        acceptedMinecraftVersions = "[1.12.2]")
public class KeyFixMod {
    public static final String MODID = "cleanroomandroidfixer";
    public static final String NAME = "Cleanroom Android Fixer";
    public static final String VERSION = "1.0.0";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        System.out.println("[Cleanroom-AndroidFixer] preInit side=" + event.getSide());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        if (event.getSide() == Side.CLIENT) {
            MinecraftForge.EVENT_BUS.register(new KeyFixHandler());
            boolean kirino;
            try {
                Class.forName("com.cleanroommc.kirino.KirinoClientCore", false, getClass().getClassLoader());
                kirino = true;
            } catch (Throwable t) {
                kirino = false;
            }
            System.out.println("[cleanroom-AndroidFixer] registered, kirino present=" + kirino);
        }
    }
}

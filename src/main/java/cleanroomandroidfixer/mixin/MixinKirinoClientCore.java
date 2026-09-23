package cleanroomandroidfixer.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Убирает проверку "ровно OpenGL 4.6" из KirinoClientCore.init().
 * Трансляторы (Zink, gl_translator) предоставляют нужные функции
 * независимо от заявленной версии GL, так что гет версии не имеет смысла.
 * Без Kirino-классов миксин не применяется (см. KirinoMixinPlugin).
 */
@Mixin(targets = "com.cleanroommc.kirino.KirinoClientCore", remap = false)
public abstract class MixinKirinoClientCore {

    @Redirect(
            method = "init",
            at = @At(value = "INVOKE",
                    target = "Lcom/cleanroommc/kirino/gl/GLDeviceInfo;getVersionMajor()I"),
            remap = false)
    private int cleanroomandroidfixer$glMajor(Object self) {
        return 4;
    }

    @Redirect(
            method = "init",
            at = @At(value = "INVOKE",
                    target = "Lcom/cleanroommc/kirino/gl/GLDeviceInfo;getVersionMinor()I"),
            remap = false)
    private int cleanroomandroidfixer$glMinor(Object self) {
        return 6;
    }
}

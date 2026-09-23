package cleanroomandroidfixer;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/** Каждый кадр: установка обёрток (когда Display создан) и дозревание символов. */
public class KeyFixHandler {
    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase != TickEvent.Phase.START) {
            return;
        }
        KeyFixCore.onFrame();
    }
}

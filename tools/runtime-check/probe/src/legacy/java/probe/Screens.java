package probe;

import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Screen access on Minecraft 1.20.1, before WorldOpenFlows.openWorld existed. */
final class Screens {
    private Screens() {
    }

    static Screen current(Minecraft mc) {
        return mc.screen;
    }

    static void show(Minecraft mc, Screen screen) {
        mc.setScreen(screen);
    }

    static boolean isLoading(Minecraft mc) {
        return mc.getOverlay() != null;
    }

    static void screenshot(Minecraft mc, Consumer<Component> done) {
        Screenshot.grab(mc.gameDirectory, mc.getMainRenderTarget(), done);
    }

    static void openWorld(Minecraft mc, String name, Runnable onCancel) {
        // 1.20.1 has no cancel callback; a refused world simply never loads,
        // which the step's timeout reports.
        // It also loads the world right here, ticking nothing until it is in,
        // so the watchdog is told to wait rather than call that a freeze.
        Probe.blockingLoad = true;
        try {
            mc.createWorldOpenFlows().loadLevel(mc.screen, name);
        } finally {
            Probe.blockingLoad = false;
        }
    }
}

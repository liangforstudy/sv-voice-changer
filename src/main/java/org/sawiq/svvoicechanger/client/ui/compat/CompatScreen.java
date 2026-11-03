package org.sawiq.svvoicechanger.client.ui.compat;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
//? if >=1.21.9 {
/*import net.minecraft.client.input.KeyEvent;
*///?}
import net.minecraft.network.chat.Component;

/**
 * Screen base class for the studio and the AI voice screens, hiding the
 * version-specific render and key-event signatures behind three stable hooks,
 * and on 1.20.1 its single-axis scroll and self-drawn background.
 */
public abstract class CompatScreen extends Screen {
    protected CompatScreen(Component title) {
        super(title);
    }

    @Override
    //? if >=26.1 {
    /*public void extractRenderState(GuiGraphics context, int mouseX, int mouseY, float delta) {
    *///?} else {
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
    //?}
        StudioGraphics graphics = new StudioGraphics(context, this.font);
        //? if <1.21 {
        /*// Up to 1.20.1 a screen draws its own background; later versions do it
        // inside super.render.
        this.renderBackground(context);
        *///?}
        paintBehindWidgets(graphics);
        //? if >=26.1 {
        /*super.extractRenderState(context, mouseX, mouseY, delta);
        *///?} else {
        super.render(context, mouseX, mouseY, delta);
        //?}
        paintAboveWidgets(graphics, mouseX, mouseY);
    }

    //? if >=1.21.9 {
    /*@Override
    public boolean keyPressed(KeyEvent event) {
        return onKeyPressed(event.key()) || super.keyPressed(event);
    }
    *///?} else {
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return onKeyPressed(keyCode) || super.keyPressed(keyCode, scanCode, modifiers);
    }
    //?}

    //? if <1.21 {
    /*/^*
     * Minecraft 1.20.1 reports a single scroll amount. Turned into the
     * two-axis form later versions use, so the screens override one method on
     * every version; the default hands it back to the vanilla handler.
     ^/
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return mouseScrolled(mouseX, mouseY, 0.0, amount);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return super.mouseScrolled(mouseX, mouseY, verticalAmount);
    }
    *///?}

    /** Drawn before the widgets: backdrops, panels, section headings. */
    protected void paintBehindWidgets(StudioGraphics graphics) {
    }

    /** Drawn after the widgets: scrollbars and status lines. */
    protected void paintAboveWidgets(StudioGraphics graphics, int mouseX, int mouseY) {
    }

    /** @return true when the key was handled and should not fall through */
    protected boolean onKeyPressed(int keyCode) {
        return false;
    }
}

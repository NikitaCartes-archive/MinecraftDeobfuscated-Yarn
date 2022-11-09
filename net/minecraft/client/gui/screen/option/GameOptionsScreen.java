/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.option;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class GameOptionsScreen
extends Screen {
    protected final Screen parent;
    protected final GameOptions gameOptions;

    public GameOptionsScreen(Screen parent, GameOptions gameOptions, Text title) {
        super(title);
        this.parent = parent;
        this.gameOptions = gameOptions;
    }

    @Override
    public void removed() {
        this.client.options.write();
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    protected void render(MatrixStack matrices, ButtonListWidget optionButtons, int mouseX, int mouseY, float tickDelta) {
        this.renderBackground(matrices);
        optionButtons.render(matrices, mouseX, mouseY, tickDelta);
        GameOptionsScreen.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        super.render(matrices, mouseX, mouseY, tickDelta);
    }
}


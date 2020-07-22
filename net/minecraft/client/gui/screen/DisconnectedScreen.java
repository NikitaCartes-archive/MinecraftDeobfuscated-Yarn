/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5489;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(value=EnvType.CLIENT)
public class DisconnectedScreen
extends Screen {
    private final Text reason;
    private class_5489 reasonFormatted = class_5489.field_26528;
    private final Screen parent;
    private int reasonHeight;

    public DisconnectedScreen(Screen parent, Text text, Text reason) {
        super(text);
        this.parent = parent;
        this.reason = reason;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    protected void init() {
        this.reasonFormatted = class_5489.method_30890(this.textRenderer, this.reason, this.width - 50);
        this.reasonHeight = this.reasonFormatted.method_30887() * this.textRenderer.fontHeight;
        this.addButton(new ButtonWidget(this.width / 2 - 100, Math.min(this.height / 2 + this.reasonHeight / 2 + this.textRenderer.fontHeight, this.height - 30), 200, 20, new TranslatableText("gui.toMenu"), buttonWidget -> this.client.openScreen(this.parent)));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        DisconnectedScreen.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, this.height / 2 - this.reasonHeight / 2 - this.textRenderer.fontHeight * 2, 0xAAAAAA);
        this.reasonFormatted.method_30888(matrices, this.width / 2, this.height / 2 - this.reasonHeight / 2);
        super.render(matrices, mouseX, mouseY, delta);
    }
}


/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class DisconnectedScreen
extends Screen {
    private final Text reason;
    private MultilineText reasonFormatted = MultilineText.EMPTY;
    private final Screen parent;
    private int reasonHeight;

    public DisconnectedScreen(Screen parent, Text title, Text reason) {
        super(title);
        this.parent = parent;
        this.reason = reason;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    protected void init() {
        this.reasonFormatted = MultilineText.create(this.textRenderer, (StringVisitable)this.reason, this.width - 50);
        this.reasonHeight = this.reasonFormatted.count() * this.textRenderer.fontHeight;
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.toMenu"), button -> this.client.setScreen(this.parent)).dimensions(this.width / 2 - 100, Math.min(this.height / 2 + this.reasonHeight / 2 + this.textRenderer.fontHeight, this.height - 30), 200, 20).build());
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        DisconnectedScreen.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, this.height / 2 - this.reasonHeight / 2 - this.textRenderer.fontHeight * 2, 0xAAAAAA);
        this.reasonFormatted.drawCenterWithShadow(matrices, this.width / 2, this.height / 2 - this.reasonHeight / 2);
        super.render(matrices, mouseX, mouseY, delta);
    }
}


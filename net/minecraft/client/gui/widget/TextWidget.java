/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class TextWidget
extends ClickableWidget {
    private int textColor = 0xFFFFFF;
    private final TextRenderer textRenderer;

    public TextWidget(Text message, TextRenderer textRenderer) {
        this(0, 0, textRenderer.getWidth(message.asOrderedText()), textRenderer.fontHeight, message, textRenderer);
    }

    public TextWidget(int width, int height, Text message, TextRenderer textRenderer) {
        this(0, 0, width, height, message, textRenderer);
    }

    public TextWidget(int x, int y, int width, int height, Text message, TextRenderer textRenderer) {
        super(x, y, width, height, message);
        this.textRenderer = textRenderer;
    }

    public TextWidget setTextColor(int textColor) {
        this.textColor = textColor;
        return this;
    }

    @Override
    public void appendClickableNarrations(NarrationMessageBuilder builder) {
    }

    @Override
    public boolean changeFocus(boolean lookForwards) {
        return false;
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        TextWidget.drawCenteredText(matrices, this.textRenderer, this.getMessage(), this.getX() + this.getWidth() / 2, this.getY() + (this.getHeight() - this.textRenderer.fontHeight) / 2, this.textColor);
    }
}


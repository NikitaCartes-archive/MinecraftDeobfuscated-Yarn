/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class PressableTextWidget
extends ButtonWidget {
    private final TextRenderer textRenderer;
    private final Text text;
    private final Text hoverText;

    public PressableTextWidget(int x, int y, int width, int height, Text text, ButtonWidget.PressAction onPress, TextRenderer textRenderer) {
        super(x, y, width, height, text, onPress);
        this.textRenderer = textRenderer;
        this.text = text;
        this.hoverText = Texts.setStyleIfAbsent(text.shallowCopy(), Style.EMPTY.withUnderline(true));
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        Text text = this.isHovered() ? this.hoverText : this.text;
        PressableTextWidget.drawTextWithShadow(matrices, this.textRenderer, text, this.x, this.y, 0xFFFFFF | MathHelper.ceil(this.alpha * 255.0f) << 24);
    }
}


/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class IconButtonWidget
extends ButtonWidget {
    protected final Identifier iconTexture;
    protected final int iconU;
    protected final int iconV;
    protected final int iconHoveredVOffset;
    protected final int iconTextureWidth;
    protected final int iconTextureHeight;
    private final int iconXOffset;
    private final int iconYOffset;
    private final int iconWidth;
    private final int iconHeight;

    IconButtonWidget(Text message, int iconU, int iconV, int iconXOffset, int iconYOffset, int iconHoveredVOffset, int iconWidth, int iconHeight, int iconTextureWidth, int iconTextureHeight, Identifier iconTexture, ButtonWidget.PressAction onPress) {
        super(0, 0, 150, 20, message, onPress, DEFAULT_NARRATION_SUPPLIER);
        this.iconTextureWidth = iconTextureWidth;
        this.iconTextureHeight = iconTextureHeight;
        this.iconU = iconU;
        this.iconV = iconV;
        this.iconHoveredVOffset = iconHoveredVOffset;
        this.iconTexture = iconTexture;
        this.iconXOffset = iconXOffset;
        this.iconYOffset = iconYOffset;
        this.iconWidth = iconWidth;
        this.iconHeight = iconHeight;
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.renderButton(matrices, mouseX, mouseY, delta);
        this.drawTexture(matrices, this.iconTexture, this.getIconX(), this.getIconY(), this.iconU, this.iconV, this.iconHoveredVOffset, this.iconWidth, this.iconHeight, this.iconTextureWidth, this.iconTextureHeight);
    }

    @Override
    public void drawMessage(MatrixStack matrices, TextRenderer textRenderer, int color) {
        int i = this.getX() + 2;
        int j = this.getX() + this.getWidth() - this.iconWidth - 6;
        IconButtonWidget.drawScrollableText(matrices, textRenderer, this.getMessage(), i, this.getY(), j, this.getY() + this.getHeight(), color);
    }

    private int getIconX() {
        return this.getX() + (this.width / 2 - this.iconWidth / 2) + this.iconXOffset;
    }

    private int getIconY() {
        return this.getY() + this.iconYOffset;
    }

    public static Builder builder(Text message, Identifier texture, ButtonWidget.PressAction pressAction) {
        return new Builder(message, texture, pressAction);
    }

    @Environment(value=EnvType.CLIENT)
    public static class Builder {
        private final Text message;
        private final Identifier texture;
        private final ButtonWidget.PressAction pressAction;
        private int u;
        private int v;
        private int hoveredVOffset;
        private int iconWidth;
        private int iconHeight;
        private int textureWidth;
        private int textureHeight;
        private int xOffset;
        private int yOffset;

        public Builder(Text message, Identifier texture, ButtonWidget.PressAction pressAction) {
            this.message = message;
            this.texture = texture;
            this.pressAction = pressAction;
        }

        public Builder uv(int u, int v) {
            this.u = u;
            this.v = v;
            return this;
        }

        public Builder xyOffset(int xOffset, int yOffset) {
            this.xOffset = xOffset;
            this.yOffset = yOffset;
            return this;
        }

        public Builder hoveredVOffset(int hoveredVOffset) {
            this.hoveredVOffset = hoveredVOffset;
            return this;
        }

        public Builder iconSize(int iconWidth, int iconHeight) {
            this.iconWidth = iconWidth;
            this.iconHeight = iconHeight;
            return this;
        }

        public Builder textureSize(int textureWidth, int textureHeight) {
            this.textureWidth = textureWidth;
            this.textureHeight = textureHeight;
            return this;
        }

        public IconButtonWidget build() {
            return new IconButtonWidget(this.message, this.u, this.v, this.xOffset, this.yOffset, this.hoveredVOffset, this.iconWidth, this.iconHeight, this.textureWidth, this.textureHeight, this.texture, this.pressAction);
        }
    }
}


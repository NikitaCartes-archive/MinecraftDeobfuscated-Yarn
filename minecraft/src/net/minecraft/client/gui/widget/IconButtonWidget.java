package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class IconButtonWidget extends TexturedButtonWidget {
	private static final int field_42122 = 5;
	private final int xOffset;
	private final int yOffset;
	private final int iconWidth;
	private final int iconHeight;

	IconButtonWidget(
		Text message,
		int u,
		int v,
		int xOffset,
		int yOffset,
		int hoveredVOffset,
		int iconWidth,
		int iconHeight,
		int textureWidth,
		int textureHeight,
		Identifier texture,
		ButtonWidget.PressAction pressAction
	) {
		super(0, 0, 150, 20, u, v, hoveredVOffset, texture, textureWidth, textureHeight, pressAction, message);
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.iconWidth = iconWidth;
		this.iconHeight = iconHeight;
	}

	@Override
	public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderButton(matrices, mouseX, mouseY);
		this.drawTexture(
			matrices,
			this.texture,
			this.getXWithOffset(),
			this.getYWithOffset(),
			this.u,
			this.v,
			this.hoveredVOffset,
			this.iconWidth,
			this.iconHeight,
			this.textureWidth,
			this.textureHeight
		);
	}

	@Override
	public void drawMessage(MatrixStack matrices, TextRenderer textRenderer, int centerX, int y, int color) {
		OrderedText orderedText = this.getMessage().asOrderedText();
		int i = textRenderer.getWidth(orderedText);
		int j = centerX - i / 2;
		int k = j + i;
		int l = this.getX() + this.width - this.iconWidth - 5;
		if (k >= l) {
			j -= k - l;
		}

		drawTextWithShadow(matrices, textRenderer, orderedText, j, y, color);
	}

	private int getXWithOffset() {
		return this.getX() + (this.width / 2 - this.iconWidth / 2) + this.xOffset;
	}

	private int getYWithOffset() {
		return this.getY() + this.yOffset;
	}

	public static IconButtonWidget.Builder builder(Text message, Identifier texture, ButtonWidget.PressAction pressAction) {
		return new IconButtonWidget.Builder(message, texture, pressAction);
	}

	@Environment(EnvType.CLIENT)
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

		public IconButtonWidget.Builder uv(int u, int v) {
			this.u = u;
			this.v = v;
			return this;
		}

		public IconButtonWidget.Builder xyOffset(int xOffset, int yOffset) {
			this.xOffset = xOffset;
			this.yOffset = yOffset;
			return this;
		}

		public IconButtonWidget.Builder hoveredVOffset(int hoveredVOffset) {
			this.hoveredVOffset = hoveredVOffset;
			return this;
		}

		public IconButtonWidget.Builder iconSize(int iconWidth, int iconHeight) {
			this.iconWidth = iconWidth;
			this.iconHeight = iconHeight;
			return this;
		}

		public IconButtonWidget.Builder textureSize(int textureWidth, int textureHeight) {
			this.textureWidth = textureWidth;
			this.textureHeight = textureHeight;
			return this;
		}

		public IconButtonWidget build() {
			return new IconButtonWidget(
				this.message,
				this.u,
				this.v,
				this.xOffset,
				this.yOffset,
				this.hoveredVOffset,
				this.iconWidth,
				this.iconHeight,
				this.textureWidth,
				this.textureHeight,
				this.texture,
				this.pressAction
			);
		}
	}
}

package net.minecraft.client.gui.widget;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * A button with an icon and an optional text.
 * 
 * @see ButtonWidget
 */
@Environment(EnvType.CLIENT)
public abstract class TextIconButtonWidget extends ButtonWidget {
	protected final Identifier texture;
	protected final int textureWidth;
	protected final int textureHeight;

	TextIconButtonWidget(int width, int height, Text message, int textureWidth, int textureHeight, Identifier texture, ButtonWidget.PressAction onPress) {
		super(0, 0, width, height, message, onPress, DEFAULT_NARRATION_SUPPLIER);
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
		this.texture = texture;
	}

	public static TextIconButtonWidget.Builder builder(Text text, ButtonWidget.PressAction onPress, boolean hideLabel) {
		return new TextIconButtonWidget.Builder(text, onPress, hideLabel);
	}

	@Environment(EnvType.CLIENT)
	public static class Builder {
		private final Text text;
		private final ButtonWidget.PressAction onPress;
		private final boolean hideText;
		private int width = 150;
		private int height = 20;
		@Nullable
		private Identifier texture;
		private int textureWidth;
		private int textureHeight;

		public Builder(Text text, ButtonWidget.PressAction onPress, boolean hideText) {
			this.text = text;
			this.onPress = onPress;
			this.hideText = hideText;
		}

		public TextIconButtonWidget.Builder width(int width) {
			this.width = width;
			return this;
		}

		public TextIconButtonWidget.Builder dimension(int width, int height) {
			this.width = width;
			this.height = height;
			return this;
		}

		public TextIconButtonWidget.Builder texture(Identifier texture, int width, int height) {
			this.texture = texture;
			this.textureWidth = width;
			this.textureHeight = height;
			return this;
		}

		public TextIconButtonWidget build() {
			if (this.texture == null) {
				throw new IllegalStateException("Sprite not set");
			} else {
				return (TextIconButtonWidget)(this.hideText
					? new TextIconButtonWidget.IconOnly(this.width, this.height, this.text, this.textureWidth, this.textureHeight, this.texture, this.onPress)
					: new TextIconButtonWidget.WithText(this.width, this.height, this.text, this.textureWidth, this.textureHeight, this.texture, this.onPress));
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class IconOnly extends TextIconButtonWidget {
		protected IconOnly(int i, int j, Text text, int k, int l, Identifier identifier, ButtonWidget.PressAction pressAction) {
			super(i, j, text, k, l, identifier, pressAction);
		}

		@Override
		public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
			super.renderButton(context, mouseX, mouseY, delta);
			int i = this.getX() + this.getWidth() / 2 - this.textureWidth / 2;
			int j = this.getY() + this.getHeight() / 2 - this.textureHeight / 2;
			context.drawGuiTexture(this.texture, i, j, this.textureWidth, this.textureHeight);
		}

		@Override
		public void drawMessage(DrawContext context, TextRenderer textRenderer, int color) {
		}
	}

	@Environment(EnvType.CLIENT)
	public static class WithText extends TextIconButtonWidget {
		protected WithText(int i, int j, Text text, int k, int l, Identifier identifier, ButtonWidget.PressAction pressAction) {
			super(i, j, text, k, l, identifier, pressAction);
		}

		@Override
		public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
			super.renderButton(context, mouseX, mouseY, delta);
			int i = this.getX() + this.getWidth() - this.textureWidth - 2;
			int j = this.getY() + this.getHeight() / 2 - this.textureHeight / 2;
			context.drawGuiTexture(this.texture, i, j, this.textureWidth, this.textureHeight);
		}

		@Override
		public void drawMessage(DrawContext context, TextRenderer textRenderer, int color) {
			int i = this.getX() + 2;
			int j = this.getX() + this.getWidth() - this.textureWidth - 4;
			int k = this.getX() + this.getWidth() / 2;
			drawScrollableText(context, textRenderer, this.getMessage(), k, i, this.getY(), j, this.getY() + this.getHeight(), color);
		}
	}
}

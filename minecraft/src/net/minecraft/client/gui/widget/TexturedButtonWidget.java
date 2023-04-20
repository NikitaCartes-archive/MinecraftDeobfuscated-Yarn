package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class TexturedButtonWidget extends ButtonWidget {
	protected final Identifier texture;
	protected final int u;
	protected final int v;
	protected final int hoveredVOffset;
	protected final int textureWidth;
	protected final int textureHeight;

	public TexturedButtonWidget(int x, int y, int width, int height, int u, int v, Identifier texture, ButtonWidget.PressAction pressAction) {
		this(x, y, width, height, u, v, height, texture, 256, 256, pressAction);
	}

	public TexturedButtonWidget(int x, int y, int width, int height, int u, int v, int hoveredVOffset, Identifier texture, ButtonWidget.PressAction pressAction) {
		this(x, y, width, height, u, v, hoveredVOffset, texture, 256, 256, pressAction);
	}

	public TexturedButtonWidget(
		int x,
		int y,
		int width,
		int height,
		int u,
		int v,
		int hoveredVOffset,
		Identifier texture,
		int textureWidth,
		int textureHeight,
		ButtonWidget.PressAction pressAction
	) {
		this(x, y, width, height, u, v, hoveredVOffset, texture, textureWidth, textureHeight, pressAction, ScreenTexts.EMPTY);
	}

	public TexturedButtonWidget(
		int x,
		int y,
		int width,
		int height,
		int u,
		int v,
		int hoveredVOffset,
		Identifier texture,
		int textureWidth,
		int textureHeight,
		ButtonWidget.PressAction pressAction,
		Text message
	) {
		super(x, y, width, height, message, pressAction, DEFAULT_NARRATION_SUPPLIER);
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
		this.u = u;
		this.v = v;
		this.hoveredVOffset = hoveredVOffset;
		this.texture = texture;
	}

	@Override
	public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
		this.drawTexture(
			context, this.texture, this.getX(), this.getY(), this.u, this.v, this.hoveredVOffset, this.width, this.height, this.textureWidth, this.textureHeight
		);
	}
}

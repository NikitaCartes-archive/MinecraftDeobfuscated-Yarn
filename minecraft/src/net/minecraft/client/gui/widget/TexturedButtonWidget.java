package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class TexturedButtonWidget extends ButtonWidget {
	private final Identifier texture;
	private final int u;
	private final int v;
	private final int hoveredVOffset;
	private final int textureWidth;
	private final int textureHeight;

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
		this(x, y, width, height, u, v, hoveredVOffset, texture, textureWidth, textureHeight, pressAction, LiteralText.EMPTY);
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
		Text text
	) {
		super(x, y, width, height, text, pressAction);
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
		this.u = u;
		this.v = v;
		this.hoveredVOffset = hoveredVOffset;
		this.texture = texture;
	}

	public void setPos(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public void renderButton(MatrixStack matrixStack, int i, int j, float f) {
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		minecraftClient.getTextureManager().bindTexture(this.texture);
		RenderSystem.disableDepthTest();
		int k = this.v;
		if (this.isHovered()) {
			k += this.hoveredVOffset;
		}

		drawTexture(matrixStack, this.x, this.y, (float)this.u, (float)k, this.width, this.height, this.textureWidth, this.textureHeight);
		RenderSystem.enableDepthTest();
	}
}

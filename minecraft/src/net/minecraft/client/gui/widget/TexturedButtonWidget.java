package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
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
		this(x, y, width, height, u, v, hoveredVOffset, texture, textureWidth, textureHeight, pressAction, "");
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
		String text
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
	public void renderButton(int mouseX, int mouseY, float delta) {
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		minecraftClient.getTextureManager().bindTexture(this.texture);
		GlStateManager.disableDepthTest();
		int i = this.v;
		if (this.isHovered()) {
			i += this.hoveredVOffset;
		}

		blit(this.x, this.y, (float)this.u, (float)i, this.width, this.height, this.textureWidth, this.textureHeight);
		GlStateManager.enableDepthTest();
	}
}

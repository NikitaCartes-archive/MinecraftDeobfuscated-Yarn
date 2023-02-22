package net.minecraft.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

@Environment(EnvType.CLIENT)
public class LogoDrawer extends DrawableHelper {
	public static final Identifier LOGO_TEXTURE = new Identifier("textures/gui/title/minecraft.png");
	public static final Identifier EDITION_TEXTURE = new Identifier("textures/gui/title/edition.png");
	public static final int field_41807 = 274;
	public static final int field_41808 = 44;
	public static final int field_41809 = 30;
	private final boolean minceraft = (double)Random.create().nextFloat() < 1.0E-4;
	private final boolean ignoreAlpha;

	public LogoDrawer(boolean ignoreAlpha) {
		this.ignoreAlpha = ignoreAlpha;
	}

	public void draw(MatrixStack matrices, int screenWidth, float alpha) {
		this.draw(matrices, screenWidth, alpha, 30);
	}

	public void draw(MatrixStack matrices, int screenWidth, float alpha, int y) {
		RenderSystem.setShaderTexture(0, LOGO_TEXTURE);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.ignoreAlpha ? 1.0F : alpha);
		int i = screenWidth / 2 - 137;
		if (this.minceraft) {
			drawWithOutline(i, y, (x, yx) -> {
				drawTexture(matrices, x, yx, 0, 0, 99, 44);
				drawTexture(matrices, x + 99, yx, 129, 0, 27, 44);
				drawTexture(matrices, x + 99 + 26, yx, 126, 0, 3, 44);
				drawTexture(matrices, x + 99 + 26 + 3, yx, 99, 0, 26, 44);
				drawTexture(matrices, x + 155, yx, 0, 45, 155, 44);
			});
		} else {
			drawWithOutline(i, y, (x, yx) -> {
				drawTexture(matrices, x, yx, 0, 0, 155, 44);
				drawTexture(matrices, x + 155, yx, 0, 45, 155, 44);
			});
		}

		RenderSystem.setShaderTexture(0, EDITION_TEXTURE);
		drawTexture(matrices, i + 88, y + 37, 0.0F, 0.0F, 98, 14, 128, 16);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
	}
}

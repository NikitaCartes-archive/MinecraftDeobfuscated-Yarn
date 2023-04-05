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
	public static final Identifier MINCERAFT_TEXTURE = new Identifier("textures/gui/title/minceraft.png");
	public static final Identifier EDITION_TEXTURE = new Identifier("textures/gui/title/edition.png");
	public static final int field_41807 = 256;
	public static final int field_41808 = 44;
	private static final int field_44541 = 256;
	private static final int field_44542 = 64;
	private static final int field_44543 = 128;
	private static final int field_44544 = 14;
	private static final int field_44545 = 128;
	private static final int field_44546 = 16;
	public static final int field_41809 = 30;
	private static final int field_44547 = 7;
	private final boolean minceraft = (double)Random.create().nextFloat() < 1.0E-4;
	private final boolean ignoreAlpha;

	public LogoDrawer(boolean ignoreAlpha) {
		this.ignoreAlpha = ignoreAlpha;
	}

	public void draw(MatrixStack matrices, int screenWidth, float alpha) {
		this.draw(matrices, screenWidth, alpha, 30);
	}

	public void draw(MatrixStack matrices, int screenWidth, float alpha, int y) {
		RenderSystem.setShaderTexture(0, this.minceraft ? MINCERAFT_TEXTURE : LOGO_TEXTURE);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.ignoreAlpha ? 1.0F : alpha);
		int i = screenWidth / 2 - 128;
		drawTexture(matrices, i, y, 0.0F, 0.0F, 256, 44, 256, 64);
		RenderSystem.setShaderTexture(0, EDITION_TEXTURE);
		int j = screenWidth / 2 - 64;
		int k = y + 44 - 7;
		drawTexture(matrices, j, k, 0.0F, 0.0F, 128, 14, 128, 16);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
	}
}

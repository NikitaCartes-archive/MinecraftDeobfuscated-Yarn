package net.minecraft.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

@Environment(EnvType.CLIENT)
public class LogoDrawer {
	public static final Identifier LOGO_TEXTURE = Identifier.ofVanilla("textures/gui/title/minecraft.png");
	public static final Identifier MINCERAFT_TEXTURE = Identifier.ofVanilla("textures/gui/title/minceraft.png");
	public static final Identifier EDITION_TEXTURE = Identifier.ofVanilla("textures/gui/title/edition.png");
	public static final int LOGO_REGION_WIDTH = 256;
	public static final int LOGO_REGION_HEIGHT = 44;
	private static final int LOGO_TEXTURE_WIDTH = 256;
	private static final int LOGO_TEXTURE_HEIGHT = 64;
	private static final int EDITION_REGION_WIDTH = 128;
	private static final int EDITION_REGION_HEIGHT = 14;
	private static final int EDITION_TEXTURE_WIDTH = 128;
	private static final int EDITION_TEXTURE_HEIGHT = 16;
	public static final int LOGO_BASE_Y = 30;
	private static final int LOGO_AND_EDITION_OVERLAP = 7;
	private final boolean minceraft = (double)Random.create().nextFloat() < 1.0E-4;
	private final boolean ignoreAlpha;

	public LogoDrawer(boolean ignoreAlpha) {
		this.ignoreAlpha = ignoreAlpha;
	}

	public void draw(DrawContext context, int screenWidth, float alpha) {
		this.draw(context, screenWidth, alpha, 30);
	}

	public void draw(DrawContext context, int screenWidth, float alpha, int y) {
		context.setShaderColor(1.0F, 1.0F, 1.0F, this.ignoreAlpha ? 1.0F : alpha);
		RenderSystem.enableBlend();
		int i = screenWidth / 2 - 128;
		context.drawTexture(this.minceraft ? MINCERAFT_TEXTURE : LOGO_TEXTURE, i, y, 0.0F, 0.0F, 256, 44, 256, 64);
		int j = screenWidth / 2 - 64;
		int k = y + 44 - 7;
		context.drawTexture(EDITION_TEXTURE, j, k, 0.0F, 0.0F, 128, 14, 128, 16);
		context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.disableBlend();
	}
}

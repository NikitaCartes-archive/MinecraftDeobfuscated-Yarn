package net.minecraft.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class LogoDrawer {
	public static final Identifier field_51079 = new Identifier("nothingtoseeheremovealong", "textures/gui/title/poisonous_potato_logo.png");
	public static final int LOGO_REGION_WIDTH = 256;
	public static final int LOGO_REGION_HEIGHT = 44;
	private static final int LOGO_TEXTURE_WIDTH = 256;
	private static final int LOGO_TEXTURE_HEIGHT = 128;
	public static final int LOGO_BASE_Y = 30;
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
		context.drawTexture(field_51079, i, y, 0.0F, 0.0F, 256, 128, 256, 128);
		context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.disableBlend();
	}
}

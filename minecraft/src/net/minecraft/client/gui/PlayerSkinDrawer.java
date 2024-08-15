package net.minecraft.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.util.Identifier;

/**
 * Helper class for drawing a player's head on GUI.
 */
@Environment(EnvType.CLIENT)
public class PlayerSkinDrawer {
	public static final int FACE_WIDTH = 8;
	public static final int FACE_HEIGHT = 8;
	public static final int FACE_X = 8;
	public static final int FACE_Y = 8;
	public static final int FACE_OVERLAY_X = 40;
	public static final int FACE_OVERLAY_Y = 8;
	public static final int field_39531 = 8;
	public static final int field_39532 = 8;
	public static final int SKIN_TEXTURE_WIDTH = 64;
	public static final int SKIN_TEXTURE_HEIGHT = 64;

	public static void draw(DrawContext context, SkinTextures textures, int x, int y, int size) {
		draw(context, textures, x, y, size, -1);
	}

	/**
	 * Draws the player's head (including the hat) on GUI.
	 */
	public static void draw(DrawContext context, SkinTextures skinTextures, int x, int y, int size, int i) {
		draw(context, skinTextures.texture(), x, y, size, true, false, i);
	}

	/**
	 * Draws the player's head on GUI.
	 */
	public static void draw(DrawContext context, Identifier texture, int x, int y, int size, boolean hatVisible, boolean upsideDown, int i) {
		int j = 8 + (upsideDown ? 8 : 0);
		int k = 8 * (upsideDown ? -1 : 1);
		context.drawTexture(RenderLayer::getGuiTextured, texture, x, y, 8.0F, (float)j, size, size, 8, k, 64, 64, i);
		if (hatVisible) {
			drawHat(context, texture, x, y, size, upsideDown, i);
		}
	}

	private static void drawHat(DrawContext context, Identifier texture, int x, int y, int size, boolean upsideDown, int i) {
		int j = 8 + (upsideDown ? 8 : 0);
		int k = 8 * (upsideDown ? -1 : 1);
		context.drawTexture(RenderLayer::getGuiTextured, texture, x, y, 40.0F, (float)j, size, size, 8, k, 64, 64, i);
	}
}

package net.minecraft.client.gui.tooltip;

import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

/**
 * A class for rendering a background box for a tooltip.
 */
@Environment(EnvType.CLIENT)
public class TooltipBackgroundRenderer {
	private static final Identifier DEFAULT_BACKGROUND_TEXTURE = Identifier.ofVanilla("tooltip/background");
	private static final Identifier DEFAULT_FRAME_TEXTURE = Identifier.ofVanilla("tooltip/frame");
	public static final int field_41688 = 12;
	private static final int field_41693 = 3;
	public static final int field_41689 = 3;
	public static final int field_41690 = 3;
	public static final int field_41691 = 3;
	public static final int field_41692 = 3;
	private static final int field_54153 = 9;

	public static void render(DrawContext context, int x, int y, int width, int height, int z, @Nullable Identifier texture) {
		int i = x - 3 - 9;
		int j = y - 3 - 9;
		int k = width + 3 + 3 + 18;
		int l = height + 3 + 3 + 18;
		context.getMatrices().push();
		context.getMatrices().translate(0.0F, 0.0F, (float)z);
		context.drawGuiTexture(RenderLayer::getGuiTextured, getBackgroundTexture(texture), i, j, k, l);
		context.drawGuiTexture(RenderLayer::getGuiTextured, getFrameTexture(texture), i, j, k, l);
		context.getMatrices().pop();
	}

	private static Identifier getBackgroundTexture(@Nullable Identifier texture) {
		return texture == null ? DEFAULT_BACKGROUND_TEXTURE : texture.withPath((UnaryOperator<String>)(name -> "tooltip/" + name + "_background"));
	}

	private static Identifier getFrameTexture(@Nullable Identifier texture) {
		return texture == null ? DEFAULT_FRAME_TEXTURE : texture.withPath((UnaryOperator<String>)(name -> "tooltip/" + name + "_frame"));
	}
}

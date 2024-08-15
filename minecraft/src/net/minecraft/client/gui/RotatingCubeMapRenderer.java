package net.minecraft.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

@Environment(EnvType.CLIENT)
public class RotatingCubeMapRenderer {
	public static final Identifier OVERLAY_TEXTURE = Identifier.ofVanilla("textures/gui/title/background/panorama_overlay.png");
	private final MinecraftClient client;
	private final CubeMapRenderer cubeMap;
	private float pitch;

	public RotatingCubeMapRenderer(CubeMapRenderer cubeMap) {
		this.cubeMap = cubeMap;
		this.client = MinecraftClient.getInstance();
	}

	public void render(DrawContext context, int width, int height, float alpha, float tickDelta) {
		float f = this.client.getRenderTickCounter().getLastDuration();
		float g = (float)((double)f * this.client.options.getPanoramaSpeed().getValue());
		this.pitch = wrapOnce(this.pitch + g * 0.1F, 360.0F);
		context.draw();
		this.cubeMap.draw(this.client, 10.0F, -this.pitch, alpha);
		context.draw();
		context.drawTexture(RenderLayer::getGuiTextured, OVERLAY_TEXTURE, 0, 0, 0.0F, 0.0F, width, height, 16, 128, 16, 128, ColorHelper.getWhite(alpha));
	}

	private static float wrapOnce(float a, float b) {
		return a > b ? a - b : a;
	}
}

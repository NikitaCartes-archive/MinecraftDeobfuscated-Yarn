package net.minecraft.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class RotatingCubeMapRenderer {
	public static final Identifier OVERLAY_TEXTURE = Identifier.ofVanilla("textures/gui/title/background/panorama_overlay.png");
	private final MinecraftClient client;
	private final CubeMapRenderer cubeMap;
	private float pitch;
	private float yaw;

	public RotatingCubeMapRenderer(CubeMapRenderer cubeMap) {
		this.cubeMap = cubeMap;
		this.client = MinecraftClient.getInstance();
	}

	public void render(DrawContext context, int width, int height, float alpha, float tickDelta) {
		float f = (float)((double)tickDelta * this.client.options.getPanoramaSpeed().getValue());
		this.pitch = wrapOnce(this.pitch + f * 0.1F, 360.0F);
		this.yaw = wrapOnce(this.yaw + f * 0.001F, (float) (Math.PI * 2));
		this.cubeMap.draw(this.client, 10.0F, -this.pitch, alpha);
		RenderSystem.enableBlend();
		context.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
		context.drawTexture(OVERLAY_TEXTURE, 0, 0, width, height, 0.0F, 0.0F, 16, 128, 16, 128);
		context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.disableBlend();
	}

	private static float wrapOnce(float a, float b) {
		return a > b ? a - b : a;
	}
}

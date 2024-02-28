package net.minecraft.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

@Environment(EnvType.CLIENT)
public class RotatingCubeMapRenderer {
	private final MinecraftClient client;
	private final CubeMapRenderer cubeMap;
	private float pitch;
	private float yaw;

	public RotatingCubeMapRenderer(CubeMapRenderer cubeMap) {
		this.cubeMap = cubeMap;
		this.client = MinecraftClient.getInstance();
	}

	public void render(float delta, float alpha) {
		float f = (float)((double)delta * this.client.options.getPanoramaSpeed().getValue());
		this.pitch = wrapOnce(this.pitch + f * 0.1F, 360.0F);
		this.yaw = wrapOnce(this.yaw + f * 0.001F, (float) (Math.PI * 2));
		this.cubeMap.draw(this.client, 10.0F, -this.pitch, alpha);
	}

	public void render(float delta) {
		this.render(delta, 1.0F);
	}

	private static float wrapOnce(float a, float b) {
		return a > b ? a - b : a;
	}
}

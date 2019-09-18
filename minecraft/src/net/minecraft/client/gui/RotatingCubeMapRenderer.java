package net.minecraft.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class RotatingCubeMapRenderer {
	private final MinecraftClient client;
	private final CubeMapRenderer cubeMap;
	private float time;

	public RotatingCubeMapRenderer(CubeMapRenderer cubeMapRenderer) {
		this.cubeMap = cubeMapRenderer;
		this.client = MinecraftClient.getInstance();
	}

	public void render(float f, float g) {
		this.time += f;
		this.cubeMap.draw(this.client, MathHelper.sin(this.time * 0.001F) * 5.0F + 25.0F, -this.time * 0.1F, g);
		this.client.method_22683().method_4493(MinecraftClient.IS_SYSTEM_MAC);
	}
}

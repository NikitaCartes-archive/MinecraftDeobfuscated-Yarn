package net.minecraft.client.render.debug;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

@Environment(EnvType.CLIENT)
public class ChunkLoadingDebugRenderer implements DebugRenderer.Renderer {
	private final MinecraftClient client;
	private double lastUpdateTime = Double.MIN_VALUE;
	private final int field_4511 = 12;

	public ChunkLoadingDebugRenderer(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
	}
}

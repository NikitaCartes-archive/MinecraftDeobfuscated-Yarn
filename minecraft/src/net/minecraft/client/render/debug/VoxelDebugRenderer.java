package net.minecraft.client.render.debug;

import java.util.Collections;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.shape.VoxelShape;

@Environment(EnvType.CLIENT)
public class VoxelDebugRenderer implements DebugRenderer.Renderer {
	private final MinecraftClient field_4540;
	private double field_4541 = Double.MIN_VALUE;
	private List<VoxelShape> field_4542 = Collections.emptyList();

	public VoxelDebugRenderer(MinecraftClient minecraftClient) {
		this.field_4540 = minecraftClient;
	}
}

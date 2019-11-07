package net.minecraft.client.render.debug;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Util;
import net.minecraft.util.shape.VoxelShape;

@Environment(EnvType.CLIENT)
public class CollisionDebugRenderer implements DebugRenderer.Renderer {
	private final MinecraftClient client;
	private double lastUpdateTime = Double.MIN_VALUE;
	private List<VoxelShape> collisions = Collections.emptyList();

	public CollisionDebugRenderer(MinecraftClient client) {
		this.client = client;
	}

	@Override
	public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, double d, double e, double f, long l) {
		double g = (double)Util.getMeasuringTimeNano();
		if (g - this.lastUpdateTime > 1.0E8) {
			this.lastUpdateTime = g;
			Entity entity = this.client.gameRenderer.getCamera().getFocusedEntity();
			this.collisions = (List<VoxelShape>)entity.world
				.getCollisions(entity, entity.getBoundingBox().expand(6.0), Collections.emptySet())
				.collect(Collectors.toList());
		}

		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getLines());

		for (VoxelShape voxelShape : this.collisions) {
			WorldRenderer.method_22983(matrixStack, vertexConsumer, voxelShape, -d, -e, -f, 1.0F, 1.0F, 1.0F, 1.0F);
		}
	}
}

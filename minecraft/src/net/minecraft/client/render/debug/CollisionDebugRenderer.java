package net.minecraft.client.render.debug;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
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
	public void render(long limitTime) {
		Camera camera = this.client.gameRenderer.getCamera();
		double d = (double)Util.getMeasuringTimeNano();
		if (d - this.lastUpdateTime > 1.0E8) {
			this.lastUpdateTime = d;
			this.collisions = (List<VoxelShape>)camera.getFocusedEntity()
				.world
				.getCollisions(camera.getFocusedEntity(), camera.getFocusedEntity().getBoundingBox().expand(6.0), Collections.emptySet())
				.collect(Collectors.toList());
		}

		double e = camera.getPos().x;
		double f = camera.getPos().y;
		double g = camera.getPos().z;
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.lineWidth(2.0F);
		RenderSystem.disableTexture();
		RenderSystem.depthMask(false);
		VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
		VertexConsumer vertexConsumer = immediate.getBuffer(RenderLayer.getLines());
		MatrixStack matrixStack = new MatrixStack();

		for (VoxelShape voxelShape : this.collisions) {
			WorldRenderer.method_22983(matrixStack, vertexConsumer, voxelShape, -e, -f, -g, 1.0F, 1.0F, 1.0F, 1.0F);
		}

		immediate.draw();
		RenderSystem.depthMask(true);
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
	}
}

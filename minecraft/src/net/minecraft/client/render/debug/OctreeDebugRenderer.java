package net.minecraft.client.render.debug;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.render.chunk.Octree;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Colors;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.mutable.MutableInt;

@Environment(EnvType.CLIENT)
public class OctreeDebugRenderer {
	private final MinecraftClient client;

	public OctreeDebugRenderer(MinecraftClient client) {
		this.client = client;
	}

	public void render(MatrixStack matrices, Frustum frustum, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
		Octree octree = this.client.worldRenderer.getChunkRenderingDataPreparer().method_62925();
		MutableInt mutableInt = new MutableInt(0);
		octree.visit(
			(node, skipVisibilityCheck, depth) -> this.renderNode(node, matrices, vertexConsumers, cameraX, cameraY, cameraZ, depth, skipVisibilityCheck, mutableInt),
			frustum
		);
	}

	private void renderNode(
		Octree.Node node,
		MatrixStack matrices,
		VertexConsumerProvider vertexConsumers,
		double cameraX,
		double cameraY,
		double cameraZ,
		int depth,
		boolean skipVisibilityCheck,
		MutableInt id
	) {
		Box box = node.getBoundingBox();
		double d = box.getLengthX();
		long l = Math.round(d / 16.0);
		if (l == 1L) {
			id.add(1);
			double e = box.getCenter().x;
			double f = box.getCenter().y;
			double g = box.getCenter().z;
			DebugRenderer.drawString(matrices, vertexConsumers, String.valueOf(id.getValue()), e, f, g, Colors.WHITE, 0.3F);
		}

		VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getLines());
		long m = l + 5L;
		VertexRendering.drawBox(
			matrices,
			vertexConsumer,
			box.contract(0.1 * (double)depth).offset(-cameraX, -cameraY, -cameraZ),
			method_62980(m, 0.3F),
			method_62980(m, 0.8F),
			method_62980(m, 0.5F),
			skipVisibilityCheck ? 0.4F : 1.0F
		);
	}

	private static float method_62980(long l, float f) {
		float g = 0.1F;
		return MathHelper.fractionalPart(f * (float)l) * 0.9F + 0.1F;
	}
}

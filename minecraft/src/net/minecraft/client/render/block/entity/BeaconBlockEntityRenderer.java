package net.minecraft.client.render.block.entity;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class BeaconBlockEntityRenderer implements BlockEntityRenderer<BeaconBlockEntity> {
	public static final Identifier BEAM_TEXTURE = Identifier.method_60656("textures/entity/beacon_beam.png");
	public static final int MAX_BEAM_HEIGHT = 1024;

	public BeaconBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
	}

	public void render(BeaconBlockEntity beaconBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
		long l = beaconBlockEntity.getWorld().getTime();
		List<BeaconBlockEntity.BeamSegment> list = beaconBlockEntity.getBeamSegments();
		int k = 0;

		for (int m = 0; m < list.size(); m++) {
			BeaconBlockEntity.BeamSegment beamSegment = (BeaconBlockEntity.BeamSegment)list.get(m);
			renderBeam(matrixStack, vertexConsumerProvider, f, l, k, m == list.size() - 1 ? 1024 : beamSegment.getHeight(), beamSegment.getColor());
			k += beamSegment.getHeight();
		}
	}

	private static void renderBeam(MatrixStack matrices, VertexConsumerProvider vertexConsumers, float tickDelta, long worldTime, int yOffset, int maxY, int i) {
		renderBeam(matrices, vertexConsumers, BEAM_TEXTURE, tickDelta, 1.0F, worldTime, yOffset, maxY, i, 0.2F, 0.25F);
	}

	public static void renderBeam(
		MatrixStack matrices,
		VertexConsumerProvider vertexConsumers,
		Identifier textureId,
		float tickDelta,
		float heightScale,
		long worldTime,
		int yOffset,
		int maxY,
		int i,
		float innerRadius,
		float outerRadius
	) {
		int j = yOffset + maxY;
		matrices.push();
		matrices.translate(0.5, 0.0, 0.5);
		float f = (float)Math.floorMod(worldTime, 40) + tickDelta;
		float g = maxY < 0 ? f : -f;
		float h = MathHelper.fractionalPart(g * 0.2F - (float)MathHelper.floor(g * 0.1F));
		matrices.push();
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(f * 2.25F - 45.0F));
		float k = 0.0F;
		float n = 0.0F;
		float o = -innerRadius;
		float p = 0.0F;
		float q = 0.0F;
		float r = -innerRadius;
		float s = 0.0F;
		float t = 1.0F;
		float u = -1.0F + h;
		float v = (float)maxY * heightScale * (0.5F / innerRadius) + u;
		renderBeamLayer(
			matrices,
			vertexConsumers.getBuffer(RenderLayer.getBeaconBeam(textureId, false)),
			i,
			yOffset,
			j,
			0.0F,
			innerRadius,
			innerRadius,
			0.0F,
			o,
			0.0F,
			0.0F,
			r,
			0.0F,
			1.0F,
			v,
			u
		);
		matrices.pop();
		k = -outerRadius;
		float l = -outerRadius;
		n = -outerRadius;
		o = -outerRadius;
		s = 0.0F;
		t = 1.0F;
		u = -1.0F + h;
		v = (float)maxY * heightScale + u;
		renderBeamLayer(
			matrices,
			vertexConsumers.getBuffer(RenderLayer.getBeaconBeam(textureId, true)),
			ColorHelper.Argb.withAlpha(32, i),
			yOffset,
			j,
			k,
			l,
			outerRadius,
			n,
			o,
			outerRadius,
			outerRadius,
			outerRadius,
			0.0F,
			1.0F,
			v,
			u
		);
		matrices.pop();
	}

	private static void renderBeamLayer(
		MatrixStack matrices,
		VertexConsumer vertices,
		int i,
		int j,
		int k,
		float alpha,
		float f,
		float g,
		float x1,
		float h,
		float l,
		float z2,
		float m,
		float n,
		float o,
		float p,
		float q
	) {
		MatrixStack.Entry entry = matrices.peek();
		renderBeamFace(entry, vertices, i, j, k, alpha, f, g, x1, n, o, p, q);
		renderBeamFace(entry, vertices, i, j, k, z2, m, h, l, n, o, p, q);
		renderBeamFace(entry, vertices, i, j, k, g, x1, z2, m, n, o, p, q);
		renderBeamFace(entry, vertices, i, j, k, h, l, alpha, f, n, o, p, q);
	}

	private static void renderBeamFace(
		MatrixStack.Entry matrix, VertexConsumer vertices, int i, int j, int k, float alpha, float f, float g, float h, float l, float m, float n, float o
	) {
		renderBeamVertex(matrix, vertices, i, k, alpha, f, m, n);
		renderBeamVertex(matrix, vertices, i, j, alpha, f, m, o);
		renderBeamVertex(matrix, vertices, i, j, g, h, l, o);
		renderBeamVertex(matrix, vertices, i, k, g, h, l, n);
	}

	private static void renderBeamVertex(MatrixStack.Entry matrix, VertexConsumer vertices, int i, int j, float blue, float alpha, float f, float g) {
		vertices.vertex(matrix, blue, (float)j, alpha)
			.color(i)
			.texture(f, g)
			.overlay(OverlayTexture.DEFAULT_UV)
			.method_60803(15728880)
			.method_60831(matrix, 0.0F, 1.0F, 0.0F);
	}

	public boolean rendersOutsideBoundingBox(BeaconBlockEntity beaconBlockEntity) {
		return true;
	}

	@Override
	public int getRenderDistance() {
		return 256;
	}

	public boolean isInRenderDistance(BeaconBlockEntity beaconBlockEntity, Vec3d vec3d) {
		return Vec3d.ofCenter(beaconBlockEntity.getPos()).multiply(1.0, 0.0, 1.0).isInRange(vec3d.multiply(1.0, 0.0, 1.0), (double)this.getRenderDistance());
	}
}

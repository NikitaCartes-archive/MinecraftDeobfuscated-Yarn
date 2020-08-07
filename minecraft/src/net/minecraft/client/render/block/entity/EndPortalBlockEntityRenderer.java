package net.minecraft.client.render.block.entity;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.EndPortalBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;

@Environment(EnvType.CLIENT)
public class EndPortalBlockEntityRenderer<T extends EndPortalBlockEntity> extends BlockEntityRenderer<T> {
	public static final Identifier SKY_TEXTURE = new Identifier("textures/environment/end_sky.png");
	public static final Identifier PORTAL_TEXTURE = new Identifier("textures/entity/end_portal.png");
	private static final Random RANDOM = new Random(31100L);
	private static final List<RenderLayer> field_21732 = (List<RenderLayer>)IntStream.range(0, 16)
		.mapToObj(i -> RenderLayer.getEndPortal(i + 1))
		.collect(ImmutableList.toImmutableList());

	public EndPortalBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
		super(blockEntityRenderDispatcher);
	}

	public void method_3591(T endPortalBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
		RANDOM.setSeed(31100L);
		double d = endPortalBlockEntity.getPos().getSquaredDistance(this.dispatcher.camera.getPos(), true);
		int k = this.method_3592(d);
		float g = this.method_3594();
		Matrix4f matrix4f = matrixStack.peek().getModel();
		this.method_23084(endPortalBlockEntity, g, 0.15F, matrix4f, vertexConsumerProvider.getBuffer((RenderLayer)field_21732.get(0)));

		for (int l = 1; l < k; l++) {
			this.method_23084(endPortalBlockEntity, g, 2.0F / (float)(18 - l), matrix4f, vertexConsumerProvider.getBuffer((RenderLayer)field_21732.get(l)));
		}
	}

	private void method_23084(T endPortalBlockEntity, float f, float g, Matrix4f matrix4f, VertexConsumer vertexConsumer) {
		float h = (RANDOM.nextFloat() * 0.5F + 0.1F) * g;
		float i = (RANDOM.nextFloat() * 0.5F + 0.4F) * g;
		float j = (RANDOM.nextFloat() * 0.5F + 0.5F) * g;
		this.method_23085(endPortalBlockEntity, matrix4f, vertexConsumer, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, h, i, j, Direction.field_11035);
		this.method_23085(endPortalBlockEntity, matrix4f, vertexConsumer, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, h, i, j, Direction.field_11043);
		this.method_23085(endPortalBlockEntity, matrix4f, vertexConsumer, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, h, i, j, Direction.field_11034);
		this.method_23085(endPortalBlockEntity, matrix4f, vertexConsumer, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, h, i, j, Direction.field_11039);
		this.method_23085(endPortalBlockEntity, matrix4f, vertexConsumer, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, h, i, j, Direction.field_11033);
		this.method_23085(endPortalBlockEntity, matrix4f, vertexConsumer, 0.0F, 1.0F, f, f, 1.0F, 1.0F, 0.0F, 0.0F, h, i, j, Direction.field_11036);
	}

	private void method_23085(
		T endPortalBlockEntity,
		Matrix4f matrix4f,
		VertexConsumer vertexConsumer,
		float f,
		float g,
		float h,
		float i,
		float j,
		float k,
		float l,
		float m,
		float n,
		float o,
		float p,
		Direction direction
	) {
		if (endPortalBlockEntity.shouldDrawSide(direction)) {
			vertexConsumer.vertex(matrix4f, f, h, j).color(n, o, p, 1.0F).next();
			vertexConsumer.vertex(matrix4f, g, h, k).color(n, o, p, 1.0F).next();
			vertexConsumer.vertex(matrix4f, g, i, l).color(n, o, p, 1.0F).next();
			vertexConsumer.vertex(matrix4f, f, i, m).color(n, o, p, 1.0F).next();
		}
	}

	protected int method_3592(double d) {
		if (d > 36864.0) {
			return 1;
		} else if (d > 25600.0) {
			return 3;
		} else if (d > 16384.0) {
			return 5;
		} else if (d > 9216.0) {
			return 7;
		} else if (d > 4096.0) {
			return 9;
		} else if (d > 1024.0) {
			return 11;
		} else if (d > 576.0) {
			return 13;
		} else {
			return d > 256.0 ? 14 : 15;
		}
	}

	protected float method_3594() {
		return 0.75F;
	}
}

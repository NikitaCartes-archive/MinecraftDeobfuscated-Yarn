package net.minecraft.client.render.block.entity;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.EndPortalBlockEntity;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class EndPortalBlockEntityRenderer<T extends EndPortalBlockEntity> extends BlockEntityRenderer<T> {
	public static final Identifier SKY_TEX = new Identifier("textures/environment/end_sky.png");
	public static final Identifier PORTAL_TEX = new Identifier("textures/entity/end_portal.png");
	private static final Random RANDOM = new Random(31100L);

	public EndPortalBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
		super(blockEntityRenderDispatcher);
	}

	public void method_3591(
		T endPortalBlockEntity,
		double d,
		double e,
		double f,
		float g,
		MatrixStack matrixStack,
		LayeredVertexConsumerStorage layeredVertexConsumerStorage,
		int i,
		int j
	) {
		RANDOM.setSeed(31100L);
		double h = d * d + e * e + f * f;
		int k = this.method_3592(h);
		float l = this.method_3594();
		Matrix4f matrix4f = matrixStack.peek();
		this.method_23084(endPortalBlockEntity, l, 0.15F, matrix4f, layeredVertexConsumerStorage.getBuffer(RenderLayer.getEndPortal(1)));

		for (int m = 1; m < k; m++) {
			this.method_23084(endPortalBlockEntity, l, 2.0F / (float)(18 - m), matrix4f, layeredVertexConsumerStorage.getBuffer(RenderLayer.getEndPortal(m + 1)));
		}
	}

	private void method_23084(T endPortalBlockEntity, float f, float g, Matrix4f matrix4f, VertexConsumer vertexConsumer) {
		float h = (RANDOM.nextFloat() * 0.5F + 0.1F) * g;
		float i = (RANDOM.nextFloat() * 0.5F + 0.4F) * g;
		float j = (RANDOM.nextFloat() * 0.5F + 0.5F) * g;
		this.method_23085(endPortalBlockEntity, matrix4f, vertexConsumer, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, h, i, j, Direction.SOUTH);
		this.method_23085(endPortalBlockEntity, matrix4f, vertexConsumer, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, h, i, j, Direction.NORTH);
		this.method_23085(endPortalBlockEntity, matrix4f, vertexConsumer, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, h, i, j, Direction.EAST);
		this.method_23085(endPortalBlockEntity, matrix4f, vertexConsumer, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, h, i, j, Direction.WEST);
		this.method_23085(endPortalBlockEntity, matrix4f, vertexConsumer, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, h, i, j, Direction.DOWN);
		this.method_23085(endPortalBlockEntity, matrix4f, vertexConsumer, 0.0F, 1.0F, f, f, 1.0F, 1.0F, 0.0F, 0.0F, h, i, j, Direction.UP);
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
		int i;
		if (d > 36864.0) {
			i = 1;
		} else if (d > 25600.0) {
			i = 3;
		} else if (d > 16384.0) {
			i = 5;
		} else if (d > 9216.0) {
			i = 7;
		} else if (d > 4096.0) {
			i = 9;
		} else if (d > 1024.0) {
			i = 11;
		} else if (d > 576.0) {
			i = 13;
		} else if (d > 256.0) {
			i = 14;
		} else {
			i = 15;
		}

		return i;
	}

	protected float method_3594() {
		return 0.75F;
	}
}

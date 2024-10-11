package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.entity.state.PaintingEntityRenderState;
import net.minecraft.client.texture.PaintingManager;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.util.Colors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class PaintingEntityRenderer extends EntityRenderer<PaintingEntity, PaintingEntityRenderState> {
	public PaintingEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
	}

	public void render(PaintingEntityRenderState paintingEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		PaintingVariant paintingVariant = paintingEntityRenderState.variant;
		if (paintingVariant != null) {
			matrixStack.push();
			matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)(180 - paintingEntityRenderState.facing.getHorizontal() * 90)));
			PaintingManager paintingManager = MinecraftClient.getInstance().getPaintingManager();
			Sprite sprite = paintingManager.getBackSprite();
			VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntitySolidZOffsetForward(sprite.getAtlasId()));
			this.renderPainting(
				matrixStack,
				vertexConsumer,
				paintingEntityRenderState.lightmapCoordinates,
				paintingVariant.width(),
				paintingVariant.height(),
				paintingManager.getPaintingSprite(paintingVariant),
				sprite
			);
			matrixStack.pop();
			super.render(paintingEntityRenderState, matrixStack, vertexConsumerProvider, i);
		}
	}

	public PaintingEntityRenderState createRenderState() {
		return new PaintingEntityRenderState();
	}

	public void updateRenderState(PaintingEntity paintingEntity, PaintingEntityRenderState paintingEntityRenderState, float f) {
		super.updateRenderState(paintingEntity, paintingEntityRenderState, f);
		Direction direction = paintingEntity.getHorizontalFacing();
		PaintingVariant paintingVariant = paintingEntity.getVariant().value();
		paintingEntityRenderState.facing = direction;
		paintingEntityRenderState.variant = paintingVariant;
		int i = paintingVariant.width();
		int j = paintingVariant.height();
		if (paintingEntityRenderState.lightmapCoordinates.length != i * j) {
			paintingEntityRenderState.lightmapCoordinates = new int[i * j];
		}

		float g = (float)(-i) / 2.0F;
		float h = (float)(-j) / 2.0F;
		World world = paintingEntity.getWorld();

		for (int k = 0; k < j; k++) {
			for (int l = 0; l < i; l++) {
				float m = (float)l + g + 0.5F;
				float n = (float)k + h + 0.5F;
				int o = paintingEntity.getBlockX();
				int p = MathHelper.floor(paintingEntity.getY() + (double)n);
				int q = paintingEntity.getBlockZ();
				switch (direction) {
					case NORTH:
						o = MathHelper.floor(paintingEntity.getX() + (double)m);
						break;
					case WEST:
						q = MathHelper.floor(paintingEntity.getZ() - (double)m);
						break;
					case SOUTH:
						o = MathHelper.floor(paintingEntity.getX() - (double)m);
						break;
					case EAST:
						q = MathHelper.floor(paintingEntity.getZ() + (double)m);
				}

				paintingEntityRenderState.lightmapCoordinates[l + k * i] = WorldRenderer.getLightmapCoordinates(world, new BlockPos(o, p, q));
			}
		}
	}

	private void renderPainting(
		MatrixStack matrices, VertexConsumer vertexConsumer, int[] lightmapCoordinates, int width, int height, Sprite paintingSprite, Sprite backSprite
	) {
		MatrixStack.Entry entry = matrices.peek();
		float f = (float)(-width) / 2.0F;
		float g = (float)(-height) / 2.0F;
		float h = 0.03125F;
		float i = backSprite.getMinU();
		float j = backSprite.getMaxU();
		float k = backSprite.getMinV();
		float l = backSprite.getMaxV();
		float m = backSprite.getMinU();
		float n = backSprite.getMaxU();
		float o = backSprite.getMinV();
		float p = backSprite.getFrameV(0.0625F);
		float q = backSprite.getMinU();
		float r = backSprite.getFrameU(0.0625F);
		float s = backSprite.getMinV();
		float t = backSprite.getMaxV();
		double d = 1.0 / (double)width;
		double e = 1.0 / (double)height;

		for (int u = 0; u < width; u++) {
			for (int v = 0; v < height; v++) {
				float w = f + (float)(u + 1);
				float x = f + (float)u;
				float y = g + (float)(v + 1);
				float z = g + (float)v;
				int aa = lightmapCoordinates[u + v * width];
				float ab = paintingSprite.getFrameU((float)(d * (double)(width - u)));
				float ac = paintingSprite.getFrameU((float)(d * (double)(width - (u + 1))));
				float ad = paintingSprite.getFrameV((float)(e * (double)(height - v)));
				float ae = paintingSprite.getFrameV((float)(e * (double)(height - (v + 1))));
				this.vertex(entry, vertexConsumer, w, z, ac, ad, -0.03125F, 0, 0, -1, aa);
				this.vertex(entry, vertexConsumer, x, z, ab, ad, -0.03125F, 0, 0, -1, aa);
				this.vertex(entry, vertexConsumer, x, y, ab, ae, -0.03125F, 0, 0, -1, aa);
				this.vertex(entry, vertexConsumer, w, y, ac, ae, -0.03125F, 0, 0, -1, aa);
				this.vertex(entry, vertexConsumer, w, y, j, k, 0.03125F, 0, 0, 1, aa);
				this.vertex(entry, vertexConsumer, x, y, i, k, 0.03125F, 0, 0, 1, aa);
				this.vertex(entry, vertexConsumer, x, z, i, l, 0.03125F, 0, 0, 1, aa);
				this.vertex(entry, vertexConsumer, w, z, j, l, 0.03125F, 0, 0, 1, aa);
				this.vertex(entry, vertexConsumer, w, y, m, o, -0.03125F, 0, 1, 0, aa);
				this.vertex(entry, vertexConsumer, x, y, n, o, -0.03125F, 0, 1, 0, aa);
				this.vertex(entry, vertexConsumer, x, y, n, p, 0.03125F, 0, 1, 0, aa);
				this.vertex(entry, vertexConsumer, w, y, m, p, 0.03125F, 0, 1, 0, aa);
				this.vertex(entry, vertexConsumer, w, z, m, o, 0.03125F, 0, -1, 0, aa);
				this.vertex(entry, vertexConsumer, x, z, n, o, 0.03125F, 0, -1, 0, aa);
				this.vertex(entry, vertexConsumer, x, z, n, p, -0.03125F, 0, -1, 0, aa);
				this.vertex(entry, vertexConsumer, w, z, m, p, -0.03125F, 0, -1, 0, aa);
				this.vertex(entry, vertexConsumer, w, y, r, s, 0.03125F, -1, 0, 0, aa);
				this.vertex(entry, vertexConsumer, w, z, r, t, 0.03125F, -1, 0, 0, aa);
				this.vertex(entry, vertexConsumer, w, z, q, t, -0.03125F, -1, 0, 0, aa);
				this.vertex(entry, vertexConsumer, w, y, q, s, -0.03125F, -1, 0, 0, aa);
				this.vertex(entry, vertexConsumer, x, y, r, s, -0.03125F, 1, 0, 0, aa);
				this.vertex(entry, vertexConsumer, x, z, r, t, -0.03125F, 1, 0, 0, aa);
				this.vertex(entry, vertexConsumer, x, z, q, t, 0.03125F, 1, 0, 0, aa);
				this.vertex(entry, vertexConsumer, x, y, q, s, 0.03125F, 1, 0, 0, aa);
			}
		}
	}

	private void vertex(
		MatrixStack.Entry matrix, VertexConsumer vertexConsumer, float x, float y, float u, float v, float z, int normalX, int normalY, int normalZ, int light
	) {
		vertexConsumer.vertex(matrix, x, y, z)
			.color(Colors.WHITE)
			.texture(u, v)
			.overlay(OverlayTexture.DEFAULT_UV)
			.light(light)
			.normal(matrix, (float)normalX, (float)normalY, (float)normalZ);
	}
}

package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.texture.PaintingManager;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class PaintingEntityRenderer extends EntityRenderer<PaintingEntity> {
	public PaintingEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
	}

	public void render(PaintingEntity paintingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		matrixStack.push();
		matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F - f));
		PaintingVariant paintingVariant = paintingEntity.getVariant().value();
		float h = 0.0625F;
		matrixStack.scale(0.0625F, 0.0625F, 0.0625F);
		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntitySolid(this.getTexture(paintingEntity)));
		PaintingManager paintingManager = MinecraftClient.getInstance().getPaintingManager();
		this.renderPainting(
			matrixStack,
			vertexConsumer,
			paintingEntity,
			paintingVariant.getWidth(),
			paintingVariant.getHeight(),
			paintingManager.getPaintingSprite(paintingVariant),
			paintingManager.getBackSprite()
		);
		matrixStack.pop();
		super.render(paintingEntity, f, g, matrixStack, vertexConsumerProvider, i);
	}

	public Identifier getTexture(PaintingEntity paintingEntity) {
		return MinecraftClient.getInstance().getPaintingManager().getBackSprite().getAtlasId();
	}

	private void renderPainting(
		MatrixStack matrices, VertexConsumer vertexConsumer, PaintingEntity entity, int width, int height, Sprite paintingSprite, Sprite backSprite
	) {
		MatrixStack.Entry entry = matrices.peek();
		float f = (float)(-width) / 2.0F;
		float g = (float)(-height) / 2.0F;
		float h = 0.5F;
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
		int u = width / 16;
		int v = height / 16;
		double d = 1.0 / (double)u;
		double e = 1.0 / (double)v;

		for(int w = 0; w < u; ++w) {
			for(int x = 0; x < v; ++x) {
				float y = f + (float)((w + 1) * 16);
				float z = f + (float)(w * 16);
				float aa = g + (float)((x + 1) * 16);
				float ab = g + (float)(x * 16);
				int ac = entity.getBlockX();
				int ad = MathHelper.floor(entity.getY() + (double)((aa + ab) / 2.0F / 16.0F));
				int ae = entity.getBlockZ();
				Direction direction = entity.getHorizontalFacing();
				if (direction == Direction.NORTH) {
					ac = MathHelper.floor(entity.getX() + (double)((y + z) / 2.0F / 16.0F));
				}

				if (direction == Direction.WEST) {
					ae = MathHelper.floor(entity.getZ() - (double)((y + z) / 2.0F / 16.0F));
				}

				if (direction == Direction.SOUTH) {
					ac = MathHelper.floor(entity.getX() - (double)((y + z) / 2.0F / 16.0F));
				}

				if (direction == Direction.EAST) {
					ae = MathHelper.floor(entity.getZ() + (double)((y + z) / 2.0F / 16.0F));
				}

				int af = WorldRenderer.getLightmapCoordinates(entity.getWorld(), new BlockPos(ac, ad, ae));
				float ag = paintingSprite.getFrameU((float)(d * (double)(u - w)));
				float ah = paintingSprite.getFrameU((float)(d * (double)(u - (w + 1))));
				float ai = paintingSprite.getFrameV((float)(e * (double)(v - x)));
				float aj = paintingSprite.getFrameV((float)(e * (double)(v - (x + 1))));
				this.vertex(entry, vertexConsumer, y, ab, ah, ai, -0.5F, 0, 0, -1, af);
				this.vertex(entry, vertexConsumer, z, ab, ag, ai, -0.5F, 0, 0, -1, af);
				this.vertex(entry, vertexConsumer, z, aa, ag, aj, -0.5F, 0, 0, -1, af);
				this.vertex(entry, vertexConsumer, y, aa, ah, aj, -0.5F, 0, 0, -1, af);
				this.vertex(entry, vertexConsumer, y, aa, j, k, 0.5F, 0, 0, 1, af);
				this.vertex(entry, vertexConsumer, z, aa, i, k, 0.5F, 0, 0, 1, af);
				this.vertex(entry, vertexConsumer, z, ab, i, l, 0.5F, 0, 0, 1, af);
				this.vertex(entry, vertexConsumer, y, ab, j, l, 0.5F, 0, 0, 1, af);
				this.vertex(entry, vertexConsumer, y, aa, m, o, -0.5F, 0, 1, 0, af);
				this.vertex(entry, vertexConsumer, z, aa, n, o, -0.5F, 0, 1, 0, af);
				this.vertex(entry, vertexConsumer, z, aa, n, p, 0.5F, 0, 1, 0, af);
				this.vertex(entry, vertexConsumer, y, aa, m, p, 0.5F, 0, 1, 0, af);
				this.vertex(entry, vertexConsumer, y, ab, m, o, 0.5F, 0, -1, 0, af);
				this.vertex(entry, vertexConsumer, z, ab, n, o, 0.5F, 0, -1, 0, af);
				this.vertex(entry, vertexConsumer, z, ab, n, p, -0.5F, 0, -1, 0, af);
				this.vertex(entry, vertexConsumer, y, ab, m, p, -0.5F, 0, -1, 0, af);
				this.vertex(entry, vertexConsumer, y, aa, r, s, 0.5F, -1, 0, 0, af);
				this.vertex(entry, vertexConsumer, y, ab, r, t, 0.5F, -1, 0, 0, af);
				this.vertex(entry, vertexConsumer, y, ab, q, t, -0.5F, -1, 0, 0, af);
				this.vertex(entry, vertexConsumer, y, aa, q, s, -0.5F, -1, 0, 0, af);
				this.vertex(entry, vertexConsumer, z, aa, r, s, -0.5F, 1, 0, 0, af);
				this.vertex(entry, vertexConsumer, z, ab, r, t, -0.5F, 1, 0, 0, af);
				this.vertex(entry, vertexConsumer, z, ab, q, t, 0.5F, 1, 0, 0, af);
				this.vertex(entry, vertexConsumer, z, aa, q, s, 0.5F, 1, 0, 0, af);
			}
		}
	}

	private void vertex(
		MatrixStack.Entry matrix, VertexConsumer vertexConsumer, float x, float y, float u, float v, float z, int normalX, int normalY, int normalZ, int light
	) {
		vertexConsumer.vertex(matrix, x, y, z)
			.color(255, 255, 255, 255)
			.texture(u, v)
			.overlay(OverlayTexture.DEFAULT_UV)
			.light(light)
			.normal(matrix, (float)normalX, (float)normalY, (float)normalZ)
			.next();
	}
}

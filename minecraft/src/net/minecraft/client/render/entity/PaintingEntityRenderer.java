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
import net.minecraft.util.Colors;
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
		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntitySolid(this.getTexture(paintingEntity)));
		PaintingManager paintingManager = MinecraftClient.getInstance().getPaintingManager();
		this.renderPainting(
			matrixStack,
			vertexConsumer,
			paintingEntity,
			paintingVariant.width(),
			paintingVariant.height(),
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
				int aa = entity.getBlockX();
				int ab = MathHelper.floor(entity.getY() + (double)((y + z) / 2.0F));
				int ac = entity.getBlockZ();
				Direction direction = entity.getHorizontalFacing();
				if (direction == Direction.NORTH) {
					aa = MathHelper.floor(entity.getX() + (double)((w + x) / 2.0F));
				}

				if (direction == Direction.WEST) {
					ac = MathHelper.floor(entity.getZ() - (double)((w + x) / 2.0F));
				}

				if (direction == Direction.SOUTH) {
					aa = MathHelper.floor(entity.getX() - (double)((w + x) / 2.0F));
				}

				if (direction == Direction.EAST) {
					ac = MathHelper.floor(entity.getZ() + (double)((w + x) / 2.0F));
				}

				int ad = WorldRenderer.getLightmapCoordinates(entity.getWorld(), new BlockPos(aa, ab, ac));
				float ae = paintingSprite.getFrameU((float)(d * (double)(width - u)));
				float af = paintingSprite.getFrameU((float)(d * (double)(width - (u + 1))));
				float ag = paintingSprite.getFrameV((float)(e * (double)(height - v)));
				float ah = paintingSprite.getFrameV((float)(e * (double)(height - (v + 1))));
				this.vertex(entry, vertexConsumer, w, z, af, ag, -0.03125F, 0, 0, -1, ad);
				this.vertex(entry, vertexConsumer, x, z, ae, ag, -0.03125F, 0, 0, -1, ad);
				this.vertex(entry, vertexConsumer, x, y, ae, ah, -0.03125F, 0, 0, -1, ad);
				this.vertex(entry, vertexConsumer, w, y, af, ah, -0.03125F, 0, 0, -1, ad);
				this.vertex(entry, vertexConsumer, w, y, j, k, 0.03125F, 0, 0, 1, ad);
				this.vertex(entry, vertexConsumer, x, y, i, k, 0.03125F, 0, 0, 1, ad);
				this.vertex(entry, vertexConsumer, x, z, i, l, 0.03125F, 0, 0, 1, ad);
				this.vertex(entry, vertexConsumer, w, z, j, l, 0.03125F, 0, 0, 1, ad);
				this.vertex(entry, vertexConsumer, w, y, m, o, -0.03125F, 0, 1, 0, ad);
				this.vertex(entry, vertexConsumer, x, y, n, o, -0.03125F, 0, 1, 0, ad);
				this.vertex(entry, vertexConsumer, x, y, n, p, 0.03125F, 0, 1, 0, ad);
				this.vertex(entry, vertexConsumer, w, y, m, p, 0.03125F, 0, 1, 0, ad);
				this.vertex(entry, vertexConsumer, w, z, m, o, 0.03125F, 0, -1, 0, ad);
				this.vertex(entry, vertexConsumer, x, z, n, o, 0.03125F, 0, -1, 0, ad);
				this.vertex(entry, vertexConsumer, x, z, n, p, -0.03125F, 0, -1, 0, ad);
				this.vertex(entry, vertexConsumer, w, z, m, p, -0.03125F, 0, -1, 0, ad);
				this.vertex(entry, vertexConsumer, w, y, r, s, 0.03125F, -1, 0, 0, ad);
				this.vertex(entry, vertexConsumer, w, z, r, t, 0.03125F, -1, 0, 0, ad);
				this.vertex(entry, vertexConsumer, w, z, q, t, -0.03125F, -1, 0, 0, ad);
				this.vertex(entry, vertexConsumer, w, y, q, s, -0.03125F, -1, 0, 0, ad);
				this.vertex(entry, vertexConsumer, x, y, r, s, -0.03125F, 1, 0, 0, ad);
				this.vertex(entry, vertexConsumer, x, z, r, t, -0.03125F, 1, 0, 0, ad);
				this.vertex(entry, vertexConsumer, x, z, q, t, 0.03125F, 1, 0, 0, ad);
				this.vertex(entry, vertexConsumer, x, y, q, s, 0.03125F, 1, 0, 0, ad);
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

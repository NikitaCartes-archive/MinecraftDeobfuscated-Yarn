package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.PaintingManager;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.decoration.painting.PaintingMotive;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class PaintingEntityRenderer extends EntityRenderer<PaintingEntity> {
	public PaintingEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	public void method_4075(
		PaintingEntity paintingEntity,
		double d,
		double e,
		double f,
		float g,
		float h,
		MatrixStack matrixStack,
		LayeredVertexConsumerStorage layeredVertexConsumerStorage
	) {
		matrixStack.push();
		matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(180.0F - g, true));
		PaintingMotive paintingMotive = paintingEntity.motive;
		float i = 0.0625F;
		matrixStack.scale(0.0625F, 0.0625F, 0.0625F);
		VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(RenderLayer.method_23017(this.method_4077(paintingEntity)));
		OverlayTexture.clearDefaultOverlay(vertexConsumer);
		PaintingManager paintingManager = MinecraftClient.getInstance().getPaintingManager();
		this.method_4074(
			matrixStack.peek(),
			vertexConsumer,
			paintingEntity,
			paintingMotive.getWidth(),
			paintingMotive.getHeight(),
			paintingManager.getPaintingSprite(paintingMotive),
			paintingManager.getBackSprite()
		);
		vertexConsumer.clearDefaultOverlay();
		matrixStack.pop();
		super.render(paintingEntity, d, e, f, g, h, matrixStack, layeredVertexConsumerStorage);
	}

	public Identifier method_4077(PaintingEntity paintingEntity) {
		return SpriteAtlasTexture.PAINTING_ATLAS_TEX;
	}

	private void method_4074(Matrix4f matrix4f, VertexConsumer vertexConsumer, PaintingEntity paintingEntity, int i, int j, Sprite sprite, Sprite sprite2) {
		float f = (float)(-i) / 2.0F;
		float g = (float)(-j) / 2.0F;
		float h = 0.5F;
		float k = sprite2.getMinU();
		float l = sprite2.getMaxU();
		float m = sprite2.getMinV();
		float n = sprite2.getMaxV();
		float o = sprite2.getMinU();
		float p = sprite2.getMaxU();
		float q = sprite2.getMinV();
		float r = sprite2.getV(1.0);
		float s = sprite2.getMinU();
		float t = sprite2.getU(1.0);
		float u = sprite2.getMinV();
		float v = sprite2.getMaxV();
		int w = i / 16;
		int x = j / 16;
		double d = 16.0 / (double)w;
		double e = 16.0 / (double)x;

		for (int y = 0; y < w; y++) {
			for (int z = 0; z < x; z++) {
				float aa = f + (float)((y + 1) * 16);
				float ab = f + (float)(y * 16);
				float ac = g + (float)((z + 1) * 16);
				float ad = g + (float)(z * 16);
				int ae = MathHelper.floor(paintingEntity.x);
				int af = MathHelper.floor(paintingEntity.y + (double)((ac + ad) / 2.0F / 16.0F));
				int ag = MathHelper.floor(paintingEntity.z);
				Direction direction = paintingEntity.getHorizontalFacing();
				if (direction == Direction.NORTH) {
					ae = MathHelper.floor(paintingEntity.x + (double)((aa + ab) / 2.0F / 16.0F));
				}

				if (direction == Direction.WEST) {
					ag = MathHelper.floor(paintingEntity.z - (double)((aa + ab) / 2.0F / 16.0F));
				}

				if (direction == Direction.SOUTH) {
					ae = MathHelper.floor(paintingEntity.x - (double)((aa + ab) / 2.0F / 16.0F));
				}

				if (direction == Direction.EAST) {
					ag = MathHelper.floor(paintingEntity.z + (double)((aa + ab) / 2.0F / 16.0F));
				}

				int ah = paintingEntity.world.getLightmapCoordinates(new BlockPos(ae, af, ag));
				float ai = sprite.getU(d * (double)(w - y));
				float aj = sprite.getU(d * (double)(w - (y + 1)));
				float ak = sprite.getV(e * (double)(x - z));
				float al = sprite.getV(e * (double)(x - (z + 1)));
				this.method_23188(matrix4f, vertexConsumer, aa, ad, aj, ak, -0.5F, 0, 0, -1, ah);
				this.method_23188(matrix4f, vertexConsumer, ab, ad, ai, ak, -0.5F, 0, 0, -1, ah);
				this.method_23188(matrix4f, vertexConsumer, ab, ac, ai, al, -0.5F, 0, 0, -1, ah);
				this.method_23188(matrix4f, vertexConsumer, aa, ac, aj, al, -0.5F, 0, 0, -1, ah);
				this.method_23188(matrix4f, vertexConsumer, aa, ac, k, m, 0.5F, 0, 0, 1, ah);
				this.method_23188(matrix4f, vertexConsumer, ab, ac, l, m, 0.5F, 0, 0, 1, ah);
				this.method_23188(matrix4f, vertexConsumer, ab, ad, l, n, 0.5F, 0, 0, 1, ah);
				this.method_23188(matrix4f, vertexConsumer, aa, ad, k, n, 0.5F, 0, 0, 1, ah);
				this.method_23188(matrix4f, vertexConsumer, aa, ac, o, q, -0.5F, 0, 1, 0, ah);
				this.method_23188(matrix4f, vertexConsumer, ab, ac, p, q, -0.5F, 0, 1, 0, ah);
				this.method_23188(matrix4f, vertexConsumer, ab, ac, p, r, 0.5F, 0, 1, 0, ah);
				this.method_23188(matrix4f, vertexConsumer, aa, ac, o, r, 0.5F, 0, 1, 0, ah);
				this.method_23188(matrix4f, vertexConsumer, aa, ad, o, q, 0.5F, 0, -1, 0, ah);
				this.method_23188(matrix4f, vertexConsumer, ab, ad, p, q, 0.5F, 0, -1, 0, ah);
				this.method_23188(matrix4f, vertexConsumer, ab, ad, p, r, -0.5F, 0, -1, 0, ah);
				this.method_23188(matrix4f, vertexConsumer, aa, ad, o, r, -0.5F, 0, -1, 0, ah);
				this.method_23188(matrix4f, vertexConsumer, aa, ac, t, u, 0.5F, -1, 0, 0, ah);
				this.method_23188(matrix4f, vertexConsumer, aa, ad, t, v, 0.5F, -1, 0, 0, ah);
				this.method_23188(matrix4f, vertexConsumer, aa, ad, s, v, -0.5F, -1, 0, 0, ah);
				this.method_23188(matrix4f, vertexConsumer, aa, ac, s, u, -0.5F, -1, 0, 0, ah);
				this.method_23188(matrix4f, vertexConsumer, ab, ac, t, u, -0.5F, 1, 0, 0, ah);
				this.method_23188(matrix4f, vertexConsumer, ab, ad, t, v, -0.5F, 1, 0, 0, ah);
				this.method_23188(matrix4f, vertexConsumer, ab, ad, s, v, 0.5F, 1, 0, 0, ah);
				this.method_23188(matrix4f, vertexConsumer, ab, ac, s, u, 0.5F, 1, 0, 0, ah);
			}
		}
	}

	private void method_23188(Matrix4f matrix4f, VertexConsumer vertexConsumer, float f, float g, float h, float i, float j, int k, int l, int m, int n) {
		vertexConsumer.vertex(matrix4f, f, g, j).color(255, 255, 255, 255).texture(h, i).light(n).normal((float)k, (float)l, (float)m).next();
	}
}

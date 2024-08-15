package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.state.LightningEntityRenderState;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LightningEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class LightningEntityRenderer extends EntityRenderer<LightningEntity, LightningEntityRenderState> {
	public LightningEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
	}

	public void render(LightningEntityRenderState lightningEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		float[] fs = new float[8];
		float[] gs = new float[8];
		float f = 0.0F;
		float g = 0.0F;
		Random random = Random.create(lightningEntityRenderState.seed);

		for (int j = 7; j >= 0; j--) {
			fs[j] = f;
			gs[j] = g;
			f += (float)(random.nextInt(11) - 5);
			g += (float)(random.nextInt(11) - 5);
		}

		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getLightning());
		Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();

		for (int k = 0; k < 4; k++) {
			Random random2 = Random.create(lightningEntityRenderState.seed);

			for (int l = 0; l < 3; l++) {
				int m = 7;
				int n = 0;
				if (l > 0) {
					m = 7 - l;
				}

				if (l > 0) {
					n = m - 2;
				}

				float h = fs[m] - f;
				float o = gs[m] - g;

				for (int p = m; p >= n; p--) {
					float q = h;
					float r = o;
					if (l == 0) {
						h += (float)(random2.nextInt(11) - 5);
						o += (float)(random2.nextInt(11) - 5);
					} else {
						h += (float)(random2.nextInt(31) - 15);
						o += (float)(random2.nextInt(31) - 15);
					}

					float s = 0.5F;
					float t = 0.45F;
					float u = 0.45F;
					float v = 0.5F;
					float w = 0.1F + (float)k * 0.2F;
					if (l == 0) {
						w *= (float)p * 0.1F + 1.0F;
					}

					float x = 0.1F + (float)k * 0.2F;
					if (l == 0) {
						x *= ((float)p - 1.0F) * 0.1F + 1.0F;
					}

					drawBranch(matrix4f, vertexConsumer, h, o, p, q, r, 0.45F, 0.45F, 0.5F, w, x, false, false, true, false);
					drawBranch(matrix4f, vertexConsumer, h, o, p, q, r, 0.45F, 0.45F, 0.5F, w, x, true, false, true, true);
					drawBranch(matrix4f, vertexConsumer, h, o, p, q, r, 0.45F, 0.45F, 0.5F, w, x, true, true, false, true);
					drawBranch(matrix4f, vertexConsumer, h, o, p, q, r, 0.45F, 0.45F, 0.5F, w, x, false, true, false, false);
				}
			}
		}
	}

	private static void drawBranch(
		Matrix4f matrix,
		VertexConsumer buffer,
		float x1,
		float z1,
		int y,
		float x2,
		float z2,
		float red,
		float green,
		float blue,
		float offset2,
		float offset1,
		boolean shiftEast1,
		boolean shiftSouth1,
		boolean shiftEast2,
		boolean shiftSouth2
	) {
		buffer.vertex(matrix, x1 + (shiftEast1 ? offset1 : -offset1), (float)(y * 16), z1 + (shiftSouth1 ? offset1 : -offset1)).color(red, green, blue, 0.3F);
		buffer.vertex(matrix, x2 + (shiftEast1 ? offset2 : -offset2), (float)((y + 1) * 16), z2 + (shiftSouth1 ? offset2 : -offset2)).color(red, green, blue, 0.3F);
		buffer.vertex(matrix, x2 + (shiftEast2 ? offset2 : -offset2), (float)((y + 1) * 16), z2 + (shiftSouth2 ? offset2 : -offset2)).color(red, green, blue, 0.3F);
		buffer.vertex(matrix, x1 + (shiftEast2 ? offset1 : -offset1), (float)(y * 16), z1 + (shiftSouth2 ? offset1 : -offset1)).color(red, green, blue, 0.3F);
	}

	public Identifier getTexture(LightningEntityRenderState lightningEntityRenderState) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
	}

	public LightningEntityRenderState getRenderState() {
		return new LightningEntityRenderState();
	}

	public void updateRenderState(LightningEntity lightningEntity, LightningEntityRenderState lightningEntityRenderState, float f) {
		super.updateRenderState(lightningEntity, lightningEntityRenderState, f);
		lightningEntityRenderState.seed = lightningEntity.seed;
	}

	protected boolean canBeCulled(LightningEntity lightningEntity) {
		return false;
	}
}

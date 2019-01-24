package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.decoration.painting.PaintingMotive;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class PaintingEntityRenderer extends EntityRenderer<PaintingEntity> {
	private static final Identifier TEX = new Identifier("textures/painting/paintings_kristoffer_zetterstrand.png");

	public PaintingEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	public void method_4075(PaintingEntity paintingEntity, double d, double e, double f, float g, float h) {
		GlStateManager.pushMatrix();
		GlStateManager.translated(d, e, f);
		GlStateManager.rotatef(180.0F - g, 0.0F, 1.0F, 0.0F);
		GlStateManager.enableRescaleNormal();
		this.bindEntityTexture(paintingEntity);
		PaintingMotive paintingMotive = paintingEntity.motive;
		float i = 0.0625F;
		GlStateManager.scalef(0.0625F, 0.0625F, 0.0625F);
		if (this.renderOutlines) {
			GlStateManager.enableColorMaterial();
			GlStateManager.setupSolidRenderingTextureCombine(this.getOutlineColor(paintingEntity));
		}

		this.method_4074(paintingEntity, paintingMotive.getWidth(), paintingMotive.getHeight(), paintingMotive.getTextureX(), paintingMotive.getTextureY());
		if (this.renderOutlines) {
			GlStateManager.tearDownSolidRenderingTextureCombine();
			GlStateManager.disableColorMaterial();
		}

		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
		super.render(paintingEntity, d, e, f, g, h);
	}

	protected Identifier method_4077(PaintingEntity paintingEntity) {
		return TEX;
	}

	private void method_4074(PaintingEntity paintingEntity, int i, int j, int k, int l) {
		float f = (float)(-i) / 2.0F;
		float g = (float)(-j) / 2.0F;
		float h = 0.5F;
		float m = 0.75F;
		float n = 0.8125F;
		float o = 0.0F;
		float p = 0.0625F;
		float q = 0.75F;
		float r = 0.8125F;
		float s = 0.001953125F;
		float t = 0.001953125F;
		float u = 0.7519531F;
		float v = 0.7519531F;
		float w = 0.0F;
		float x = 0.0625F;

		for (int y = 0; y < i / 16; y++) {
			for (int z = 0; z < j / 16; z++) {
				float aa = f + (float)((y + 1) * 16);
				float ab = f + (float)(y * 16);
				float ac = g + (float)((z + 1) * 16);
				float ad = g + (float)(z * 16);
				this.method_4076(paintingEntity, (aa + ab) / 2.0F, (ac + ad) / 2.0F);
				float ae = (float)(k + i - y * 16) / 256.0F;
				float af = (float)(k + i - (y + 1) * 16) / 256.0F;
				float ag = (float)(l + j - z * 16) / 256.0F;
				float ah = (float)(l + j - (z + 1) * 16) / 256.0F;
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
				bufferBuilder.begin(7, VertexFormats.POSITION_UV_NORMAL);
				bufferBuilder.vertex((double)aa, (double)ad, -0.5).texture((double)af, (double)ag).normal(0.0F, 0.0F, -1.0F).next();
				bufferBuilder.vertex((double)ab, (double)ad, -0.5).texture((double)ae, (double)ag).normal(0.0F, 0.0F, -1.0F).next();
				bufferBuilder.vertex((double)ab, (double)ac, -0.5).texture((double)ae, (double)ah).normal(0.0F, 0.0F, -1.0F).next();
				bufferBuilder.vertex((double)aa, (double)ac, -0.5).texture((double)af, (double)ah).normal(0.0F, 0.0F, -1.0F).next();
				bufferBuilder.vertex((double)aa, (double)ac, 0.5).texture(0.75, 0.0).normal(0.0F, 0.0F, 1.0F).next();
				bufferBuilder.vertex((double)ab, (double)ac, 0.5).texture(0.8125, 0.0).normal(0.0F, 0.0F, 1.0F).next();
				bufferBuilder.vertex((double)ab, (double)ad, 0.5).texture(0.8125, 0.0625).normal(0.0F, 0.0F, 1.0F).next();
				bufferBuilder.vertex((double)aa, (double)ad, 0.5).texture(0.75, 0.0625).normal(0.0F, 0.0F, 1.0F).next();
				bufferBuilder.vertex((double)aa, (double)ac, -0.5).texture(0.75, 0.001953125).normal(0.0F, 1.0F, 0.0F).next();
				bufferBuilder.vertex((double)ab, (double)ac, -0.5).texture(0.8125, 0.001953125).normal(0.0F, 1.0F, 0.0F).next();
				bufferBuilder.vertex((double)ab, (double)ac, 0.5).texture(0.8125, 0.001953125).normal(0.0F, 1.0F, 0.0F).next();
				bufferBuilder.vertex((double)aa, (double)ac, 0.5).texture(0.75, 0.001953125).normal(0.0F, 1.0F, 0.0F).next();
				bufferBuilder.vertex((double)aa, (double)ad, 0.5).texture(0.75, 0.001953125).normal(0.0F, -1.0F, 0.0F).next();
				bufferBuilder.vertex((double)ab, (double)ad, 0.5).texture(0.8125, 0.001953125).normal(0.0F, -1.0F, 0.0F).next();
				bufferBuilder.vertex((double)ab, (double)ad, -0.5).texture(0.8125, 0.001953125).normal(0.0F, -1.0F, 0.0F).next();
				bufferBuilder.vertex((double)aa, (double)ad, -0.5).texture(0.75, 0.001953125).normal(0.0F, -1.0F, 0.0F).next();
				bufferBuilder.vertex((double)aa, (double)ac, 0.5).texture(0.7519531F, 0.0).normal(-1.0F, 0.0F, 0.0F).next();
				bufferBuilder.vertex((double)aa, (double)ad, 0.5).texture(0.7519531F, 0.0625).normal(-1.0F, 0.0F, 0.0F).next();
				bufferBuilder.vertex((double)aa, (double)ad, -0.5).texture(0.7519531F, 0.0625).normal(-1.0F, 0.0F, 0.0F).next();
				bufferBuilder.vertex((double)aa, (double)ac, -0.5).texture(0.7519531F, 0.0).normal(-1.0F, 0.0F, 0.0F).next();
				bufferBuilder.vertex((double)ab, (double)ac, -0.5).texture(0.7519531F, 0.0).normal(1.0F, 0.0F, 0.0F).next();
				bufferBuilder.vertex((double)ab, (double)ad, -0.5).texture(0.7519531F, 0.0625).normal(1.0F, 0.0F, 0.0F).next();
				bufferBuilder.vertex((double)ab, (double)ad, 0.5).texture(0.7519531F, 0.0625).normal(1.0F, 0.0F, 0.0F).next();
				bufferBuilder.vertex((double)ab, (double)ac, 0.5).texture(0.7519531F, 0.0).normal(1.0F, 0.0F, 0.0F).next();
				tessellator.draw();
			}
		}
	}

	private void method_4076(PaintingEntity paintingEntity, float f, float g) {
		int i = MathHelper.floor(paintingEntity.x);
		int j = MathHelper.floor(paintingEntity.y + (double)(g / 16.0F));
		int k = MathHelper.floor(paintingEntity.z);
		Direction direction = paintingEntity.facing;
		if (direction == Direction.NORTH) {
			i = MathHelper.floor(paintingEntity.x + (double)(f / 16.0F));
		}

		if (direction == Direction.WEST) {
			k = MathHelper.floor(paintingEntity.z - (double)(f / 16.0F));
		}

		if (direction == Direction.SOUTH) {
			i = MathHelper.floor(paintingEntity.x - (double)(f / 16.0F));
		}

		if (direction == Direction.EAST) {
			k = MathHelper.floor(paintingEntity.z + (double)(f / 16.0F));
		}

		int l = this.renderManager.world.getLightmapIndex(new BlockPos(i, j, k), 0);
		int m = l % 65536;
		int n = l / 65536;
		GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)m, (float)n);
		GlStateManager.color3f(1.0F, 1.0F, 1.0F);
	}
}

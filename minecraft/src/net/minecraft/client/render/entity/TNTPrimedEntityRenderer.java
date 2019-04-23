package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.PrimedTntEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class TNTPrimedEntityRenderer extends EntityRenderer<PrimedTntEntity> {
	public TNTPrimedEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
		this.field_4673 = 0.5F;
	}

	public void method_4135(PrimedTntEntity primedTntEntity, double d, double e, double f, float g, float h) {
		BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
		GlStateManager.pushMatrix();
		GlStateManager.translatef((float)d, (float)e + 0.5F, (float)f);
		if ((float)primedTntEntity.getFuseTimer() - h + 1.0F < 10.0F) {
			float i = 1.0F - ((float)primedTntEntity.getFuseTimer() - h + 1.0F) / 10.0F;
			i = MathHelper.clamp(i, 0.0F, 1.0F);
			i *= i;
			i *= i;
			float j = 1.0F + i * 0.3F;
			GlStateManager.scalef(j, j, j);
		}

		float i = (1.0F - ((float)primedTntEntity.getFuseTimer() - h + 1.0F) / 100.0F) * 0.8F;
		this.bindEntityTexture(primedTntEntity);
		GlStateManager.rotatef(-90.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.translatef(-0.5F, -0.5F, 0.5F);
		blockRenderManager.renderDynamic(Blocks.field_10375.getDefaultState(), primedTntEntity.getBrightnessAtEyes());
		GlStateManager.translatef(0.0F, 0.0F, 1.0F);
		if (this.field_4674) {
			GlStateManager.enableColorMaterial();
			GlStateManager.setupSolidRenderingTextureCombine(this.getOutlineColor(primedTntEntity));
			blockRenderManager.renderDynamic(Blocks.field_10375.getDefaultState(), 1.0F);
			GlStateManager.tearDownSolidRenderingTextureCombine();
			GlStateManager.disableColorMaterial();
		} else if (primedTntEntity.getFuseTimer() / 5 % 2 == 0) {
			GlStateManager.disableTexture();
			GlStateManager.disableLighting();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.DST_ALPHA);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, i);
			GlStateManager.polygonOffset(-3.0F, -3.0F);
			GlStateManager.enablePolygonOffset();
			blockRenderManager.renderDynamic(Blocks.field_10375.getDefaultState(), 1.0F);
			GlStateManager.polygonOffset(0.0F, 0.0F);
			GlStateManager.disablePolygonOffset();
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.disableBlend();
			GlStateManager.enableLighting();
			GlStateManager.enableTexture();
		}

		GlStateManager.popMatrix();
		super.render(primedTntEntity, d, e, f, g, h);
	}

	protected Identifier method_4136(PrimedTntEntity primedTntEntity) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
	}
}

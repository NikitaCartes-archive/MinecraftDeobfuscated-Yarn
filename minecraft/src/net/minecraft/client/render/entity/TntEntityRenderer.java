package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.TntEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class TntEntityRenderer extends EntityRenderer<TntEntity> {
	public TntEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
		this.field_4673 = 0.5F;
	}

	public void method_4135(TntEntity tntEntity, double d, double e, double f, float g, float h) {
		BlockRenderManager blockRenderManager = MinecraftClient.getInstance().method_1541();
		GlStateManager.pushMatrix();
		GlStateManager.translatef((float)d, (float)e + 0.5F, (float)f);
		if ((float)tntEntity.getFuseTimer() - h + 1.0F < 10.0F) {
			float i = 1.0F - ((float)tntEntity.getFuseTimer() - h + 1.0F) / 10.0F;
			i = MathHelper.clamp(i, 0.0F, 1.0F);
			i *= i;
			i *= i;
			float j = 1.0F + i * 0.3F;
			GlStateManager.scalef(j, j, j);
		}

		float i = (1.0F - ((float)tntEntity.getFuseTimer() - h + 1.0F) / 100.0F) * 0.8F;
		this.bindEntityTexture(tntEntity);
		GlStateManager.rotatef(-90.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.translatef(-0.5F, -0.5F, 0.5F);
		blockRenderManager.renderDynamic(Blocks.field_10375.method_9564(), tntEntity.getBrightnessAtEyes());
		GlStateManager.translatef(0.0F, 0.0F, 1.0F);
		if (this.renderOutlines) {
			GlStateManager.enableColorMaterial();
			GlStateManager.setupSolidRenderingTextureCombine(this.getOutlineColor(tntEntity));
			blockRenderManager.renderDynamic(Blocks.field_10375.method_9564(), 1.0F);
			GlStateManager.tearDownSolidRenderingTextureCombine();
			GlStateManager.disableColorMaterial();
		} else if (tntEntity.getFuseTimer() / 5 % 2 == 0) {
			GlStateManager.disableTexture();
			GlStateManager.disableLighting();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.DST_ALPHA);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, i);
			GlStateManager.polygonOffset(-3.0F, -3.0F);
			GlStateManager.enablePolygonOffset();
			blockRenderManager.renderDynamic(Blocks.field_10375.method_9564(), 1.0F);
			GlStateManager.polygonOffset(0.0F, 0.0F);
			GlStateManager.disablePolygonOffset();
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.disableBlend();
			GlStateManager.enableLighting();
			GlStateManager.enableTexture();
		}

		GlStateManager.popMatrix();
		super.render(tntEntity, d, e, f, g, h);
	}

	protected Identifier method_4136(TntEntity tntEntity) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
	}
}

package net.minecraft.client.render.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4493;
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
		BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
		RenderSystem.pushMatrix();
		RenderSystem.translatef((float)d, (float)e + 0.5F, (float)f);
		if ((float)tntEntity.getFuseTimer() - h + 1.0F < 10.0F) {
			float i = 1.0F - ((float)tntEntity.getFuseTimer() - h + 1.0F) / 10.0F;
			i = MathHelper.clamp(i, 0.0F, 1.0F);
			i *= i;
			i *= i;
			float j = 1.0F + i * 0.3F;
			RenderSystem.scalef(j, j, j);
		}

		float i = (1.0F - ((float)tntEntity.getFuseTimer() - h + 1.0F) / 100.0F) * 0.8F;
		this.bindEntityTexture(tntEntity);
		RenderSystem.rotatef(-90.0F, 0.0F, 1.0F, 0.0F);
		RenderSystem.translatef(-0.5F, -0.5F, 0.5F);
		blockRenderManager.renderDynamic(Blocks.TNT.getDefaultState(), tntEntity.getBrightnessAtEyes());
		RenderSystem.translatef(0.0F, 0.0F, 1.0F);
		if (this.renderOutlines) {
			RenderSystem.enableColorMaterial();
			RenderSystem.setupSolidRenderingTextureCombine(this.getOutlineColor(tntEntity));
			blockRenderManager.renderDynamic(Blocks.TNT.getDefaultState(), 1.0F);
			RenderSystem.tearDownSolidRenderingTextureCombine();
			RenderSystem.disableColorMaterial();
		} else if (tntEntity.getFuseTimer() / 5 % 2 == 0) {
			RenderSystem.disableTexture();
			RenderSystem.disableLighting();
			RenderSystem.enableBlend();
			RenderSystem.blendFunc(class_4493.class_4535.SRC_ALPHA, class_4493.class_4534.DST_ALPHA);
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, i);
			RenderSystem.polygonOffset(-3.0F, -3.0F);
			RenderSystem.enablePolygonOffset();
			blockRenderManager.renderDynamic(Blocks.TNT.getDefaultState(), 1.0F);
			RenderSystem.polygonOffset(0.0F, 0.0F);
			RenderSystem.disablePolygonOffset();
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.disableBlend();
			RenderSystem.enableLighting();
			RenderSystem.enableTexture();
		}

		RenderSystem.popMatrix();
		super.render(tntEntity, d, e, f, g, h);
	}

	protected Identifier method_4136(TntEntity tntEntity) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
	}
}

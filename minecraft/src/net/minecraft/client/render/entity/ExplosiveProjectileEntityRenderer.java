package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexBuffer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ExplosiveProjectileEntityRenderer extends EntityRenderer<ExplosiveProjectileEntity> {
	private final float field_4704;

	public ExplosiveProjectileEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, float f) {
		super(entityRenderDispatcher);
		this.field_4704 = f;
	}

	public void method_3971(ExplosiveProjectileEntity explosiveProjectileEntity, double d, double e, double f, float g, float h) {
		GlStateManager.pushMatrix();
		this.method_3925(explosiveProjectileEntity);
		GlStateManager.translatef((float)d, (float)e, (float)f);
		GlStateManager.enableRescaleNormal();
		GlStateManager.scalef(this.field_4704, this.field_4704, this.field_4704);
		Sprite sprite = MinecraftClient.getInstance().getItemRenderer().getModelMap().getSprite(Items.field_8814);
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexBuffer = tessellator.getVertexBuffer();
		float i = sprite.getMinU();
		float j = sprite.getMaxU();
		float k = sprite.getMinV();
		float l = sprite.getMaxV();
		float m = 1.0F;
		float n = 0.5F;
		float o = 0.25F;
		GlStateManager.rotatef(180.0F - this.renderManager.field_4679, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef((float)(this.renderManager.settings.field_1850 == 2 ? -1 : 1) * -this.renderManager.field_4677, 1.0F, 0.0F, 0.0F);
		if (this.field_4674) {
			GlStateManager.enableColorMaterial();
			GlStateManager.setupSolidRenderingTextureCombine(this.method_3929(explosiveProjectileEntity));
		}

		vertexBuffer.begin(7, VertexFormats.POSITION_UV_NORMAL);
		vertexBuffer.vertex(-0.5, -0.25, 0.0).texture((double)i, (double)l).normal(0.0F, 1.0F, 0.0F).next();
		vertexBuffer.vertex(0.5, -0.25, 0.0).texture((double)j, (double)l).normal(0.0F, 1.0F, 0.0F).next();
		vertexBuffer.vertex(0.5, 0.75, 0.0).texture((double)j, (double)k).normal(0.0F, 1.0F, 0.0F).next();
		vertexBuffer.vertex(-0.5, 0.75, 0.0).texture((double)i, (double)k).normal(0.0F, 1.0F, 0.0F).next();
		tessellator.draw();
		if (this.field_4674) {
			GlStateManager.tearDownSolidRenderingTextureCombine();
			GlStateManager.disableColorMaterial();
		}

		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
		super.method_3936(explosiveProjectileEntity, d, e, f, g, h);
	}

	protected Identifier getTexture(ExplosiveProjectileEntity explosiveProjectileEntity) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
	}
}

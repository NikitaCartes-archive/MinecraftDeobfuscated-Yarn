package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3856;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SnowballEntityRenderer<T extends Entity & class_3856> extends EntityRenderer<T> {
	private final ItemRenderer item;
	private final float field_17147;

	public SnowballEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, ItemRenderer itemRenderer, float f) {
		super(entityRenderDispatcher);
		this.item = itemRenderer;
		this.field_17147 = f;
	}

	public SnowballEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, ItemRenderer itemRenderer) {
		this(entityRenderDispatcher, itemRenderer, 1.0F);
	}

	@Override
	public void render(T entity, double d, double e, double f, float g, float h) {
		GlStateManager.pushMatrix();
		GlStateManager.translatef((float)d, (float)e, (float)f);
		GlStateManager.enableRescaleNormal();
		GlStateManager.scalef(this.field_17147, this.field_17147, this.field_17147);
		GlStateManager.rotatef(-this.renderManager.field_4679, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef((float)(this.renderManager.settings.field_1850 == 2 ? -1 : 1) * this.renderManager.field_4677, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
		this.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		if (this.renderOutlines) {
			GlStateManager.enableColorMaterial();
			GlStateManager.setupSolidRenderingTextureCombine(this.getOutlineColor(entity));
		}

		this.item.renderItemWithTransformation(entity.method_7495(), ModelTransformation.Type.GROUND);
		if (this.renderOutlines) {
			GlStateManager.tearDownSolidRenderingTextureCombine();
			GlStateManager.disableColorMaterial();
		}

		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
		super.render(entity, d, e, f, g, h);
	}

	@Override
	protected Identifier getTexture(Entity entity) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
	}
}

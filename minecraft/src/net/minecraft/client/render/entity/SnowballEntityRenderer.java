package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformations;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SnowballEntityRenderer<T extends Entity> extends EntityRenderer<T> {
	protected final Item item;
	private final ItemRenderer itemRenderer;

	public SnowballEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, Item item, ItemRenderer itemRenderer) {
		super(entityRenderDispatcher);
		this.item = item;
		this.itemRenderer = itemRenderer;
	}

	@Override
	public void method_3936(T entity, double d, double e, double f, float g, float h) {
		GlStateManager.pushMatrix();
		GlStateManager.translatef((float)d, (float)e, (float)f);
		GlStateManager.enableRescaleNormal();
		GlStateManager.rotatef(-this.renderManager.field_4679, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef((float)(this.renderManager.settings.field_1850 == 2 ? -1 : 1) * this.renderManager.field_4677, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
		this.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		if (this.field_4674) {
			GlStateManager.enableColorMaterial();
			GlStateManager.setupSolidRenderingTextureCombine(this.method_3929(entity));
		}

		this.itemRenderer.renderItemWithTransformation(this.getItemStack(entity), ModelTransformations.Type.GROUND);
		if (this.field_4674) {
			GlStateManager.tearDownSolidRenderingTextureCombine();
			GlStateManager.disableColorMaterial();
		}

		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
		super.method_3936(entity, d, e, f, g, h);
	}

	public ItemStack getItemStack(T entity) {
		return new ItemStack(this.item);
	}

	@Override
	protected Identifier getTexture(Entity entity) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
	}
}

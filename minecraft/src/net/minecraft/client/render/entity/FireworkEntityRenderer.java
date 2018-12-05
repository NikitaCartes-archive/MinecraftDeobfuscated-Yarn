package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformations;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.FireworkEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class FireworkEntityRenderer extends EntityRenderer<FireworkEntity> {
	protected final Item field_4702;
	private final ItemRenderer field_4703;

	public FireworkEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, Item item, ItemRenderer itemRenderer) {
		super(entityRenderDispatcher);
		this.field_4702 = item;
		this.field_4703 = itemRenderer;
	}

	public void method_3968(FireworkEntity fireworkEntity, double d, double e, double f, float g, float h) {
		GlStateManager.pushMatrix();
		GlStateManager.translatef((float)d, (float)e, (float)f);
		GlStateManager.enableRescaleNormal();
		GlStateManager.rotatef(-this.renderManager.field_4679, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef((float)(this.renderManager.settings.field_1850 == 2 ? -1 : 1) * this.renderManager.field_4677, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
		this.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		if (this.field_4674) {
			GlStateManager.enableColorMaterial();
			GlStateManager.setupSolidRenderingTextureCombine(this.method_3929(fireworkEntity));
		}

		this.field_4703.renderItemWithTransformation(new ItemStack(this.field_4702), ModelTransformations.Type.GROUND);
		if (this.field_4674) {
			GlStateManager.tearDownSolidRenderingTextureCombine();
			GlStateManager.disableColorMaterial();
		}

		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
		super.method_3936(fireworkEntity, d, e, f, g, h);
	}

	protected Identifier getTexture(FireworkEntity fireworkEntity) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
	}
}

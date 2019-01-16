package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.FireworkEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class FireworkEntityRenderer extends EntityRenderer<FireworkEntity> {
	private final ItemRenderer field_4703;

	public FireworkEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, ItemRenderer itemRenderer) {
		super(entityRenderDispatcher);
		this.field_4703 = itemRenderer;
	}

	public void method_3968(FireworkEntity fireworkEntity, double d, double e, double f, float g, float h) {
		GlStateManager.pushMatrix();
		GlStateManager.translatef((float)d, (float)e, (float)f);
		GlStateManager.enableRescaleNormal();
		GlStateManager.rotatef(-this.renderManager.field_4679, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef((float)(this.renderManager.settings.field_1850 == 2 ? -1 : 1) * this.renderManager.field_4677, 1.0F, 0.0F, 0.0F);
		if (fireworkEntity.method_7477()) {
			GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
		} else {
			GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
		}

		this.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		if (this.renderOutlines) {
			GlStateManager.enableColorMaterial();
			GlStateManager.setupSolidRenderingTextureCombine(this.getOutlineColor(fireworkEntity));
		}

		this.field_4703.renderItemWithTransformation(fireworkEntity.getItem(), ModelTransformation.Type.GROUND);
		if (this.renderOutlines) {
			GlStateManager.tearDownSolidRenderingTextureCombine();
			GlStateManager.disableColorMaterial();
		}

		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
		super.render(fireworkEntity, d, e, f, g, h);
	}

	protected Identifier getTexture(FireworkEntity fireworkEntity) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
	}
}

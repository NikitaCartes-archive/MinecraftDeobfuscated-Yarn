package net.minecraft.client.render.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.FireworkEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class FireworkEntityRenderer extends EntityRenderer<FireworkEntity> {
	private final ItemRenderer itemRenderer;

	public FireworkEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, ItemRenderer itemRenderer) {
		super(entityRenderDispatcher);
		this.itemRenderer = itemRenderer;
	}

	public void method_3968(FireworkEntity fireworkEntity, double d, double e, double f, float g, float h) {
		RenderSystem.pushMatrix();
		RenderSystem.translatef((float)d, (float)e, (float)f);
		RenderSystem.enableRescaleNormal();
		RenderSystem.rotatef(-this.renderManager.cameraYaw, 0.0F, 1.0F, 0.0F);
		RenderSystem.rotatef((float)(this.renderManager.gameOptions.perspective == 2 ? -1 : 1) * this.renderManager.cameraPitch, 1.0F, 0.0F, 0.0F);
		if (fireworkEntity.wasShotAtAngle()) {
			RenderSystem.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
		} else {
			RenderSystem.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
		}

		this.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		if (this.renderOutlines) {
			RenderSystem.enableColorMaterial();
			RenderSystem.setupSolidRenderingTextureCombine(this.getOutlineColor(fireworkEntity));
		}

		this.itemRenderer.renderItem(fireworkEntity.getStack(), ModelTransformation.Type.GROUND);
		if (this.renderOutlines) {
			RenderSystem.tearDownSolidRenderingTextureCombine();
			RenderSystem.disableColorMaterial();
		}

		RenderSystem.disableRescaleNormal();
		RenderSystem.popMatrix();
		super.render(fireworkEntity, d, e, f, g, h);
	}

	protected Identifier method_3969(FireworkEntity fireworkEntity) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
	}
}

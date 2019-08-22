package net.minecraft.client.render.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class FlyingItemEntityRenderer<T extends Entity & FlyingItemEntity> extends EntityRenderer<T> {
	private final ItemRenderer item;
	private final float scale;

	public FlyingItemEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, ItemRenderer itemRenderer, float f) {
		super(entityRenderDispatcher);
		this.item = itemRenderer;
		this.scale = f;
	}

	public FlyingItemEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, ItemRenderer itemRenderer) {
		this(entityRenderDispatcher, itemRenderer, 1.0F);
	}

	@Override
	public void render(T entity, double d, double e, double f, float g, float h) {
		RenderSystem.pushMatrix();
		RenderSystem.translatef((float)d, (float)e, (float)f);
		RenderSystem.enableRescaleNormal();
		RenderSystem.scalef(this.scale, this.scale, this.scale);
		RenderSystem.rotatef(-this.renderManager.cameraYaw, 0.0F, 1.0F, 0.0F);
		RenderSystem.rotatef((float)(this.renderManager.gameOptions.perspective == 2 ? -1 : 1) * this.renderManager.cameraPitch, 1.0F, 0.0F, 0.0F);
		RenderSystem.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
		this.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		if (this.renderOutlines) {
			RenderSystem.enableColorMaterial();
			RenderSystem.setupSolidRenderingTextureCombine(this.getOutlineColor(entity));
		}

		this.item.renderItem(entity.getStack(), ModelTransformation.Type.GROUND);
		if (this.renderOutlines) {
			RenderSystem.tearDownSolidRenderingTextureCombine();
			RenderSystem.disableColorMaterial();
		}

		RenderSystem.disableRescaleNormal();
		RenderSystem.popMatrix();
		super.render(entity, d, e, f, g, h);
	}

	@Override
	protected Identifier getTexture(Entity entity) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
	}
}

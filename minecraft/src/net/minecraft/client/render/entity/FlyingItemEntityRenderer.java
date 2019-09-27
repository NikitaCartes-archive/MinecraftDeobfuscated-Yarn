package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.Vector3f;
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
	public void render(T entity, double d, double e, double f, float g, float h, class_4587 arg, class_4597 arg2) {
		arg.method_22903();
		arg.method_22905(this.scale, this.scale, this.scale);
		arg.method_22907(Vector3f.field_20705.method_23214(-this.renderManager.cameraYaw, true));
		arg.method_22907(Vector3f.field_20703.method_23214((float)(this.renderManager.gameOptions.perspective == 2 ? -1 : 1) * this.renderManager.cameraPitch, true));
		arg.method_22907(Vector3f.field_20705.method_23214(180.0F, true));
		this.item.method_23178(entity.getStack(), ModelTransformation.Type.GROUND, entity.getLightmapCoordinates(), arg, arg2);
		arg.method_22909();
		super.render(entity, d, e, f, g, h, arg, arg2);
	}

	@Override
	public Identifier getTexture(Entity entity) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
	}
}

package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MatrixStack;

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
	public void render(
		T entity, double d, double e, double f, float g, float h, MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage
	) {
		matrixStack.push();
		matrixStack.scale(this.scale, this.scale, this.scale);
		matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(-this.renderManager.cameraYaw, true));
		matrixStack.multiply(
			Vector3f.POSITIVE_X.getRotationQuaternion((float)(this.renderManager.gameOptions.perspective == 2 ? -1 : 1) * this.renderManager.cameraPitch, true)
		);
		matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(180.0F, true));
		this.item.method_23178(entity.getStack(), ModelTransformation.Type.GROUND, entity.getLightmapCoordinates(), matrixStack, layeredVertexConsumerStorage);
		matrixStack.pop();
		super.render(entity, d, e, f, g, h, matrixStack, layeredVertexConsumerStorage);
	}

	@Override
	public Identifier getTexture(Entity entity) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
	}
}

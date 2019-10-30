package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class FlyingItemEntityRenderer<T extends Entity & FlyingItemEntity> extends EntityRenderer<T> {
	private final ItemRenderer item;
	private final float scale;

	public FlyingItemEntityRenderer(EntityRenderDispatcher renderManager, ItemRenderer itemRenderer, float scale) {
		super(renderManager);
		this.item = itemRenderer;
		this.scale = scale;
	}

	public FlyingItemEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, ItemRenderer itemRenderer) {
		this(entityRenderDispatcher, itemRenderer, 1.0F);
	}

	@Override
	public void render(T entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrix, VertexConsumerProvider vertexConsumers) {
		matrix.push();
		matrix.scale(this.scale, this.scale, this.scale);
		matrix.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(-this.renderManager.cameraYaw));
		float f = (float)(this.renderManager.gameOptions.perspective == 2 ? -1 : 1) * this.renderManager.cameraPitch;
		matrix.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(f));
		matrix.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(180.0F));
		this.item
			.method_23178(entity.getStack(), ModelTransformation.Type.GROUND, entity.getLightmapCoordinates(), OverlayTexture.DEFAULT_UV, matrix, vertexConsumers);
		matrix.pop();
		super.render(entity, x, y, z, yaw, tickDelta, matrix, vertexConsumers);
	}

	@Override
	public Identifier getTexture(Entity entity) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
	}
}

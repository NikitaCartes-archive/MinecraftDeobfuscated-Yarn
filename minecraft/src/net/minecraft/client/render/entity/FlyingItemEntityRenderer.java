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
	private final boolean field_21745;

	public FlyingItemEntityRenderer(EntityRenderDispatcher renderManager, ItemRenderer itemRenderer, float scale, boolean bl) {
		super(renderManager);
		this.item = itemRenderer;
		this.scale = scale;
		this.field_21745 = bl;
	}

	public FlyingItemEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, ItemRenderer itemRenderer) {
		this(entityRenderDispatcher, itemRenderer, 1.0F, false);
	}

	@Override
	protected int getBlockLight(T entity, float tickDelta) {
		return this.field_21745 ? 15 : super.getBlockLight(entity, tickDelta);
	}

	@Override
	public void render(T entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
		matrices.push();
		matrices.scale(this.scale, this.scale, this.scale);
		matrices.multiply(this.renderManager.method_24197());
		matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
		this.item.method_23178(entity.getStack(), ModelTransformation.Type.GROUND, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers);
		matrices.pop();
		super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
	}

	@Override
	public Identifier getTexture(Entity entity) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
	}
}

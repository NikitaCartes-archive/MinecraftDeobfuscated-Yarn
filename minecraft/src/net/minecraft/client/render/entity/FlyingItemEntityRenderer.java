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
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class FlyingItemEntityRenderer<T extends Entity & FlyingItemEntity> extends EntityRenderer<T> {
	private final ItemRenderer itemRenderer;
	private final float scale;
	private final boolean lit;

	public FlyingItemEntityRenderer(EntityRenderDispatcher dispatcher, ItemRenderer itemRenderer, float scale, boolean lit) {
		super(dispatcher);
		this.itemRenderer = itemRenderer;
		this.scale = scale;
		this.lit = lit;
	}

	public FlyingItemEntityRenderer(EntityRenderDispatcher dispatcher, ItemRenderer itemRenderer) {
		this(dispatcher, itemRenderer, 1.0F, false);
	}

	@Override
	protected int getBlockLight(T entity, BlockPos blockPos) {
		return this.lit ? 15 : super.getBlockLight(entity, blockPos);
	}

	@Override
	public void render(T entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
		if (entity.age >= 2 || !(this.dispatcher.camera.getFocusedEntity().squaredDistanceTo(entity) < 12.25)) {
			matrices.push();
			matrices.scale(this.scale, this.scale, this.scale);
			matrices.multiply(this.dispatcher.getRotation());
			matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
			this.itemRenderer.renderItem(entity.getStack(), ModelTransformation.Mode.GROUND, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers);
			matrices.pop();
			super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
		}
	}

	@Override
	public Identifier getTexture(Entity entity) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
	}
}

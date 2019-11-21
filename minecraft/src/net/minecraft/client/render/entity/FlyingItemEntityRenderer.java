package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
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
	protected int method_24087(T entity, float f) {
		return this.field_21745 ? 15 : super.method_24087(entity, f);
	}

	@Override
	public void render(T entity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		matrixStack.push();
		matrixStack.scale(this.scale, this.scale, this.scale);
		matrixStack.multiply(this.renderManager.camera.method_23767());
		this.item.method_23178(entity.getStack(), ModelTransformation.Type.GROUND, i, OverlayTexture.DEFAULT_UV, matrixStack, vertexConsumerProvider);
		matrixStack.pop();
		super.render(entity, f, g, matrixStack, vertexConsumerProvider, i);
	}

	@Override
	public Identifier getTexture(Entity entity) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
	}
}

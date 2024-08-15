package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.state.FlyingItemEntityRenderState;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class FlyingItemEntityRenderer<T extends Entity & FlyingItemEntity> extends EntityRenderer<T, FlyingItemEntityRenderState> {
	private final ItemRenderer itemRenderer;
	private final float scale;
	private final boolean lit;

	public FlyingItemEntityRenderer(EntityRendererFactory.Context ctx, float scale, boolean lit) {
		super(ctx);
		this.itemRenderer = ctx.getItemRenderer();
		this.scale = scale;
		this.lit = lit;
	}

	public FlyingItemEntityRenderer(EntityRendererFactory.Context context) {
		this(context, 1.0F, false);
	}

	@Override
	protected int getBlockLight(T entity, BlockPos pos) {
		return this.lit ? 15 : super.getBlockLight(entity, pos);
	}

	public void render(FlyingItemEntityRenderState flyingItemEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		matrixStack.push();
		matrixStack.scale(this.scale, this.scale, this.scale);
		matrixStack.multiply(this.dispatcher.getRotation());
		if (flyingItemEntityRenderState.model != null) {
			this.itemRenderer
				.renderItem(
					flyingItemEntityRenderState.stack,
					ModelTransformationMode.GROUND,
					false,
					matrixStack,
					vertexConsumerProvider,
					i,
					OverlayTexture.DEFAULT_UV,
					flyingItemEntityRenderState.model
				);
		}

		matrixStack.pop();
		super.render(flyingItemEntityRenderState, matrixStack, vertexConsumerProvider, i);
	}

	public FlyingItemEntityRenderState getRenderState() {
		return new FlyingItemEntityRenderState();
	}

	public void updateRenderState(T entity, FlyingItemEntityRenderState flyingItemEntityRenderState, float f) {
		super.updateRenderState(entity, flyingItemEntityRenderState, f);
		ItemStack itemStack = entity.getStack();
		flyingItemEntityRenderState.model = !itemStack.isEmpty() ? this.itemRenderer.getModel(itemStack, entity.getWorld(), null, entity.getId()) : null;
		flyingItemEntityRenderState.stack = itemStack;
	}

	public Identifier getTexture(FlyingItemEntityRenderState flyingItemEntityRenderState) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
	}
}

package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.state.OminousItemSpawnerEntityRenderState;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.OminousItemSpawnerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;

@Environment(EnvType.CLIENT)
public class OminousItemSpawnerEntityRenderer extends EntityRenderer<OminousItemSpawnerEntity, OminousItemSpawnerEntityRenderState> {
	private static final float field_50231 = 40.0F;
	private static final int field_50232 = 50;
	private final ItemRenderer itemRenderer;

	protected OminousItemSpawnerEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
		this.itemRenderer = context.getItemRenderer();
	}

	public Identifier getTexture(OminousItemSpawnerEntityRenderState ominousItemSpawnerEntityRenderState) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
	}

	public OminousItemSpawnerEntityRenderState getRenderState() {
		return new OminousItemSpawnerEntityRenderState();
	}

	public void updateRenderState(
		OminousItemSpawnerEntity ominousItemSpawnerEntity, OminousItemSpawnerEntityRenderState ominousItemSpawnerEntityRenderState, float f
	) {
		super.updateRenderState(ominousItemSpawnerEntity, ominousItemSpawnerEntityRenderState, f);
		ItemStack itemStack = ominousItemSpawnerEntity.getItem();
		ominousItemSpawnerEntityRenderState.stack = itemStack.copy();
		ominousItemSpawnerEntityRenderState.model = !itemStack.isEmpty() ? this.itemRenderer.getModel(itemStack, ominousItemSpawnerEntity.getWorld(), null, 0) : null;
	}

	public void render(
		OminousItemSpawnerEntityRenderState ominousItemSpawnerEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i
	) {
		BakedModel bakedModel = ominousItemSpawnerEntityRenderState.model;
		if (bakedModel != null) {
			matrixStack.push();
			if (ominousItemSpawnerEntityRenderState.age <= 50.0F) {
				float f = Math.min(ominousItemSpawnerEntityRenderState.age, 50.0F) / 50.0F;
				matrixStack.scale(f, f, f);
			}

			float f = MathHelper.wrapDegrees(ominousItemSpawnerEntityRenderState.age * 40.0F);
			matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(f));
			ItemEntityRenderer.renderStack(
				this.itemRenderer,
				matrixStack,
				vertexConsumerProvider,
				15728880,
				ominousItemSpawnerEntityRenderState.stack,
				bakedModel,
				bakedModel.hasDepth(),
				Random.create()
			);
			matrixStack.pop();
		}
	}
}

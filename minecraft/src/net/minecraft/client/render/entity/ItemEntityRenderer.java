package net.minecraft.client.render.entity;

import com.google.common.annotations.VisibleForTesting;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.state.ItemEntityRenderState;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class ItemEntityRenderer extends EntityRenderer<ItemEntity, ItemEntityRenderState> {
	private static final float field_32924 = 0.15F;
	private static final float field_32929 = 0.0F;
	private static final float field_32930 = 0.0F;
	private static final float field_32931 = 0.09375F;
	private final ItemRenderer itemRenderer;
	private final Random random = Random.create();

	public ItemEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
		this.itemRenderer = context.getItemRenderer();
		this.shadowRadius = 0.15F;
		this.shadowOpacity = 0.75F;
	}

	public Identifier getTexture(ItemEntityRenderState itemEntityRenderState) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
	}

	public ItemEntityRenderState getRenderState() {
		return new ItemEntityRenderState();
	}

	public void updateRenderState(ItemEntity itemEntity, ItemEntityRenderState itemEntityRenderState, float f) {
		super.updateRenderState(itemEntity, itemEntityRenderState, f);
		itemEntityRenderState.age = (float)itemEntity.getItemAge() + f;
		itemEntityRenderState.uniqueOffset = itemEntity.uniqueOffset;
		ItemStack itemStack = itemEntity.getStack();
		itemEntityRenderState.stack = itemStack;
		itemEntityRenderState.model = this.itemRenderer.getModel(itemStack, itemEntity.getWorld(), null, itemEntity.getId());
	}

	public void render(ItemEntityRenderState itemEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		BakedModel bakedModel = itemEntityRenderState.model;
		if (bakedModel != null) {
			matrixStack.push();
			ItemStack itemStack = itemEntityRenderState.stack;
			this.random.setSeed((long)getSeed(itemStack));
			boolean bl = bakedModel.hasDepth();
			float f = 0.25F;
			float g = MathHelper.sin(itemEntityRenderState.age / 10.0F + itemEntityRenderState.uniqueOffset) * 0.1F + 0.1F;
			float h = bakedModel.getTransformation().getTransformation(ModelTransformationMode.GROUND).scale.y();
			matrixStack.translate(0.0F, g + 0.25F * h, 0.0F);
			float j = ItemEntity.getRotation(itemEntityRenderState.age, itemEntityRenderState.uniqueOffset);
			matrixStack.multiply(RotationAxis.POSITIVE_Y.rotation(j));
			renderStack(this.itemRenderer, matrixStack, vertexConsumerProvider, i, itemStack, bakedModel, bl, this.random);
			matrixStack.pop();
			super.render(itemEntityRenderState, matrixStack, vertexConsumerProvider, i);
		}
	}

	public static int getSeed(ItemStack stack) {
		return stack.isEmpty() ? 187 : Item.getRawId(stack.getItem()) + stack.getDamage();
	}

	@VisibleForTesting
	static int getRenderedAmount(int stackSize) {
		if (stackSize <= 1) {
			return 1;
		} else if (stackSize <= 16) {
			return 2;
		} else if (stackSize <= 32) {
			return 3;
		} else {
			return stackSize <= 48 ? 4 : 5;
		}
	}

	public static void renderStack(
		ItemRenderer itemRenderer, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ItemStack stack, Random random, World world
	) {
		BakedModel bakedModel = itemRenderer.getModel(stack, world, null, 0);
		renderStack(itemRenderer, matrices, vertexConsumers, light, stack, bakedModel, bakedModel.hasDepth(), random);
	}

	public static void renderStack(
		ItemRenderer itemRenderer,
		MatrixStack matrices,
		VertexConsumerProvider vertexConsumers,
		int light,
		ItemStack stack,
		BakedModel model,
		boolean depth,
		Random random
	) {
		int i = getRenderedAmount(stack.getCount());
		float f = model.getTransformation().ground.scale.x();
		float g = model.getTransformation().ground.scale.y();
		float h = model.getTransformation().ground.scale.z();
		if (!depth) {
			float j = -0.0F * (float)(i - 1) * 0.5F * f;
			float k = -0.0F * (float)(i - 1) * 0.5F * g;
			float l = -0.09375F * (float)(i - 1) * 0.5F * h;
			matrices.translate(j, k, l);
		}

		for (int m = 0; m < i; m++) {
			matrices.push();
			if (m > 0) {
				if (depth) {
					float k = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
					float l = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
					float n = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
					matrices.translate(k, l, n);
				} else {
					float k = (random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
					float l = (random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
					matrices.translate(k, l, 0.0F);
				}
			}

			itemRenderer.renderItem(stack, ModelTransformationMode.GROUND, false, matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV, model);
			matrices.pop();
			if (!depth) {
				matrices.translate(0.0F * f, 0.0F * g, 0.09375F * h);
			}
		}
	}
}

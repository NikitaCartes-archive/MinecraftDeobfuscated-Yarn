package net.minecraft.client.render.entity;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class ItemEntityRenderer extends EntityRenderer<ItemEntity> {
	private final ItemRenderer itemRenderer;
	private final Random random = new Random();

	public ItemEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, ItemRenderer itemRenderer) {
		super(entityRenderDispatcher);
		this.itemRenderer = itemRenderer;
		this.field_4673 = 0.15F;
		this.field_4672 = 0.75F;
	}

	private int getRenderedAmount(ItemStack itemStack) {
		int i = 1;
		if (itemStack.getCount() > 48) {
			i = 5;
		} else if (itemStack.getCount() > 32) {
			i = 4;
		} else if (itemStack.getCount() > 16) {
			i = 3;
		} else if (itemStack.getCount() > 1) {
			i = 2;
		}

		return i;
	}

	public void method_3996(
		ItemEntity itemEntity, double d, double e, double f, float g, float h, MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage
	) {
		matrixStack.push();
		ItemStack itemStack = itemEntity.getStack();
		int i = itemStack.isEmpty() ? 187 : Item.getRawId(itemStack.getItem()) + itemStack.getDamage();
		this.random.setSeed((long)i);
		BakedModel bakedModel = this.itemRenderer.getHeldItemModel(itemStack, itemEntity.world, null);
		boolean bl = bakedModel.hasDepthInGui();
		int j = this.getRenderedAmount(itemStack);
		float k = 0.25F;
		float l = MathHelper.sin(((float)itemEntity.getAge() + h) / 10.0F + itemEntity.hoverHeight) * 0.1F + 0.1F;
		float m = bakedModel.getTransformation().getTransformation(ModelTransformation.Type.GROUND).scale.getY();
		matrixStack.translate(0.0, (double)(l + 0.25F * m), 0.0);
		float n = ((float)itemEntity.getAge() + h) / 20.0F + itemEntity.hoverHeight;
		matrixStack.multiply(Vector3f.POSITIVE_Y.method_23626(n));
		float o = bakedModel.getTransformation().ground.scale.getX();
		float p = bakedModel.getTransformation().ground.scale.getY();
		float q = bakedModel.getTransformation().ground.scale.getZ();
		if (!bl) {
			float r = -0.0F * (float)(j - 1) * 0.5F * o;
			float s = -0.0F * (float)(j - 1) * 0.5F * p;
			float t = -0.09375F * (float)(j - 1) * 0.5F * q;
			matrixStack.translate((double)r, (double)s, (double)t);
		}

		for (int u = 0; u < j; u++) {
			matrixStack.push();
			if (u > 0) {
				if (bl) {
					float s = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
					float t = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
					float v = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
					matrixStack.translate((double)s, (double)t, (double)v);
				} else {
					float s = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
					float t = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
					matrixStack.translate((double)s, (double)t, 0.0);
				}
			}

			this.itemRenderer
				.method_23179(
					itemStack,
					ModelTransformation.Type.GROUND,
					false,
					matrixStack,
					layeredVertexConsumerStorage,
					itemEntity.getLightmapCoordinates(),
					OverlayTexture.field_21444,
					bakedModel
				);
			matrixStack.pop();
			if (!bl) {
				matrixStack.translate((double)(0.0F * o), (double)(0.0F * p), (double)(0.09375F * q));
			}
		}

		matrixStack.pop();
		super.render(itemEntity, d, e, f, g, h, matrixStack, layeredVertexConsumerStorage);
	}

	public Identifier method_3999(ItemEntity itemEntity) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
	}
}

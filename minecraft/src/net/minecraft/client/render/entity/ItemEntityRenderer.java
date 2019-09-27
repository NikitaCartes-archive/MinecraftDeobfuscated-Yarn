package net.minecraft.client.render.entity;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
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

	public void method_3996(ItemEntity itemEntity, double d, double e, double f, float g, float h, class_4587 arg, class_4597 arg2) {
		arg.method_22903();
		ItemStack itemStack = itemEntity.getStack();
		int i = itemStack.isEmpty() ? 187 : Item.getRawId(itemStack.getItem()) + itemStack.getDamage();
		this.random.setSeed((long)i);
		BakedModel bakedModel = this.itemRenderer.getHeldItemModel(itemStack, itemEntity.world, null);
		boolean bl = bakedModel.hasDepthInGui();
		int j = this.getRenderedAmount(itemStack);
		float k = 0.25F;
		float l = MathHelper.sin(((float)itemEntity.getAge() + h) / 10.0F + itemEntity.hoverHeight) * 0.1F + 0.1F;
		float m = bakedModel.getTransformation().getTransformation(ModelTransformation.Type.GROUND).scale.getY();
		arg.method_22904(0.0, (double)(l + 0.25F * m), 0.0);
		float n = ((float)itemEntity.getAge() + h) / 20.0F + itemEntity.hoverHeight;
		arg.method_22907(Vector3f.field_20705.method_23214(n, false));
		float o = bakedModel.getTransformation().ground.scale.getX();
		float p = bakedModel.getTransformation().ground.scale.getY();
		float q = bakedModel.getTransformation().ground.scale.getZ();
		if (!bl) {
			float r = -0.0F * (float)(j - 1) * 0.5F * o;
			float s = -0.0F * (float)(j - 1) * 0.5F * p;
			float t = -0.09375F * (float)(j - 1) * 0.5F * q;
			arg.method_22904((double)r, (double)s, (double)t);
		}

		for (int u = 0; u < j; u++) {
			arg.method_22903();
			if (u > 0) {
				if (bl) {
					float s = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
					float t = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
					float v = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
					arg.method_22904((double)s, (double)t, (double)v);
				} else {
					float s = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
					float t = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
					arg.method_22904((double)s, (double)t, 0.0);
				}
			}

			this.itemRenderer.method_23179(itemStack, ModelTransformation.Type.GROUND, false, arg, arg2, itemEntity.getLightmapCoordinates(), bakedModel);
			arg.method_22909();
			if (!bl) {
				arg.method_22904((double)(0.0F * o), (double)(0.0F * p), (double)(0.09375F * q));
			}
		}

		arg.method_22909();
		super.render(itemEntity, d, e, f, g, h, arg, arg2);
	}

	public Identifier method_3999(ItemEntity itemEntity) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
	}
}

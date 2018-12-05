package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_308;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformations;
import net.minecraft.client.texture.SpriteAtlasTexture;
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

	private int method_3997(ItemEntity itemEntity, double d, double e, double f, float g, BakedModel bakedModel) {
		ItemStack itemStack = itemEntity.getStack();
		Item item = itemStack.getItem();
		if (item == null) {
			return 0;
		} else {
			boolean bl = bakedModel.hasDepthInGui();
			int i = this.method_3998(itemStack);
			float h = 0.25F;
			float j = MathHelper.sin(((float)itemEntity.getAge() + g) / 10.0F + itemEntity.field_7203) * 0.1F + 0.1F;
			float k = bakedModel.getTransformations().getTransformation(ModelTransformations.Type.GROUND).field_4285.y();
			GlStateManager.translatef((float)d, (float)e + j + 0.25F * k, (float)f);
			if (bl || this.renderManager.settings != null) {
				float l = (((float)itemEntity.getAge() + g) / 20.0F + itemEntity.field_7203) * (180.0F / (float)Math.PI);
				GlStateManager.rotatef(l, 0.0F, 1.0F, 0.0F);
			}

			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			return i;
		}
	}

	private int method_3998(ItemStack itemStack) {
		int i = 1;
		if (itemStack.getAmount() > 48) {
			i = 5;
		} else if (itemStack.getAmount() > 32) {
			i = 4;
		} else if (itemStack.getAmount() > 16) {
			i = 3;
		} else if (itemStack.getAmount() > 1) {
			i = 2;
		}

		return i;
	}

	public void method_3996(ItemEntity itemEntity, double d, double e, double f, float g, float h) {
		ItemStack itemStack = itemEntity.getStack();
		int i = itemStack.isEmpty() ? 187 : Item.getRawIdByItem(itemStack.getItem()) + itemStack.getDamage();
		this.random.setSeed((long)i);
		boolean bl = false;
		if (this.method_3925(itemEntity)) {
			this.renderManager.textureManager.getTexture(this.getTexture(itemEntity)).pushFilter(false, false);
			bl = true;
		}

		GlStateManager.enableRescaleNormal();
		GlStateManager.alphaFunc(516, 0.1F);
		GlStateManager.enableBlend();
		class_308.method_1452();
		GlStateManager.blendFuncSeparate(
			GlStateManager.SrcBlendFactor.SRC_ALPHA,
			GlStateManager.DstBlendFactor.ONE_MINUS_SRC_ALPHA,
			GlStateManager.SrcBlendFactor.ONE,
			GlStateManager.DstBlendFactor.ZERO
		);
		GlStateManager.pushMatrix();
		BakedModel bakedModel = this.itemRenderer.method_4028(itemStack, itemEntity.world, null);
		int j = this.method_3997(itemEntity, d, e, f, h, bakedModel);
		float k = bakedModel.getTransformations().ground.field_4285.x();
		float l = bakedModel.getTransformations().ground.field_4285.y();
		float m = bakedModel.getTransformations().ground.field_4285.z();
		boolean bl2 = bakedModel.hasDepthInGui();
		if (!bl2) {
			float n = -0.0F * (float)(j - 1) * 0.5F * k;
			float o = -0.0F * (float)(j - 1) * 0.5F * l;
			float p = -0.09375F * (float)(j - 1) * 0.5F * m;
			GlStateManager.translatef(n, o, p);
		}

		if (this.field_4674) {
			GlStateManager.enableColorMaterial();
			GlStateManager.setupSolidRenderingTextureCombine(this.method_3929(itemEntity));
		}

		for (int q = 0; q < j; q++) {
			if (bl2) {
				GlStateManager.pushMatrix();
				if (q > 0) {
					float o = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
					float p = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
					float r = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
					GlStateManager.translatef(o, p, r);
				}

				bakedModel.getTransformations().applyGl(ModelTransformations.Type.GROUND);
				this.itemRenderer.renderItemAndGlow(itemStack, bakedModel);
				GlStateManager.popMatrix();
			} else {
				GlStateManager.pushMatrix();
				if (q > 0) {
					float o = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
					float p = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
					GlStateManager.translatef(o, p, 0.0F);
				}

				bakedModel.getTransformations().applyGl(ModelTransformations.Type.GROUND);
				this.itemRenderer.renderItemAndGlow(itemStack, bakedModel);
				GlStateManager.popMatrix();
				GlStateManager.translatef(0.0F * k, 0.0F * l, 0.09375F * m);
			}
		}

		if (this.field_4674) {
			GlStateManager.tearDownSolidRenderingTextureCombine();
			GlStateManager.disableColorMaterial();
		}

		GlStateManager.popMatrix();
		GlStateManager.disableRescaleNormal();
		GlStateManager.disableBlend();
		this.method_3925(itemEntity);
		if (bl) {
			this.renderManager.textureManager.getTexture(this.getTexture(itemEntity)).popFilter();
		}

		super.method_3936(itemEntity, d, e, f, g, h);
	}

	protected Identifier getTexture(ItemEntity itemEntity) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
	}
}

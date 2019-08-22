package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.model.WitchEntityModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

@Environment(EnvType.CLIENT)
public class WitchHeldItemFeatureRenderer<T extends LivingEntity> extends FeatureRenderer<T, WitchEntityModel<T>> {
	public WitchHeldItemFeatureRenderer(FeatureRendererContext<T, WitchEntityModel<T>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_4208(T livingEntity, float f, float g, float h, float i, float j, float k, float l) {
		ItemStack itemStack = livingEntity.getMainHandStack();
		if (!itemStack.isEmpty()) {
			RenderSystem.color3f(1.0F, 1.0F, 1.0F);
			RenderSystem.pushMatrix();
			if (this.getModel().isChild) {
				RenderSystem.translatef(0.0F, 0.625F, 0.0F);
				RenderSystem.rotatef(-20.0F, -1.0F, 0.0F, 0.0F);
				float m = 0.5F;
				RenderSystem.scalef(0.5F, 0.5F, 0.5F);
			}

			this.getModel().method_2839().applyTransform(0.0625F);
			RenderSystem.translatef(-0.0625F, 0.53125F, 0.21875F);
			Item item = itemStack.getItem();
			if (Block.getBlockFromItem(item).getDefaultState().getRenderType() == BlockRenderType.ENTITYBLOCK_ANIMATED) {
				RenderSystem.translatef(0.0F, 0.0625F, -0.25F);
				RenderSystem.rotatef(30.0F, 1.0F, 0.0F, 0.0F);
				RenderSystem.rotatef(-5.0F, 0.0F, 1.0F, 0.0F);
				float n = 0.375F;
				RenderSystem.scalef(0.375F, -0.375F, 0.375F);
			} else if (item == Items.BOW) {
				RenderSystem.translatef(0.0F, 0.125F, -0.125F);
				RenderSystem.rotatef(-45.0F, 0.0F, 1.0F, 0.0F);
				float n = 0.625F;
				RenderSystem.scalef(0.625F, -0.625F, 0.625F);
				RenderSystem.rotatef(-100.0F, 1.0F, 0.0F, 0.0F);
				RenderSystem.rotatef(-20.0F, 0.0F, 1.0F, 0.0F);
			} else {
				RenderSystem.translatef(0.1875F, 0.1875F, 0.0F);
				float n = 0.875F;
				RenderSystem.scalef(0.875F, 0.875F, 0.875F);
				RenderSystem.rotatef(-20.0F, 0.0F, 0.0F, 1.0F);
				RenderSystem.rotatef(-60.0F, 1.0F, 0.0F, 0.0F);
				RenderSystem.rotatef(-30.0F, 0.0F, 0.0F, 1.0F);
			}

			RenderSystem.rotatef(-15.0F, 1.0F, 0.0F, 0.0F);
			RenderSystem.rotatef(40.0F, 0.0F, 0.0F, 1.0F);
			MinecraftClient.getInstance().getFirstPersonRenderer().renderItem(livingEntity, itemStack, ModelTransformation.Type.THIRD_PERSON_RIGHT_HAND);
			RenderSystem.popMatrix();
		}
	}

	@Override
	public boolean hasHurtOverlay() {
		return false;
	}
}

package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.model.json.ModelTransformations;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sortme.OptionMainHand;

@Environment(EnvType.CLIENT)
public class HeldItemEntityRenderer implements LayerEntityRenderer<LivingEntity> {
	protected final LivingEntityRenderer<?> renderer;

	public HeldItemEntityRenderer(LivingEntityRenderer<?> livingEntityRenderer) {
		this.renderer = livingEntityRenderer;
	}

	@Override
	public void render(LivingEntity livingEntity, float f, float g, float h, float i, float j, float k, float l) {
		boolean bl = livingEntity.getMainHand() == OptionMainHand.field_6183;
		ItemStack itemStack = bl ? livingEntity.getOffHandStack() : livingEntity.getMainHandStack();
		ItemStack itemStack2 = bl ? livingEntity.getMainHandStack() : livingEntity.getOffHandStack();
		if (!itemStack.isEmpty() || !itemStack2.isEmpty()) {
			GlStateManager.pushMatrix();
			if (this.renderer.method_4038().isChild) {
				float m = 0.5F;
				GlStateManager.translatef(0.0F, 0.75F, 0.0F);
				GlStateManager.scalef(0.5F, 0.5F, 0.5F);
			}

			this.method_4192(livingEntity, itemStack2, ModelTransformations.Type.THIRD_PERSON_RIGHT_HAND, OptionMainHand.field_6183);
			this.method_4192(livingEntity, itemStack, ModelTransformations.Type.THIRD_PERSON_LEFT_HAND, OptionMainHand.field_6182);
			GlStateManager.popMatrix();
		}
	}

	private void method_4192(LivingEntity livingEntity, ItemStack itemStack, ModelTransformations.Type type, OptionMainHand optionMainHand) {
		if (!itemStack.isEmpty()) {
			GlStateManager.pushMatrix();
			this.method_4193(optionMainHand);
			if (livingEntity.isSneaking()) {
				GlStateManager.translatef(0.0F, 0.2F, 0.0F);
			}

			GlStateManager.rotatef(-90.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
			boolean bl = optionMainHand == OptionMainHand.field_6182;
			GlStateManager.translatef((float)(bl ? -1 : 1) / 16.0F, 0.125F, -0.625F);
			MinecraftClient.getInstance().getFirstPersonRenderer().renderItemFromSide(livingEntity, itemStack, type, bl);
			GlStateManager.popMatrix();
		}
	}

	protected void method_4193(OptionMainHand optionMainHand) {
		((BipedEntityModel)this.renderer.method_4038()).method_2803(0.0625F, optionMainHand);
	}

	@Override
	public boolean shouldMergeTextures() {
		return false;
	}
}

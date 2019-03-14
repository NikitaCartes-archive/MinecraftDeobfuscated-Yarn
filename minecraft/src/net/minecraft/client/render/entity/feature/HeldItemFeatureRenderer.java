package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AbsoluteHand;

@Environment(EnvType.CLIENT)
public class HeldItemFeatureRenderer<T extends LivingEntity, M extends EntityModel<T> & ModelWithArms> extends FeatureRenderer<T, M> {
	public HeldItemFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_17162(T livingEntity, float f, float g, float h, float i, float j, float k, float l) {
		boolean bl = livingEntity.getMainHand() == AbsoluteHand.field_6183;
		ItemStack itemStack = bl ? livingEntity.getOffHandStack() : livingEntity.getMainHandStack();
		ItemStack itemStack2 = bl ? livingEntity.getMainHandStack() : livingEntity.getOffHandStack();
		if (!itemStack.isEmpty() || !itemStack2.isEmpty()) {
			GlStateManager.pushMatrix();
			if (this.getModel().isChild) {
				float m = 0.5F;
				GlStateManager.translatef(0.0F, 0.75F, 0.0F);
				GlStateManager.scalef(0.5F, 0.5F, 0.5F);
			}

			this.method_4192(livingEntity, itemStack2, ModelTransformation.Type.field_4320, AbsoluteHand.field_6183);
			this.method_4192(livingEntity, itemStack, ModelTransformation.Type.field_4323, AbsoluteHand.field_6182);
			GlStateManager.popMatrix();
		}
	}

	private void method_4192(LivingEntity livingEntity, ItemStack itemStack, ModelTransformation.Type type, AbsoluteHand absoluteHand) {
		if (!itemStack.isEmpty()) {
			GlStateManager.pushMatrix();
			this.method_4193(absoluteHand);
			if (livingEntity.isSneaking()) {
				GlStateManager.translatef(0.0F, 0.2F, 0.0F);
			}

			GlStateManager.rotatef(-90.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
			boolean bl = absoluteHand == AbsoluteHand.field_6182;
			GlStateManager.translatef((float)(bl ? -1 : 1) / 16.0F, 0.125F, -0.625F);
			MinecraftClient.getInstance().getFirstPersonRenderer().renderItemFromSide(livingEntity, itemStack, type, bl);
			GlStateManager.popMatrix();
		}
	}

	protected void method_4193(AbsoluteHand absoluteHand) {
		this.getModel().setArmAngle(0.0625F, absoluteHand);
	}

	@Override
	public boolean method_4200() {
		return false;
	}
}

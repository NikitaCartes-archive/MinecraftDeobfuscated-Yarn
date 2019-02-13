package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.FoxModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)
public class class_4043 extends FeatureRenderer<FoxEntity, FoxModel<FoxEntity>> {
	public class_4043(FeatureRendererContext<FoxEntity, FoxModel<FoxEntity>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_18335(FoxEntity foxEntity, float f, float g, float h, float i, float j, float k, float l) {
		ItemStack itemStack = foxEntity.getEquippedStack(EquipmentSlot.HAND_MAIN);
		if (!itemStack.isEmpty()) {
			boolean bl = foxEntity.isSleeping();
			boolean bl2 = foxEntity.isChild();
			float m = -0.14F;
			float n = -0.7F;
			float o = 1.3F;
			if (bl2) {
				m = -0.01F;
				o = 1.355F;
				n = -0.37F;
				if (foxEntity.isSitting()) {
					o = 1.19F;
					n = -0.4F;
				} else if (bl) {
					m = 0.55F;
					o = 1.5F;
					n = 0.125F;
				} else if (foxEntity.isRollingHead()) {
					m = -0.08F;
					o = 1.32F;
				}
			} else if (foxEntity.isSitting()) {
				o = 0.9F;
				n = -0.52F;
			} else if (bl) {
				m = 0.6F;
				n = 0.0F;
				o = 1.5F;
			} else if (foxEntity.isRollingHead()) {
				m = -0.225F;
				o = 1.25F;
			}

			if (foxEntity.isCrouching()) {
				o += foxEntity.getBodyRotationHeightOffset(h) / (float)(bl2 ? 22 : 16);
			}

			GlStateManager.pushMatrix();
			GlStateManager.translatef(m, o, n);
			GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
			if (bl) {
				GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
			}

			GlStateManager.rotatef(foxEntity.getHeadRoll(h) * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
			MinecraftClient.getInstance().getItemRenderer().renderHeldItem(itemStack, foxEntity, ModelTransformation.Type.field_4318, false);
			GlStateManager.popMatrix();
		}
	}

	@Override
	public boolean method_4200() {
		return false;
	}
}

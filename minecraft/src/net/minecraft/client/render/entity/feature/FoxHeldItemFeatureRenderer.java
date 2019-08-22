package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.model.FoxEntityModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)
public class FoxHeldItemFeatureRenderer extends FeatureRenderer<FoxEntity, FoxEntityModel<FoxEntity>> {
	public FoxHeldItemFeatureRenderer(FeatureRendererContext<FoxEntity, FoxEntityModel<FoxEntity>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_18335(FoxEntity foxEntity, float f, float g, float h, float i, float j, float k, float l) {
		ItemStack itemStack = foxEntity.getEquippedStack(EquipmentSlot.MAINHAND);
		if (!itemStack.isEmpty()) {
			boolean bl = foxEntity.isSleeping();
			boolean bl2 = foxEntity.isBaby();
			RenderSystem.pushMatrix();
			if (bl2) {
				float m = 0.75F;
				RenderSystem.scalef(0.75F, 0.75F, 0.75F);
				RenderSystem.translatef(0.0F, 8.0F * l, 3.35F * l);
			}

			RenderSystem.translatef(
				this.getModel().head.rotationPointX / 16.0F, this.getModel().head.rotationPointY / 16.0F, this.getModel().head.rotationPointZ / 16.0F
			);
			float m = foxEntity.getHeadRoll(h) * (180.0F / (float)Math.PI);
			RenderSystem.rotatef(m, 0.0F, 0.0F, 1.0F);
			RenderSystem.rotatef(j, 0.0F, 1.0F, 0.0F);
			RenderSystem.rotatef(k, 1.0F, 0.0F, 0.0F);
			if (foxEntity.isBaby()) {
				if (bl) {
					RenderSystem.translatef(0.4F, 0.26F, 0.15F);
				} else {
					RenderSystem.translatef(0.06F, 0.26F, -0.5F);
				}
			} else if (bl) {
				RenderSystem.translatef(0.46F, 0.26F, 0.22F);
			} else {
				RenderSystem.translatef(0.06F, 0.27F, -0.5F);
			}

			RenderSystem.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
			if (bl) {
				RenderSystem.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
			}

			MinecraftClient.getInstance().getItemRenderer().renderHeldItem(itemStack, foxEntity, ModelTransformation.Type.GROUND, false);
			RenderSystem.popMatrix();
		}
	}

	@Override
	public boolean hasHurtOverlay() {
		return false;
	}
}

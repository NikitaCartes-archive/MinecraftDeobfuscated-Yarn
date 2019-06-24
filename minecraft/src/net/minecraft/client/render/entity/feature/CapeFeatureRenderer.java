package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class CapeFeatureRenderer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
	public CapeFeatureRenderer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_4177(AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, float h, float i, float j, float k, float l) {
		if (abstractClientPlayerEntity.canRenderCapeTexture()
			&& !abstractClientPlayerEntity.isInvisible()
			&& abstractClientPlayerEntity.isSkinOverlayVisible(PlayerModelPart.CAPE)
			&& abstractClientPlayerEntity.getCapeTexture() != null) {
			ItemStack itemStack = abstractClientPlayerEntity.getEquippedStack(EquipmentSlot.CHEST);
			if (itemStack.getItem() != Items.ELYTRA) {
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				this.bindTexture(abstractClientPlayerEntity.getCapeTexture());
				GlStateManager.pushMatrix();
				GlStateManager.translatef(0.0F, 0.0F, 0.125F);
				double d = MathHelper.lerp((double)h, abstractClientPlayerEntity.field_7524, abstractClientPlayerEntity.field_7500)
					- MathHelper.lerp((double)h, abstractClientPlayerEntity.prevX, abstractClientPlayerEntity.x);
				double e = MathHelper.lerp((double)h, abstractClientPlayerEntity.field_7502, abstractClientPlayerEntity.field_7521)
					- MathHelper.lerp((double)h, abstractClientPlayerEntity.prevY, abstractClientPlayerEntity.y);
				double m = MathHelper.lerp((double)h, abstractClientPlayerEntity.field_7522, abstractClientPlayerEntity.field_7499)
					- MathHelper.lerp((double)h, abstractClientPlayerEntity.prevZ, abstractClientPlayerEntity.z);
				float n = abstractClientPlayerEntity.field_6220 + (abstractClientPlayerEntity.field_6283 - abstractClientPlayerEntity.field_6220);
				double o = (double)MathHelper.sin(n * (float) (Math.PI / 180.0));
				double p = (double)(-MathHelper.cos(n * (float) (Math.PI / 180.0)));
				float q = (float)e * 10.0F;
				q = MathHelper.clamp(q, -6.0F, 32.0F);
				float r = (float)(d * o + m * p) * 100.0F;
				r = MathHelper.clamp(r, 0.0F, 150.0F);
				float s = (float)(d * p - m * o) * 100.0F;
				s = MathHelper.clamp(s, -20.0F, 20.0F);
				if (r < 0.0F) {
					r = 0.0F;
				}

				float t = MathHelper.lerp(h, abstractClientPlayerEntity.field_7505, abstractClientPlayerEntity.field_7483);
				q += MathHelper.sin(MathHelper.lerp(h, abstractClientPlayerEntity.prevHorizontalSpeed, abstractClientPlayerEntity.horizontalSpeed) * 6.0F) * 32.0F * t;
				if (abstractClientPlayerEntity.isInSneakingPose()) {
					q += 25.0F;
				}

				GlStateManager.rotatef(6.0F + r / 2.0F + q, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotatef(s / 2.0F, 0.0F, 0.0F, 1.0F);
				GlStateManager.rotatef(-s / 2.0F, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
				this.getModel().renderCape(0.0625F);
				GlStateManager.popMatrix();
			}
		}
	}

	@Override
	public boolean hasHurtOverlay() {
		return false;
	}
}

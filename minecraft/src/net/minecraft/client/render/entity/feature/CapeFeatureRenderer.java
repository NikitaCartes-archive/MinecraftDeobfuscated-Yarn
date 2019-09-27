package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class CapeFeatureRenderer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
	public CapeFeatureRenderer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_4177(
		class_4587 arg, class_4597 arg2, int i, AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, float h, float j, float k, float l, float m
	) {
		if (abstractClientPlayerEntity.canRenderCapeTexture()
			&& !abstractClientPlayerEntity.isInvisible()
			&& abstractClientPlayerEntity.isSkinOverlayVisible(PlayerModelPart.CAPE)
			&& abstractClientPlayerEntity.getCapeTexture() != null) {
			ItemStack itemStack = abstractClientPlayerEntity.getEquippedStack(EquipmentSlot.CHEST);
			if (itemStack.getItem() != Items.ELYTRA) {
				arg.method_22903();
				arg.method_22904(0.0, 0.0, 0.125);
				double d = MathHelper.lerp((double)h, abstractClientPlayerEntity.field_7524, abstractClientPlayerEntity.field_7500)
					- MathHelper.lerp((double)h, abstractClientPlayerEntity.prevX, abstractClientPlayerEntity.x);
				double e = MathHelper.lerp((double)h, abstractClientPlayerEntity.field_7502, abstractClientPlayerEntity.field_7521)
					- MathHelper.lerp((double)h, abstractClientPlayerEntity.prevY, abstractClientPlayerEntity.y);
				double n = MathHelper.lerp((double)h, abstractClientPlayerEntity.field_7522, abstractClientPlayerEntity.field_7499)
					- MathHelper.lerp((double)h, abstractClientPlayerEntity.prevZ, abstractClientPlayerEntity.z);
				float o = abstractClientPlayerEntity.prevBodyYaw + (abstractClientPlayerEntity.bodyYaw - abstractClientPlayerEntity.prevBodyYaw);
				double p = (double)MathHelper.sin(o * (float) (Math.PI / 180.0));
				double q = (double)(-MathHelper.cos(o * (float) (Math.PI / 180.0)));
				float r = (float)e * 10.0F;
				r = MathHelper.clamp(r, -6.0F, 32.0F);
				float s = (float)(d * p + n * q) * 100.0F;
				s = MathHelper.clamp(s, 0.0F, 150.0F);
				float t = (float)(d * q - n * p) * 100.0F;
				t = MathHelper.clamp(t, -20.0F, 20.0F);
				if (s < 0.0F) {
					s = 0.0F;
				}

				float u = MathHelper.lerp(h, abstractClientPlayerEntity.field_7505, abstractClientPlayerEntity.field_7483);
				r += MathHelper.sin(MathHelper.lerp(h, abstractClientPlayerEntity.prevHorizontalSpeed, abstractClientPlayerEntity.horizontalSpeed) * 6.0F) * 32.0F * u;
				if (abstractClientPlayerEntity.isInSneakingPose()) {
					r += 25.0F;
				}

				arg.method_22907(Vector3f.field_20703.method_23214(6.0F + s / 2.0F + r, true));
				arg.method_22907(Vector3f.field_20707.method_23214(t / 2.0F, true));
				arg.method_22907(Vector3f.field_20705.method_23214(180.0F - t / 2.0F, true));
				class_4588 lv = arg2.getBuffer(BlockRenderLayer.method_23017(abstractClientPlayerEntity.getCapeTexture()));
				class_4608.method_23211(lv);
				this.getModel().renderCape(arg, lv, 0.0625F, i);
				lv.method_22923();
				arg.method_22909();
			}
		}
	}
}

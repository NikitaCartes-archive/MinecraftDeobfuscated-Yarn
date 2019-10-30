package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class CapeFeatureRenderer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
	public CapeFeatureRenderer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context) {
		super(context);
	}

	public void method_4177(
		MatrixStack matrixStack,
		VertexConsumerProvider vertexConsumerProvider,
		int i,
		AbstractClientPlayerEntity abstractClientPlayerEntity,
		float f,
		float g,
		float h,
		float j,
		float k,
		float l,
		float m
	) {
		if (abstractClientPlayerEntity.canRenderCapeTexture()
			&& !abstractClientPlayerEntity.isInvisible()
			&& abstractClientPlayerEntity.isSkinOverlayVisible(PlayerModelPart.CAPE)
			&& abstractClientPlayerEntity.getCapeTexture() != null) {
			ItemStack itemStack = abstractClientPlayerEntity.getEquippedStack(EquipmentSlot.CHEST);
			if (itemStack.getItem() != Items.ELYTRA) {
				matrixStack.push();
				matrixStack.translate(0.0, 0.0, 0.125);
				double d = MathHelper.lerp((double)h, abstractClientPlayerEntity.field_7524, abstractClientPlayerEntity.field_7500)
					- MathHelper.lerp((double)h, abstractClientPlayerEntity.prevX, abstractClientPlayerEntity.getX());
				double e = MathHelper.lerp((double)h, abstractClientPlayerEntity.field_7502, abstractClientPlayerEntity.field_7521)
					- MathHelper.lerp((double)h, abstractClientPlayerEntity.prevY, abstractClientPlayerEntity.getY());
				double n = MathHelper.lerp((double)h, abstractClientPlayerEntity.field_7522, abstractClientPlayerEntity.field_7499)
					- MathHelper.lerp((double)h, abstractClientPlayerEntity.prevZ, abstractClientPlayerEntity.getZ());
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

				matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(6.0F + s / 2.0F + r));
				matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(t / 2.0F));
				matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(180.0F - t / 2.0F));
				VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntitySolid(abstractClientPlayerEntity.getCapeTexture()));
				this.getModel().renderCape(matrixStack, vertexConsumer, 0.0625F, i, OverlayTexture.DEFAULT_UV);
				matrixStack.pop();
			}
		}
	}
}

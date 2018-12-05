package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class DeadmauEntityRenderer5 implements LayerEntityRenderer<AbstractClientPlayerEntity> {
	private final PlayerEntityRenderer field_4849;

	public DeadmauEntityRenderer5(PlayerEntityRenderer playerEntityRenderer) {
		this.field_4849 = playerEntityRenderer;
	}

	public void render(AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, float h, float i, float j, float k, float l) {
		if ("deadmau5".equals(abstractClientPlayerEntity.getName().getString())
			&& abstractClientPlayerEntity.method_3127()
			&& !abstractClientPlayerEntity.isInvisible()) {
			this.field_4849.bindTexture(abstractClientPlayerEntity.method_3117());

			for (int m = 0; m < 2; m++) {
				float n = MathHelper.lerp(h, abstractClientPlayerEntity.prevYaw, abstractClientPlayerEntity.yaw)
					- MathHelper.lerp(h, abstractClientPlayerEntity.field_6220, abstractClientPlayerEntity.field_6283);
				float o = MathHelper.lerp(h, abstractClientPlayerEntity.prevPitch, abstractClientPlayerEntity.pitch);
				GlStateManager.pushMatrix();
				GlStateManager.rotatef(n, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotatef(o, 1.0F, 0.0F, 0.0F);
				GlStateManager.translatef(0.375F * (float)(m * 2 - 1), 0.0F, 0.0F);
				GlStateManager.translatef(0.0F, -0.375F, 0.0F);
				GlStateManager.rotatef(-o, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotatef(-n, 0.0F, 1.0F, 0.0F);
				float p = 1.3333334F;
				GlStateManager.scalef(1.3333334F, 1.3333334F, 1.3333334F);
				this.field_4849.method_4214().method_2824(0.0625F);
				GlStateManager.popMatrix();
			}
		}
	}

	@Override
	public boolean shouldMergeTextures() {
		return true;
	}
}

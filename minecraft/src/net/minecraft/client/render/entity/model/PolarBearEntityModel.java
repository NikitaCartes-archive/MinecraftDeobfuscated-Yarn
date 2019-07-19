package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.passive.PolarBearEntity;

@Environment(EnvType.CLIENT)
public class PolarBearEntityModel<T extends PolarBearEntity> extends QuadrupedEntityModel<T> {
	public PolarBearEntityModel() {
		super(12, 0.0F);
		this.textureWidth = 128;
		this.textureHeight = 64;
		this.head = new ModelPart(this, 0, 0);
		this.head.addCuboid(-3.5F, -3.0F, -3.0F, 7, 7, 7, 0.0F);
		this.head.setPivot(0.0F, 10.0F, -16.0F);
		this.head.setTextureOffset(0, 44).addCuboid(-2.5F, 1.0F, -6.0F, 5, 3, 3, 0.0F);
		this.head.setTextureOffset(26, 0).addCuboid(-4.5F, -4.0F, -1.0F, 2, 2, 1, 0.0F);
		ModelPart modelPart = this.head.setTextureOffset(26, 0);
		modelPart.mirror = true;
		modelPart.addCuboid(2.5F, -4.0F, -1.0F, 2, 2, 1, 0.0F);
		this.torso = new ModelPart(this);
		this.torso.setTextureOffset(0, 19).addCuboid(-5.0F, -13.0F, -7.0F, 14, 14, 11, 0.0F);
		this.torso.setTextureOffset(39, 0).addCuboid(-4.0F, -25.0F, -7.0F, 12, 12, 10, 0.0F);
		this.torso.setPivot(-2.0F, 9.0F, 12.0F);
		int i = 10;
		this.backRightLeg = new ModelPart(this, 50, 22);
		this.backRightLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4, 10, 8, 0.0F);
		this.backRightLeg.setPivot(-3.5F, 14.0F, 6.0F);
		this.backLeftLeg = new ModelPart(this, 50, 22);
		this.backLeftLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4, 10, 8, 0.0F);
		this.backLeftLeg.setPivot(3.5F, 14.0F, 6.0F);
		this.frontRightLeg = new ModelPart(this, 50, 40);
		this.frontRightLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4, 10, 6, 0.0F);
		this.frontRightLeg.setPivot(-2.5F, 14.0F, -7.0F);
		this.frontLeftLeg = new ModelPart(this, 50, 40);
		this.frontLeftLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4, 10, 6, 0.0F);
		this.frontLeftLeg.setPivot(2.5F, 14.0F, -7.0F);
		this.backRightLeg.pivotX--;
		this.backLeftLeg.pivotX++;
		this.backRightLeg.pivotZ += 0.0F;
		this.backLeftLeg.pivotZ += 0.0F;
		this.frontRightLeg.pivotX--;
		this.frontLeftLeg.pivotX++;
		this.frontRightLeg.pivotZ--;
		this.frontLeftLeg.pivotZ--;
		this.field_3537 += 2.0F;
	}

	public void render(T polarBearEntity, float f, float g, float h, float i, float j, float k) {
		this.setAngles(polarBearEntity, f, g, h, i, j, k);
		if (this.child) {
			float l = 2.0F;
			this.field_3540 = 16.0F;
			this.field_3537 = 4.0F;
			GlStateManager.pushMatrix();
			GlStateManager.scalef(0.6666667F, 0.6666667F, 0.6666667F);
			GlStateManager.translatef(0.0F, this.field_3540 * k, this.field_3537 * k);
			this.head.render(k);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			GlStateManager.scalef(0.5F, 0.5F, 0.5F);
			GlStateManager.translatef(0.0F, 24.0F * k, 0.0F);
			this.torso.render(k);
			this.backRightLeg.render(k);
			this.backLeftLeg.render(k);
			this.frontRightLeg.render(k);
			this.frontLeftLeg.render(k);
			GlStateManager.popMatrix();
		} else {
			this.head.render(k);
			this.torso.render(k);
			this.backRightLeg.render(k);
			this.backLeftLeg.render(k);
			this.frontRightLeg.render(k);
			this.frontLeftLeg.render(k);
		}
	}

	public void setAngles(T polarBearEntity, float f, float g, float h, float i, float j, float k) {
		super.setAngles(polarBearEntity, f, g, h, i, j, k);
		float l = h - (float)polarBearEntity.age;
		float m = polarBearEntity.getWarningAnimationProgress(l);
		m *= m;
		float n = 1.0F - m;
		this.torso.pitch = (float) (Math.PI / 2) - m * (float) Math.PI * 0.35F;
		this.torso.pivotY = 9.0F * n + 11.0F * m;
		this.frontRightLeg.pivotY = 14.0F * n - 6.0F * m;
		this.frontRightLeg.pivotZ = -8.0F * n - 4.0F * m;
		this.frontRightLeg.pitch -= m * (float) Math.PI * 0.45F;
		this.frontLeftLeg.pivotY = this.frontRightLeg.pivotY;
		this.frontLeftLeg.pivotZ = this.frontRightLeg.pivotZ;
		this.frontLeftLeg.pitch -= m * (float) Math.PI * 0.45F;
		if (this.child) {
			this.head.pivotY = 10.0F * n - 9.0F * m;
			this.head.pivotZ = -16.0F * n - 7.0F * m;
		} else {
			this.head.pivotY = 10.0F * n - 14.0F * m;
			this.head.pivotZ = -16.0F * n - 3.0F * m;
		}

		this.head.pitch += m * (float) Math.PI * 0.15F;
	}
}

package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.passive.AbstractDonkeyEntity;

@Environment(EnvType.CLIENT)
public class LlamaEntityModel<T extends AbstractDonkeyEntity> extends QuadrupedEntityModel<T> {
	private final ModelPart field_3430;
	private final ModelPart field_3429;

	public LlamaEntityModel(float f) {
		super(15, f);
		this.textureWidth = 128;
		this.textureHeight = 64;
		this.head = new ModelPart(this, 0, 0);
		this.head.addCuboid(-2.0F, -14.0F, -10.0F, 4, 4, 9, f);
		this.head.setPivot(0.0F, 7.0F, -6.0F);
		this.head.setTextureOffset(0, 14).addCuboid(-4.0F, -16.0F, -6.0F, 8, 18, 6, f);
		this.head.setTextureOffset(17, 0).addCuboid(-4.0F, -19.0F, -4.0F, 3, 3, 2, f);
		this.head.setTextureOffset(17, 0).addCuboid(1.0F, -19.0F, -4.0F, 3, 3, 2, f);
		this.torso = new ModelPart(this, 29, 0);
		this.torso.addCuboid(-6.0F, -10.0F, -7.0F, 12, 18, 10, f);
		this.torso.setPivot(0.0F, 5.0F, 2.0F);
		this.field_3430 = new ModelPart(this, 45, 28);
		this.field_3430.addCuboid(-3.0F, 0.0F, 0.0F, 8, 8, 3, f);
		this.field_3430.setPivot(-8.5F, 3.0F, 3.0F);
		this.field_3430.yaw = (float) (Math.PI / 2);
		this.field_3429 = new ModelPart(this, 45, 41);
		this.field_3429.addCuboid(-3.0F, 0.0F, 0.0F, 8, 8, 3, f);
		this.field_3429.setPivot(5.5F, 3.0F, 3.0F);
		this.field_3429.yaw = (float) (Math.PI / 2);
		int i = 4;
		int j = 14;
		this.backRightLeg = new ModelPart(this, 29, 29);
		this.backRightLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4, 14, 4, f);
		this.backRightLeg.setPivot(-2.5F, 10.0F, 6.0F);
		this.backLeftLeg = new ModelPart(this, 29, 29);
		this.backLeftLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4, 14, 4, f);
		this.backLeftLeg.setPivot(2.5F, 10.0F, 6.0F);
		this.frontRightLeg = new ModelPart(this, 29, 29);
		this.frontRightLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4, 14, 4, f);
		this.frontRightLeg.setPivot(-2.5F, 10.0F, -4.0F);
		this.frontLeftLeg = new ModelPart(this, 29, 29);
		this.frontLeftLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4, 14, 4, f);
		this.frontLeftLeg.setPivot(2.5F, 10.0F, -4.0F);
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

	public void render(T abstractDonkeyEntity, float f, float g, float h, float i, float j, float k) {
		boolean bl = !abstractDonkeyEntity.isBaby() && abstractDonkeyEntity.hasChest();
		this.setAngles(abstractDonkeyEntity, f, g, h, i, j, k);
		if (this.child) {
			float l = 2.0F;
			GlStateManager.pushMatrix();
			GlStateManager.translatef(0.0F, this.field_3540 * k, this.field_3537 * k);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			float m = 0.7F;
			GlStateManager.scalef(0.71428573F, 0.64935064F, 0.7936508F);
			GlStateManager.translatef(0.0F, 21.0F * k, 0.22F);
			this.head.render(k);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			float n = 1.1F;
			GlStateManager.scalef(0.625F, 0.45454544F, 0.45454544F);
			GlStateManager.translatef(0.0F, 33.0F * k, 0.0F);
			this.torso.render(k);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			GlStateManager.scalef(0.45454544F, 0.41322312F, 0.45454544F);
			GlStateManager.translatef(0.0F, 33.0F * k, 0.0F);
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

		if (bl) {
			this.field_3430.render(k);
			this.field_3429.render(k);
		}
	}
}

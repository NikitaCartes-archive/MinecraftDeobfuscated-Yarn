package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class PandaEntityModel<T extends PandaEntity> extends QuadrupedEntityModel<T> {
	private float scaredAnimationProgress;
	private float lieOnBackAnimationProgress;
	private float playAnimationProgress;

	public PandaEntityModel(int i, float f) {
		super(i, f);
		this.textureWidth = 64;
		this.textureHeight = 64;
		this.head = new ModelPart(this, 0, 6);
		this.head.addCuboid(-6.5F, -5.0F, -4.0F, 13, 10, 9);
		this.head.setPivot(0.0F, 11.5F, -17.0F);
		this.head.setTextureOffset(45, 16).addCuboid(-3.5F, 0.0F, -6.0F, 7, 5, 2);
		this.head.setTextureOffset(52, 25).addCuboid(-8.5F, -8.0F, -1.0F, 5, 4, 1);
		this.head.setTextureOffset(52, 25).addCuboid(3.5F, -8.0F, -1.0F, 5, 4, 1);
		this.torso = new ModelPart(this, 0, 25);
		this.torso.addCuboid(-9.5F, -13.0F, -6.5F, 19, 26, 13);
		this.torso.setPivot(0.0F, 10.0F, 0.0F);
		int j = 9;
		int k = 6;
		this.backRightLeg = new ModelPart(this, 40, 0);
		this.backRightLeg.addCuboid(-3.0F, 0.0F, -3.0F, 6, 9, 6);
		this.backRightLeg.setPivot(-5.5F, 15.0F, 9.0F);
		this.backLeftLeg = new ModelPart(this, 40, 0);
		this.backLeftLeg.addCuboid(-3.0F, 0.0F, -3.0F, 6, 9, 6);
		this.backLeftLeg.setPivot(5.5F, 15.0F, 9.0F);
		this.frontRightLeg = new ModelPart(this, 40, 0);
		this.frontRightLeg.addCuboid(-3.0F, 0.0F, -3.0F, 6, 9, 6);
		this.frontRightLeg.setPivot(-5.5F, 15.0F, -9.0F);
		this.frontLeftLeg = new ModelPart(this, 40, 0);
		this.frontLeftLeg.addCuboid(-3.0F, 0.0F, -3.0F, 6, 9, 6);
		this.frontLeftLeg.setPivot(5.5F, 15.0F, -9.0F);
	}

	public void animateModel(T pandaEntity, float f, float g, float h) {
		super.animateModel(pandaEntity, f, g, h);
		this.scaredAnimationProgress = pandaEntity.getScaredAnimationProgress(h);
		this.lieOnBackAnimationProgress = pandaEntity.getLieOnBackAnimationProgress(h);
		this.playAnimationProgress = pandaEntity.isBaby() ? 0.0F : pandaEntity.getRollOverAnimationProgress(h);
	}

	public void setAngles(T pandaEntity, float f, float g, float h, float i, float j, float k) {
		super.setAngles(pandaEntity, f, g, h, i, j, k);
		boolean bl = pandaEntity.getAskForBambooTicks() > 0;
		boolean bl2 = pandaEntity.isSneezing();
		int l = pandaEntity.getSneezeProgress();
		boolean bl3 = pandaEntity.isEating();
		boolean bl4 = pandaEntity.method_6524();
		if (bl) {
			this.head.yaw = 0.35F * MathHelper.sin(0.6F * h);
			this.head.roll = 0.35F * MathHelper.sin(0.6F * h);
			this.frontRightLeg.pitch = -0.75F * MathHelper.sin(0.3F * h);
			this.frontLeftLeg.pitch = 0.75F * MathHelper.sin(0.3F * h);
		} else {
			this.head.roll = 0.0F;
		}

		if (bl2) {
			if (l < 15) {
				this.head.pitch = (float) (-Math.PI / 4) * (float)l / 14.0F;
			} else if (l < 20) {
				float m = (float)((l - 15) / 5);
				this.head.pitch = (float) (-Math.PI / 4) + (float) (Math.PI / 4) * m;
			}
		}

		if (this.scaredAnimationProgress > 0.0F) {
			this.torso.pitch = this.interpolateAngle(this.torso.pitch, 1.7407963F, this.scaredAnimationProgress);
			this.head.pitch = this.interpolateAngle(this.head.pitch, (float) (Math.PI / 2), this.scaredAnimationProgress);
			this.frontRightLeg.roll = -0.27079642F;
			this.frontLeftLeg.roll = 0.27079642F;
			this.backRightLeg.roll = 0.5707964F;
			this.backLeftLeg.roll = -0.5707964F;
			if (bl3) {
				this.head.pitch = (float) (Math.PI / 2) + 0.2F * MathHelper.sin(h * 0.6F);
				this.frontRightLeg.pitch = -0.4F - 0.2F * MathHelper.sin(h * 0.6F);
				this.frontLeftLeg.pitch = -0.4F - 0.2F * MathHelper.sin(h * 0.6F);
			}

			if (bl4) {
				this.head.pitch = 2.1707964F;
				this.frontRightLeg.pitch = -0.9F;
				this.frontLeftLeg.pitch = -0.9F;
			}
		} else {
			this.backRightLeg.roll = 0.0F;
			this.backLeftLeg.roll = 0.0F;
			this.frontRightLeg.roll = 0.0F;
			this.frontLeftLeg.roll = 0.0F;
		}

		if (this.lieOnBackAnimationProgress > 0.0F) {
			this.backRightLeg.pitch = -0.6F * MathHelper.sin(h * 0.15F);
			this.backLeftLeg.pitch = 0.6F * MathHelper.sin(h * 0.15F);
			this.frontRightLeg.pitch = 0.3F * MathHelper.sin(h * 0.25F);
			this.frontLeftLeg.pitch = -0.3F * MathHelper.sin(h * 0.25F);
			this.head.pitch = this.interpolateAngle(this.head.pitch, (float) (Math.PI / 2), this.lieOnBackAnimationProgress);
		}

		if (this.playAnimationProgress > 0.0F) {
			this.head.pitch = this.interpolateAngle(this.head.pitch, 2.0561945F, this.playAnimationProgress);
			this.backRightLeg.pitch = -0.5F * MathHelper.sin(h * 0.5F);
			this.backLeftLeg.pitch = 0.5F * MathHelper.sin(h * 0.5F);
			this.frontRightLeg.pitch = 0.5F * MathHelper.sin(h * 0.5F);
			this.frontLeftLeg.pitch = -0.5F * MathHelper.sin(h * 0.5F);
		}
	}

	protected float interpolateAngle(float angle1, float angle2, float progress) {
		float f = angle2 - angle1;

		while (f < (float) -Math.PI) {
			f += (float) (Math.PI * 2);
		}

		while (f >= (float) Math.PI) {
			f -= (float) (Math.PI * 2);
		}

		return angle1 + progress * f;
	}

	public void render(T pandaEntity, float f, float g, float h, float i, float j, float k) {
		this.setAngles(pandaEntity, f, g, h, i, j, k);
		if (this.child) {
			float l = 3.0F;
			GlStateManager.pushMatrix();
			GlStateManager.translatef(0.0F, this.field_3540 * k, this.field_3537 * k);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			float m = 0.6F;
			GlStateManager.scalef(0.5555555F, 0.5555555F, 0.5555555F);
			GlStateManager.translatef(0.0F, 23.0F * k, 0.3F);
			this.head.render(k);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			GlStateManager.scalef(0.33333334F, 0.33333334F, 0.33333334F);
			GlStateManager.translatef(0.0F, 49.0F * k, 0.0F);
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
}

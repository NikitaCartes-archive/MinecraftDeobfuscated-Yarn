package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelUtil;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class PandaEntityModel<T extends PandaEntity> extends QuadrupedEntityModel<T> {
	private float scaredAnimationProgress;
	private float lieOnBackAnimationProgress;
	private float playAnimationProgress;

	public PandaEntityModel(int legHeight, float scale) {
		super(legHeight, scale, true, 23.0F, 4.8F, 2.7F, 3.0F, 49);
		this.textureWidth = 64;
		this.textureHeight = 64;
		this.head = new ModelPart(this, 0, 6);
		this.head.addCuboid(-6.5F, -5.0F, -4.0F, 13.0F, 10.0F, 9.0F);
		this.head.setPivot(0.0F, 11.5F, -17.0F);
		this.head.setTextureOffset(45, 16).addCuboid(-3.5F, 0.0F, -6.0F, 7.0F, 5.0F, 2.0F);
		this.head.setTextureOffset(52, 25).addCuboid(-8.5F, -8.0F, -1.0F, 5.0F, 4.0F, 1.0F);
		this.head.setTextureOffset(52, 25).addCuboid(3.5F, -8.0F, -1.0F, 5.0F, 4.0F, 1.0F);
		this.torso = new ModelPart(this, 0, 25);
		this.torso.addCuboid(-9.5F, -13.0F, -6.5F, 19.0F, 26.0F, 13.0F);
		this.torso.setPivot(0.0F, 10.0F, 0.0F);
		int i = 9;
		int j = 6;
		this.backRightLeg = new ModelPart(this, 40, 0);
		this.backRightLeg.addCuboid(-3.0F, 0.0F, -3.0F, 6.0F, 9.0F, 6.0F);
		this.backRightLeg.setPivot(-5.5F, 15.0F, 9.0F);
		this.backLeftLeg = new ModelPart(this, 40, 0);
		this.backLeftLeg.addCuboid(-3.0F, 0.0F, -3.0F, 6.0F, 9.0F, 6.0F);
		this.backLeftLeg.setPivot(5.5F, 15.0F, 9.0F);
		this.frontRightLeg = new ModelPart(this, 40, 0);
		this.frontRightLeg.addCuboid(-3.0F, 0.0F, -3.0F, 6.0F, 9.0F, 6.0F);
		this.frontRightLeg.setPivot(-5.5F, 15.0F, -9.0F);
		this.frontLeftLeg = new ModelPart(this, 40, 0);
		this.frontLeftLeg.addCuboid(-3.0F, 0.0F, -3.0F, 6.0F, 9.0F, 6.0F);
		this.frontLeftLeg.setPivot(5.5F, 15.0F, -9.0F);
	}

	public void method_17102(T pandaEntity, float f, float g, float h) {
		super.animateModel(pandaEntity, f, g, h);
		this.scaredAnimationProgress = pandaEntity.getScaredAnimationProgress(h);
		this.lieOnBackAnimationProgress = pandaEntity.getLieOnBackAnimationProgress(h);
		this.playAnimationProgress = pandaEntity.isBaby() ? 0.0F : pandaEntity.getRollOverAnimationProgress(h);
	}

	public void method_17103(T pandaEntity, float f, float g, float h, float i, float j) {
		super.setAngles(pandaEntity, f, g, h, i, j);
		boolean bl = pandaEntity.getAskForBambooTicks() > 0;
		boolean bl2 = pandaEntity.isSneezing();
		int k = pandaEntity.getSneezeProgress();
		boolean bl3 = pandaEntity.isEating();
		boolean bl4 = pandaEntity.isScaredByThunderstorm();
		if (bl) {
			this.head.yaw = 0.35F * MathHelper.sin(0.6F * h);
			this.head.roll = 0.35F * MathHelper.sin(0.6F * h);
			this.frontRightLeg.pitch = -0.75F * MathHelper.sin(0.3F * h);
			this.frontLeftLeg.pitch = 0.75F * MathHelper.sin(0.3F * h);
		} else {
			this.head.roll = 0.0F;
		}

		if (bl2) {
			if (k < 15) {
				this.head.pitch = (float) (-Math.PI / 4) * (float)k / 14.0F;
			} else if (k < 20) {
				float l = (float)((k - 15) / 5);
				this.head.pitch = (float) (-Math.PI / 4) + (float) (Math.PI / 4) * l;
			}
		}

		if (this.scaredAnimationProgress > 0.0F) {
			this.torso.pitch = ModelUtil.interpolateAngle(this.torso.pitch, 1.7407963F, this.scaredAnimationProgress);
			this.head.pitch = ModelUtil.interpolateAngle(this.head.pitch, (float) (Math.PI / 2), this.scaredAnimationProgress);
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
			this.head.pitch = ModelUtil.interpolateAngle(this.head.pitch, (float) (Math.PI / 2), this.lieOnBackAnimationProgress);
		}

		if (this.playAnimationProgress > 0.0F) {
			this.head.pitch = ModelUtil.interpolateAngle(this.head.pitch, 2.0561945F, this.playAnimationProgress);
			this.backRightLeg.pitch = -0.5F * MathHelper.sin(h * 0.5F);
			this.backLeftLeg.pitch = 0.5F * MathHelper.sin(h * 0.5F);
			this.frontRightLeg.pitch = 0.5F * MathHelper.sin(h * 0.5F);
			this.frontLeftLeg.pitch = -0.5F * MathHelper.sin(h * 0.5F);
		}
	}
}

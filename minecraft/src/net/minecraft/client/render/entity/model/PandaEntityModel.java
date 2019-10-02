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

	public PandaEntityModel(int i, float f) {
		super(i, f, true, 23.0F, 4.8F, 2.7F, 3.0F, 49);
		this.textureWidth = 64;
		this.textureHeight = 64;
		this.head = new ModelPart(this, 0, 6);
		this.head.addCuboid(-6.5F, -5.0F, -4.0F, 13.0F, 10.0F, 9.0F);
		this.head.setPivot(0.0F, 11.5F, -17.0F);
		this.head.setTextureOffset(45, 16).addCuboid(-3.5F, 0.0F, -6.0F, 7.0F, 5.0F, 2.0F);
		this.head.setTextureOffset(52, 25).addCuboid(-8.5F, -8.0F, -1.0F, 5.0F, 4.0F, 1.0F);
		this.head.setTextureOffset(52, 25).addCuboid(3.5F, -8.0F, -1.0F, 5.0F, 4.0F, 1.0F);
		this.body = new ModelPart(this, 0, 25);
		this.body.addCuboid(-9.5F, -13.0F, -6.5F, 19.0F, 26.0F, 13.0F);
		this.body.setPivot(0.0F, 10.0F, 0.0F);
		int j = 9;
		int k = 6;
		this.leg1 = new ModelPart(this, 40, 0);
		this.leg1.addCuboid(-3.0F, 0.0F, -3.0F, 6.0F, 9.0F, 6.0F);
		this.leg1.setPivot(-5.5F, 15.0F, 9.0F);
		this.leg2 = new ModelPart(this, 40, 0);
		this.leg2.addCuboid(-3.0F, 0.0F, -3.0F, 6.0F, 9.0F, 6.0F);
		this.leg2.setPivot(5.5F, 15.0F, 9.0F);
		this.leg3 = new ModelPart(this, 40, 0);
		this.leg3.addCuboid(-3.0F, 0.0F, -3.0F, 6.0F, 9.0F, 6.0F);
		this.leg3.setPivot(-5.5F, 15.0F, -9.0F);
		this.leg4 = new ModelPart(this, 40, 0);
		this.leg4.addCuboid(-3.0F, 0.0F, -3.0F, 6.0F, 9.0F, 6.0F);
		this.leg4.setPivot(5.5F, 15.0F, -9.0F);
	}

	public void method_17102(T pandaEntity, float f, float g, float h) {
		super.animateModel(pandaEntity, f, g, h);
		this.scaredAnimationProgress = pandaEntity.getScaredAnimationProgress(h);
		this.lieOnBackAnimationProgress = pandaEntity.getLieOnBackAnimationProgress(h);
		this.playAnimationProgress = pandaEntity.isBaby() ? 0.0F : pandaEntity.getRollOverAnimationProgress(h);
	}

	public void method_17103(T pandaEntity, float f, float g, float h, float i, float j, float k) {
		super.setAngles(pandaEntity, f, g, h, i, j, k);
		boolean bl = pandaEntity.getAskForBambooTicks() > 0;
		boolean bl2 = pandaEntity.isSneezing();
		int l = pandaEntity.getSneezeProgress();
		boolean bl3 = pandaEntity.isEating();
		boolean bl4 = pandaEntity.method_6524();
		if (bl) {
			this.head.yaw = 0.35F * MathHelper.sin(0.6F * h);
			this.head.roll = 0.35F * MathHelper.sin(0.6F * h);
			this.leg3.pitch = -0.75F * MathHelper.sin(0.3F * h);
			this.leg4.pitch = 0.75F * MathHelper.sin(0.3F * h);
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
			this.body.pitch = ModelUtil.interpolateAngle(this.body.pitch, 1.7407963F, this.scaredAnimationProgress);
			this.head.pitch = ModelUtil.interpolateAngle(this.head.pitch, (float) (Math.PI / 2), this.scaredAnimationProgress);
			this.leg3.roll = -0.27079642F;
			this.leg4.roll = 0.27079642F;
			this.leg1.roll = 0.5707964F;
			this.leg2.roll = -0.5707964F;
			if (bl3) {
				this.head.pitch = (float) (Math.PI / 2) + 0.2F * MathHelper.sin(h * 0.6F);
				this.leg3.pitch = -0.4F - 0.2F * MathHelper.sin(h * 0.6F);
				this.leg4.pitch = -0.4F - 0.2F * MathHelper.sin(h * 0.6F);
			}

			if (bl4) {
				this.head.pitch = 2.1707964F;
				this.leg3.pitch = -0.9F;
				this.leg4.pitch = -0.9F;
			}
		} else {
			this.leg1.roll = 0.0F;
			this.leg2.roll = 0.0F;
			this.leg3.roll = 0.0F;
			this.leg4.roll = 0.0F;
		}

		if (this.lieOnBackAnimationProgress > 0.0F) {
			this.leg1.pitch = -0.6F * MathHelper.sin(h * 0.15F);
			this.leg2.pitch = 0.6F * MathHelper.sin(h * 0.15F);
			this.leg3.pitch = 0.3F * MathHelper.sin(h * 0.25F);
			this.leg4.pitch = -0.3F * MathHelper.sin(h * 0.25F);
			this.head.pitch = ModelUtil.interpolateAngle(this.head.pitch, (float) (Math.PI / 2), this.lieOnBackAnimationProgress);
		}

		if (this.playAnimationProgress > 0.0F) {
			this.head.pitch = ModelUtil.interpolateAngle(this.head.pitch, 2.0561945F, this.playAnimationProgress);
			this.leg1.pitch = -0.5F * MathHelper.sin(h * 0.5F);
			this.leg2.pitch = 0.5F * MathHelper.sin(h * 0.5F);
			this.leg3.pitch = 0.5F * MathHelper.sin(h * 0.5F);
			this.leg4.pitch = -0.5F * MathHelper.sin(h * 0.5F);
		}
	}
}

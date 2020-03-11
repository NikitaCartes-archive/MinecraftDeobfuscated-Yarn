package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelUtil;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class BeeEntityModel<T extends BeeEntity> extends AnimalModel<T> {
	private final ModelPart body;
	private final ModelPart torso;
	private final ModelPart rightWing;
	private final ModelPart leftWing;
	private final ModelPart frontLegs;
	private final ModelPart middleLegs;
	private final ModelPart backLegs;
	private final ModelPart stinger;
	private final ModelPart leftAntenna;
	private final ModelPart rightAntenna;
	private float bodyPitch;

	public BeeEntityModel() {
		super(false, 24.0F, 0.0F);
		this.textureWidth = 64;
		this.textureHeight = 64;
		this.body = new ModelPart(this);
		this.body.setPivot(0.0F, 19.0F, 0.0F);
		this.torso = new ModelPart(this, 0, 0);
		this.torso.setPivot(0.0F, 0.0F, 0.0F);
		this.body.addChild(this.torso);
		this.torso.addCuboid(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 10.0F, 0.0F);
		this.stinger = new ModelPart(this, 26, 7);
		this.stinger.addCuboid(0.0F, -1.0F, 5.0F, 0.0F, 1.0F, 2.0F, 0.0F);
		this.torso.addChild(this.stinger);
		this.leftAntenna = new ModelPart(this, 2, 0);
		this.leftAntenna.setPivot(0.0F, -2.0F, -5.0F);
		this.leftAntenna.addCuboid(1.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F, 0.0F);
		this.rightAntenna = new ModelPart(this, 2, 3);
		this.rightAntenna.setPivot(0.0F, -2.0F, -5.0F);
		this.rightAntenna.addCuboid(-2.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F, 0.0F);
		this.torso.addChild(this.leftAntenna);
		this.torso.addChild(this.rightAntenna);
		this.rightWing = new ModelPart(this, 0, 18);
		this.rightWing.setPivot(-1.5F, -4.0F, -3.0F);
		this.rightWing.pitch = 0.0F;
		this.rightWing.yaw = -0.2618F;
		this.rightWing.roll = 0.0F;
		this.body.addChild(this.rightWing);
		this.rightWing.addCuboid(-9.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, 0.001F);
		this.leftWing = new ModelPart(this, 0, 18);
		this.leftWing.setPivot(1.5F, -4.0F, -3.0F);
		this.leftWing.pitch = 0.0F;
		this.leftWing.yaw = 0.2618F;
		this.leftWing.roll = 0.0F;
		this.leftWing.mirror = true;
		this.body.addChild(this.leftWing);
		this.leftWing.addCuboid(0.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, 0.001F);
		this.frontLegs = new ModelPart(this);
		this.frontLegs.setPivot(1.5F, 3.0F, -2.0F);
		this.body.addChild(this.frontLegs);
		this.frontLegs.addCuboid("frontLegBox", -5.0F, 0.0F, 0.0F, 7, 2, 0, 0.0F, 26, 1);
		this.middleLegs = new ModelPart(this);
		this.middleLegs.setPivot(1.5F, 3.0F, 0.0F);
		this.body.addChild(this.middleLegs);
		this.middleLegs.addCuboid("midLegBox", -5.0F, 0.0F, 0.0F, 7, 2, 0, 0.0F, 26, 3);
		this.backLegs = new ModelPart(this);
		this.backLegs.setPivot(1.5F, 3.0F, 2.0F);
		this.body.addChild(this.backLegs);
		this.backLegs.addCuboid("backLegBox", -5.0F, 0.0F, 0.0F, 7, 2, 0, 0.0F, 26, 5);
	}

	public void animateModel(T beeEntity, float f, float g, float h) {
		super.animateModel(beeEntity, f, g, h);
		this.bodyPitch = beeEntity.getBodyPitch(h);
		this.stinger.visible = !beeEntity.hasStung();
	}

	public void setAngles(T beeEntity, float f, float g, float h, float i, float j) {
		this.rightWing.pitch = 0.0F;
		this.leftAntenna.pitch = 0.0F;
		this.rightAntenna.pitch = 0.0F;
		this.body.pitch = 0.0F;
		this.body.pivotY = 19.0F;
		boolean bl = beeEntity.isOnGround() && beeEntity.getVelocity().lengthSquared() < 1.0E-7;
		if (bl) {
			this.rightWing.yaw = -0.2618F;
			this.rightWing.roll = 0.0F;
			this.leftWing.pitch = 0.0F;
			this.leftWing.yaw = 0.2618F;
			this.leftWing.roll = 0.0F;
			this.frontLegs.pitch = 0.0F;
			this.middleLegs.pitch = 0.0F;
			this.backLegs.pitch = 0.0F;
		} else {
			float k = h * 2.1F;
			this.rightWing.yaw = 0.0F;
			this.rightWing.roll = MathHelper.cos(k) * (float) Math.PI * 0.15F;
			this.leftWing.pitch = this.rightWing.pitch;
			this.leftWing.yaw = this.rightWing.yaw;
			this.leftWing.roll = -this.rightWing.roll;
			this.frontLegs.pitch = (float) (Math.PI / 4);
			this.middleLegs.pitch = (float) (Math.PI / 4);
			this.backLegs.pitch = (float) (Math.PI / 4);
			this.body.pitch = 0.0F;
			this.body.yaw = 0.0F;
			this.body.roll = 0.0F;
		}

		if (!beeEntity.isAngry()) {
			this.body.pitch = 0.0F;
			this.body.yaw = 0.0F;
			this.body.roll = 0.0F;
			if (!bl) {
				float k = MathHelper.cos(h * 0.18F);
				this.body.pitch = 0.1F + k * (float) Math.PI * 0.025F;
				this.leftAntenna.pitch = k * (float) Math.PI * 0.03F;
				this.rightAntenna.pitch = k * (float) Math.PI * 0.03F;
				this.frontLegs.pitch = -k * (float) Math.PI * 0.1F + (float) (Math.PI / 8);
				this.backLegs.pitch = -k * (float) Math.PI * 0.05F + (float) (Math.PI / 4);
				this.body.pivotY = 19.0F - MathHelper.cos(h * 0.18F) * 0.9F;
			}
		}

		if (this.bodyPitch > 0.0F) {
			this.body.pitch = ModelUtil.interpolateAngle(this.body.pitch, 3.0915928F, this.bodyPitch);
		}
	}

	@Override
	protected Iterable<ModelPart> getHeadParts() {
		return ImmutableList.<ModelPart>of();
	}

	@Override
	protected Iterable<ModelPart> getBodyParts() {
		return ImmutableList.<ModelPart>of(this.body);
	}
}

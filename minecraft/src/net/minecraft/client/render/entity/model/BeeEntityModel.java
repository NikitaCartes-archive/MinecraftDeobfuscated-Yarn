package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4592;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelUtil;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class BeeEntityModel<T extends BeeEntity> extends class_4592<T> {
	private final ModelPart body;
	private final ModelPart mainBody;
	private final ModelPart leftWing;
	private final ModelPart rightWing;
	private final ModelPart frontLeg;
	private final ModelPart midLeg;
	private final ModelPart backLeg;
	private final ModelPart stinger;
	private final ModelPart leftAntenna;
	private final ModelPart rightAntenna;
	private float bodyPitch;

	public BeeEntityModel() {
		super(RenderLayer::getEntityCutoutNoCull, false, 24.0F, 0.0F, 2.0F, 2.0F, 24.0F);
		this.textureWidth = 64;
		this.textureHeight = 64;
		this.body = new ModelPart(this);
		this.body.setPivot(0.0F, 19.0F, 0.0F);
		this.mainBody = new ModelPart(this, 0, 0);
		this.mainBody.setPivot(0.0F, 0.0F, 0.0F);
		this.body.addChild(this.mainBody);
		this.mainBody.addCuboid(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 10.0F, 0.0F);
		this.stinger = new ModelPart(this, 26, 7);
		this.stinger.addCuboid(0.0F, -1.0F, 5.0F, 0.0F, 1.0F, 2.0F, 0.0F);
		this.mainBody.addChild(this.stinger);
		this.leftAntenna = new ModelPart(this, 2, 0);
		this.leftAntenna.setPivot(0.0F, -2.0F, -5.0F);
		this.leftAntenna.addCuboid(1.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F, 0.0F);
		this.rightAntenna = new ModelPart(this, 2, 3);
		this.rightAntenna.setPivot(0.0F, -2.0F, -5.0F);
		this.rightAntenna.addCuboid(-2.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F, 0.0F);
		this.mainBody.addChild(this.leftAntenna);
		this.mainBody.addChild(this.rightAntenna);
		this.leftWing = new ModelPart(this, 0, 18);
		this.leftWing.setPivot(-1.5F, -4.0F, -3.0F);
		this.leftWing.pitch = 0.0F;
		this.leftWing.yaw = -0.2618F;
		this.leftWing.roll = 0.0F;
		this.body.addChild(this.leftWing);
		this.leftWing.addCuboid(-9.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, 0.001F);
		this.rightWing = new ModelPart(this, 0, 18);
		this.rightWing.setPivot(1.5F, -4.0F, -3.0F);
		this.rightWing.pitch = 0.0F;
		this.rightWing.yaw = 0.2618F;
		this.rightWing.roll = 0.0F;
		this.rightWing.mirror = true;
		this.body.addChild(this.rightWing);
		this.rightWing.addCuboid(0.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, 0.001F);
		this.frontLeg = new ModelPart(this);
		this.frontLeg.setPivot(1.5F, 3.0F, -2.0F);
		this.body.addChild(this.frontLeg);
		this.frontLeg.addCuboid("frontLegBox", -5.0F, 0.0F, 0.0F, 7, 2, 0, 0.0F, 26, 1);
		this.midLeg = new ModelPart(this);
		this.midLeg.setPivot(1.5F, 3.0F, 0.0F);
		this.body.addChild(this.midLeg);
		this.midLeg.addCuboid("midLegBox", -5.0F, 0.0F, 0.0F, 7, 2, 0, 0.0F, 26, 3);
		this.backLeg = new ModelPart(this);
		this.backLeg.setPivot(1.5F, 3.0F, 2.0F);
		this.body.addChild(this.backLeg);
		this.backLeg.addCuboid("backLegBox", -5.0F, 0.0F, 0.0F, 7, 2, 0, 0.0F, 26, 5);
	}

	public void method_22111(T beeEntity, float f, float g, float h) {
		super.animateModel(beeEntity, f, g, h);
		this.bodyPitch = beeEntity.isBaby() ? 0.0F : beeEntity.getBodyPitch(h);
		this.stinger.visible = !beeEntity.hasStung();
	}

	public void method_22112(T beeEntity, float f, float g, float h, float i, float j, float k) {
		this.leftWing.pitch = 0.0F;
		this.leftAntenna.pitch = 0.0F;
		this.rightAntenna.pitch = 0.0F;
		this.body.pitch = 0.0F;
		this.body.pivotY = 19.0F;
		boolean bl = beeEntity.onGround && beeEntity.getVelocity().lengthSquared() < 1.0E-7;
		if (bl) {
			this.leftWing.yaw = -0.2618F;
			this.leftWing.roll = 0.0F;
			this.rightWing.pitch = 0.0F;
			this.rightWing.yaw = 0.2618F;
			this.rightWing.roll = 0.0F;
			this.frontLeg.pitch = 0.0F;
			this.midLeg.pitch = 0.0F;
			this.backLeg.pitch = 0.0F;
		} else {
			float l = h * 2.1F;
			this.leftWing.yaw = 0.0F;
			this.leftWing.roll = MathHelper.cos(l) * (float) Math.PI * 0.15F;
			this.rightWing.pitch = this.leftWing.pitch;
			this.rightWing.yaw = this.leftWing.yaw;
			this.rightWing.roll = -this.leftWing.roll;
			this.frontLeg.pitch = (float) (Math.PI / 4);
			this.midLeg.pitch = (float) (Math.PI / 4);
			this.backLeg.pitch = (float) (Math.PI / 4);
			this.body.pitch = 0.0F;
			this.body.yaw = 0.0F;
			this.body.roll = 0.0F;
		}

		if (!beeEntity.isAngry()) {
			this.body.pitch = 0.0F;
			this.body.yaw = 0.0F;
			this.body.roll = 0.0F;
			if (!bl) {
				float l = MathHelper.cos(h * 0.18F);
				this.body.pitch = 0.1F + l * (float) Math.PI * 0.025F;
				this.leftAntenna.pitch = l * (float) Math.PI * 0.03F;
				this.rightAntenna.pitch = l * (float) Math.PI * 0.03F;
				this.frontLeg.pitch = -l * (float) Math.PI * 0.1F + (float) (Math.PI / 8);
				this.backLeg.pitch = -l * (float) Math.PI * 0.05F + (float) (Math.PI / 4);
				this.body.pivotY = 19.0F - MathHelper.cos(h * 0.18F) * 0.9F;
			}
		}

		if (this.bodyPitch > 0.0F) {
			this.body.pitch = ModelUtil.interpolateAngle(this.body.pitch, 3.0915928F, this.bodyPitch);
		}
	}

	@Override
	protected Iterable<ModelPart> method_22946() {
		return ImmutableList.<ModelPart>of();
	}

	@Override
	protected Iterable<ModelPart> method_22948() {
		return ImmutableList.<ModelPart>of(this.body);
	}
}

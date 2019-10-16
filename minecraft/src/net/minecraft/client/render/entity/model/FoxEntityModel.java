package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class FoxEntityModel<T extends FoxEntity> extends AnimalModel<T> {
	public final ModelPart head;
	private final ModelPart leftEar;
	private final ModelPart rightEar;
	private final ModelPart nose;
	private final ModelPart body;
	private final ModelPart frontLeftLeg;
	private final ModelPart frontRightLeg;
	private final ModelPart rearLeftLeg;
	private final ModelPart rearRightLeg;
	private final ModelPart tail;
	private float field_18025;

	public FoxEntityModel() {
		super(true, 8.0F, 3.35F);
		this.textureWidth = 48;
		this.textureHeight = 32;
		this.head = new ModelPart(this, 1, 5);
		this.head.addCuboid(-3.0F, -2.0F, -5.0F, 8.0F, 6.0F, 6.0F);
		this.head.setPivot(-1.0F, 16.5F, -3.0F);
		this.leftEar = new ModelPart(this, 8, 1);
		this.leftEar.addCuboid(-3.0F, -4.0F, -4.0F, 2.0F, 2.0F, 1.0F);
		this.rightEar = new ModelPart(this, 15, 1);
		this.rightEar.addCuboid(3.0F, -4.0F, -4.0F, 2.0F, 2.0F, 1.0F);
		this.nose = new ModelPart(this, 6, 18);
		this.nose.addCuboid(-1.0F, 2.01F, -8.0F, 4.0F, 2.0F, 3.0F);
		this.head.addChild(this.leftEar);
		this.head.addChild(this.rightEar);
		this.head.addChild(this.nose);
		this.body = new ModelPart(this, 24, 15);
		this.body.addCuboid(-3.0F, 3.999F, -3.5F, 6.0F, 11.0F, 6.0F);
		this.body.setPivot(0.0F, 16.0F, -6.0F);
		float f = 0.001F;
		this.frontLeftLeg = new ModelPart(this, 13, 24);
		this.frontLeftLeg.addCuboid(2.0F, 0.5F, -1.0F, 2.0F, 6.0F, 2.0F, 0.001F);
		this.frontLeftLeg.setPivot(-5.0F, 17.5F, 7.0F);
		this.frontRightLeg = new ModelPart(this, 4, 24);
		this.frontRightLeg.addCuboid(2.0F, 0.5F, -1.0F, 2.0F, 6.0F, 2.0F, 0.001F);
		this.frontRightLeg.setPivot(-1.0F, 17.5F, 7.0F);
		this.rearLeftLeg = new ModelPart(this, 13, 24);
		this.rearLeftLeg.addCuboid(2.0F, 0.5F, -1.0F, 2.0F, 6.0F, 2.0F, 0.001F);
		this.rearLeftLeg.setPivot(-5.0F, 17.5F, 0.0F);
		this.rearRightLeg = new ModelPart(this, 4, 24);
		this.rearRightLeg.addCuboid(2.0F, 0.5F, -1.0F, 2.0F, 6.0F, 2.0F, 0.001F);
		this.rearRightLeg.setPivot(-1.0F, 17.5F, 0.0F);
		this.tail = new ModelPart(this, 30, 0);
		this.tail.addCuboid(2.0F, 0.0F, -1.0F, 4.0F, 9.0F, 5.0F);
		this.tail.setPivot(-4.0F, 15.0F, -1.0F);
		this.body.addChild(this.tail);
	}

	public void method_18330(T foxEntity, float f, float g, float h) {
		this.body.pitch = (float) (Math.PI / 2);
		this.tail.pitch = -0.05235988F;
		this.frontLeftLeg.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g;
		this.frontRightLeg.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g;
		this.rearLeftLeg.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g;
		this.rearRightLeg.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g;
		this.head.setPivot(-1.0F, 16.5F, -3.0F);
		this.head.yaw = 0.0F;
		this.head.roll = foxEntity.getHeadRoll(h);
		this.frontLeftLeg.visible = true;
		this.frontRightLeg.visible = true;
		this.rearLeftLeg.visible = true;
		this.rearRightLeg.visible = true;
		this.body.setPivot(0.0F, 16.0F, -6.0F);
		this.body.roll = 0.0F;
		this.frontLeftLeg.setPivot(-5.0F, 17.5F, 7.0F);
		this.frontRightLeg.setPivot(-1.0F, 17.5F, 7.0F);
		if (foxEntity.isInSneakingPose()) {
			this.body.pitch = 1.6755161F;
			float i = foxEntity.getBodyRotationHeightOffset(h);
			this.body.setPivot(0.0F, 16.0F + foxEntity.getBodyRotationHeightOffset(h), -6.0F);
			this.head.setPivot(-1.0F, 16.5F + i, -3.0F);
			this.head.yaw = 0.0F;
		} else if (foxEntity.isSleeping()) {
			this.body.roll = (float) (-Math.PI / 2);
			this.body.setPivot(0.0F, 21.0F, -6.0F);
			this.tail.pitch = (float) (-Math.PI * 5.0 / 6.0);
			if (this.isChild) {
				this.tail.pitch = -2.1816616F;
				this.body.setPivot(0.0F, 21.0F, -2.0F);
			}

			this.head.setPivot(1.0F, 19.49F, -3.0F);
			this.head.pitch = 0.0F;
			this.head.yaw = (float) (-Math.PI * 2.0 / 3.0);
			this.head.roll = 0.0F;
			this.frontLeftLeg.visible = false;
			this.frontRightLeg.visible = false;
			this.rearLeftLeg.visible = false;
			this.rearRightLeg.visible = false;
		} else if (foxEntity.isSitting()) {
			this.body.pitch = (float) (Math.PI / 6);
			this.body.setPivot(0.0F, 9.0F, -3.0F);
			this.tail.pitch = (float) (Math.PI / 4);
			this.tail.setPivot(-4.0F, 15.0F, -2.0F);
			this.head.setPivot(-1.0F, 10.0F, -0.25F);
			this.head.pitch = 0.0F;
			this.head.yaw = 0.0F;
			if (this.isChild) {
				this.head.setPivot(-1.0F, 13.0F, -3.75F);
			}

			this.frontLeftLeg.pitch = (float) (-Math.PI * 5.0 / 12.0);
			this.frontLeftLeg.setPivot(-5.0F, 21.5F, 6.75F);
			this.frontRightLeg.pitch = (float) (-Math.PI * 5.0 / 12.0);
			this.frontRightLeg.setPivot(-1.0F, 21.5F, 6.75F);
			this.rearLeftLeg.pitch = (float) (-Math.PI / 12);
			this.rearRightLeg.pitch = (float) (-Math.PI / 12);
		}
	}

	@Override
	protected Iterable<ModelPart> getHeadParts() {
		return ImmutableList.<ModelPart>of(this.head);
	}

	@Override
	protected Iterable<ModelPart> getBodyParts() {
		return ImmutableList.<ModelPart>of(this.body, this.frontLeftLeg, this.frontRightLeg, this.rearLeftLeg, this.rearRightLeg);
	}

	public void method_18332(T foxEntity, float f, float g, float h, float i, float j, float k) {
		if (!foxEntity.isSleeping() && !foxEntity.isWalking() && !foxEntity.isInSneakingPose()) {
			this.head.pitch = j * (float) (Math.PI / 180.0);
			this.head.yaw = i * (float) (Math.PI / 180.0);
		}

		if (foxEntity.isSleeping()) {
			this.head.pitch = 0.0F;
			this.head.yaw = (float) (-Math.PI * 2.0 / 3.0);
			this.head.roll = MathHelper.cos(h * 0.027F) / 22.0F;
		}

		if (foxEntity.isInSneakingPose()) {
			float l = MathHelper.cos(h) * 0.01F;
			this.body.yaw = l;
			this.frontLeftLeg.roll = l;
			this.frontRightLeg.roll = l;
			this.rearLeftLeg.roll = l / 2.0F;
			this.rearRightLeg.roll = l / 2.0F;
		}

		if (foxEntity.isWalking()) {
			float l = 0.1F;
			this.field_18025 += 0.67F;
			this.frontLeftLeg.pitch = MathHelper.cos(this.field_18025 * 0.4662F) * 0.1F;
			this.frontRightLeg.pitch = MathHelper.cos(this.field_18025 * 0.4662F + (float) Math.PI) * 0.1F;
			this.rearLeftLeg.pitch = MathHelper.cos(this.field_18025 * 0.4662F + (float) Math.PI) * 0.1F;
			this.rearRightLeg.pitch = MathHelper.cos(this.field_18025 * 0.4662F) * 0.1F;
		}
	}
}

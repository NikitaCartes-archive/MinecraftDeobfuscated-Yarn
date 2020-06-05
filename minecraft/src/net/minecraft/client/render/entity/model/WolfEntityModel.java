package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class WolfEntityModel<T extends WolfEntity> extends TintableAnimalModel<T> {
	private final ModelPart head;
	private final ModelPart field_20788;
	private final ModelPart torso;
	private final ModelPart rightBackLeg;
	private final ModelPart leftBackLeg;
	private final ModelPart rightFrontLeg;
	private final ModelPart leftFrontLeg;
	private final ModelPart tail;
	private final ModelPart field_20789;
	private final ModelPart neck;

	public WolfEntityModel() {
		float f = 0.0F;
		float g = 13.5F;
		this.head = new ModelPart(this, 0, 0);
		this.head.setPivot(-1.0F, 13.5F, -7.0F);
		this.field_20788 = new ModelPart(this, 0, 0);
		this.field_20788.addCuboid(-2.0F, -3.0F, -2.0F, 6.0F, 6.0F, 4.0F, 0.0F);
		this.head.addChild(this.field_20788);
		this.torso = new ModelPart(this, 18, 14);
		this.torso.addCuboid(-3.0F, -2.0F, -3.0F, 6.0F, 9.0F, 6.0F, 0.0F);
		this.torso.setPivot(0.0F, 14.0F, 2.0F);
		this.neck = new ModelPart(this, 21, 0);
		this.neck.addCuboid(-3.0F, -3.0F, -3.0F, 8.0F, 6.0F, 7.0F, 0.0F);
		this.neck.setPivot(-1.0F, 14.0F, 2.0F);
		this.rightBackLeg = new ModelPart(this, 0, 18);
		this.rightBackLeg.addCuboid(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, 0.0F);
		this.rightBackLeg.setPivot(-2.5F, 16.0F, 7.0F);
		this.leftBackLeg = new ModelPart(this, 0, 18);
		this.leftBackLeg.addCuboid(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, 0.0F);
		this.leftBackLeg.setPivot(0.5F, 16.0F, 7.0F);
		this.rightFrontLeg = new ModelPart(this, 0, 18);
		this.rightFrontLeg.addCuboid(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, 0.0F);
		this.rightFrontLeg.setPivot(-2.5F, 16.0F, -4.0F);
		this.leftFrontLeg = new ModelPart(this, 0, 18);
		this.leftFrontLeg.addCuboid(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, 0.0F);
		this.leftFrontLeg.setPivot(0.5F, 16.0F, -4.0F);
		this.tail = new ModelPart(this, 9, 18);
		this.tail.setPivot(-1.0F, 12.0F, 8.0F);
		this.field_20789 = new ModelPart(this, 9, 18);
		this.field_20789.addCuboid(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, 0.0F);
		this.tail.addChild(this.field_20789);
		this.field_20788.setTextureOffset(16, 14).addCuboid(-2.0F, -5.0F, 0.0F, 2.0F, 2.0F, 1.0F, 0.0F);
		this.field_20788.setTextureOffset(16, 14).addCuboid(2.0F, -5.0F, 0.0F, 2.0F, 2.0F, 1.0F, 0.0F);
		this.field_20788.setTextureOffset(0, 10).addCuboid(-0.5F, 0.0F, -5.0F, 3.0F, 3.0F, 4.0F, 0.0F);
	}

	@Override
	protected Iterable<ModelPart> getHeadParts() {
		return ImmutableList.<ModelPart>of(this.head);
	}

	@Override
	protected Iterable<ModelPart> getBodyParts() {
		return ImmutableList.<ModelPart>of(this.torso, this.rightBackLeg, this.leftBackLeg, this.rightFrontLeg, this.leftFrontLeg, this.tail, this.neck);
	}

	public void animateModel(T wolfEntity, float f, float g, float h) {
		if (wolfEntity.hasAngerTime()) {
			this.tail.yaw = 0.0F;
		} else {
			this.tail.yaw = MathHelper.cos(f * 0.6662F) * 1.4F * g;
		}

		if (wolfEntity.isInSittingPose()) {
			this.neck.setPivot(-1.0F, 16.0F, -3.0F);
			this.neck.pitch = (float) (Math.PI * 2.0 / 5.0);
			this.neck.yaw = 0.0F;
			this.torso.setPivot(0.0F, 18.0F, 0.0F);
			this.torso.pitch = (float) (Math.PI / 4);
			this.tail.setPivot(-1.0F, 21.0F, 6.0F);
			this.rightBackLeg.setPivot(-2.5F, 22.7F, 2.0F);
			this.rightBackLeg.pitch = (float) (Math.PI * 3.0 / 2.0);
			this.leftBackLeg.setPivot(0.5F, 22.7F, 2.0F);
			this.leftBackLeg.pitch = (float) (Math.PI * 3.0 / 2.0);
			this.rightFrontLeg.pitch = 5.811947F;
			this.rightFrontLeg.setPivot(-2.49F, 17.0F, -4.0F);
			this.leftFrontLeg.pitch = 5.811947F;
			this.leftFrontLeg.setPivot(0.51F, 17.0F, -4.0F);
		} else {
			this.torso.setPivot(0.0F, 14.0F, 2.0F);
			this.torso.pitch = (float) (Math.PI / 2);
			this.neck.setPivot(-1.0F, 14.0F, -3.0F);
			this.neck.pitch = this.torso.pitch;
			this.tail.setPivot(-1.0F, 12.0F, 8.0F);
			this.rightBackLeg.setPivot(-2.5F, 16.0F, 7.0F);
			this.leftBackLeg.setPivot(0.5F, 16.0F, 7.0F);
			this.rightFrontLeg.setPivot(-2.5F, 16.0F, -4.0F);
			this.leftFrontLeg.setPivot(0.5F, 16.0F, -4.0F);
			this.rightBackLeg.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g;
			this.leftBackLeg.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g;
			this.rightFrontLeg.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g;
			this.leftFrontLeg.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g;
		}

		this.field_20788.roll = wolfEntity.getBegAnimationProgress(h) + wolfEntity.getShakeAnimationProgress(h, 0.0F);
		this.neck.roll = wolfEntity.getShakeAnimationProgress(h, -0.08F);
		this.torso.roll = wolfEntity.getShakeAnimationProgress(h, -0.16F);
		this.field_20789.roll = wolfEntity.getShakeAnimationProgress(h, -0.2F);
	}

	public void setAngles(T wolfEntity, float f, float g, float h, float i, float j) {
		this.head.pitch = j * (float) (Math.PI / 180.0);
		this.head.yaw = i * (float) (Math.PI / 180.0);
		this.tail.pitch = h;
	}
}

package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.passive.CatEntity;

@Environment(EnvType.CLIENT)
public class CatEntityModel<T extends CatEntity> extends OcelotEntityModel<T> {
	private float sleepAnimation;
	private float tailCurlAnimation;
	private float headDownAnimation;

	public CatEntityModel(float f) {
		super(f);
	}

	public void animateModel(T catEntity, float f, float g, float h) {
		this.sleepAnimation = catEntity.getSleepAnimation(h);
		this.tailCurlAnimation = catEntity.getTailCurlAnimation(h);
		this.headDownAnimation = catEntity.getHeadDownAnimation(h);
		if (this.sleepAnimation <= 0.0F) {
			this.head.pitch = 0.0F;
			this.head.roll = 0.0F;
			this.leftFrontLeg.pitch = 0.0F;
			this.leftFrontLeg.roll = 0.0F;
			this.rightFrontLeg.pitch = 0.0F;
			this.rightFrontLeg.roll = 0.0F;
			this.rightFrontLeg.pivotX = -1.2F;
			this.leftBackLeg.pitch = 0.0F;
			this.rightBackLeg.pitch = 0.0F;
			this.rightBackLeg.roll = 0.0F;
			this.rightBackLeg.pivotX = -1.1F;
			this.rightBackLeg.pivotY = 18.0F;
		}

		super.animateModel(catEntity, f, g, h);
		if (catEntity.isSitting()) {
			this.torso.pitch = (float) (Math.PI / 4);
			this.torso.pivotY += -4.0F;
			this.torso.pivotZ += 5.0F;
			this.head.pivotY += -3.3F;
			this.head.pivotZ++;
			this.upperTail.pivotY += 8.0F;
			this.upperTail.pivotZ += -2.0F;
			this.lowerTail.pivotY += 2.0F;
			this.lowerTail.pivotZ += -0.8F;
			this.upperTail.pitch = 1.7278761F;
			this.lowerTail.pitch = 2.670354F;
			this.leftFrontLeg.pitch = (float) (-Math.PI / 20);
			this.leftFrontLeg.pivotY = 16.1F;
			this.leftFrontLeg.pivotZ = -7.0F;
			this.rightFrontLeg.pitch = (float) (-Math.PI / 20);
			this.rightFrontLeg.pivotY = 16.1F;
			this.rightFrontLeg.pivotZ = -7.0F;
			this.leftBackLeg.pitch = (float) (-Math.PI / 2);
			this.leftBackLeg.pivotY = 21.0F;
			this.leftBackLeg.pivotZ = 1.0F;
			this.rightBackLeg.pitch = (float) (-Math.PI / 2);
			this.rightBackLeg.pivotY = 21.0F;
			this.rightBackLeg.pivotZ = 1.0F;
			this.animationState = 3;
		}
	}

	public void setAngles(T catEntity, float f, float g, float h, float i, float j, float k) {
		super.setAngles(catEntity, f, g, h, i, j, k);
		if (this.sleepAnimation > 0.0F) {
			this.head.roll = this.lerpAngle(this.head.roll, -1.2707963F, this.sleepAnimation);
			this.head.yaw = this.lerpAngle(this.head.yaw, 1.2707963F, this.sleepAnimation);
			this.leftFrontLeg.pitch = -1.2707963F;
			this.rightFrontLeg.pitch = -0.47079635F;
			this.rightFrontLeg.roll = -0.2F;
			this.rightFrontLeg.pivotX = -0.2F;
			this.leftBackLeg.pitch = -0.4F;
			this.rightBackLeg.pitch = 0.5F;
			this.rightBackLeg.roll = -0.5F;
			this.rightBackLeg.pivotX = -0.3F;
			this.rightBackLeg.pivotY = 20.0F;
			this.upperTail.pitch = this.lerpAngle(this.upperTail.pitch, 0.8F, this.tailCurlAnimation);
			this.lowerTail.pitch = this.lerpAngle(this.lowerTail.pitch, -0.4F, this.tailCurlAnimation);
		}

		if (this.headDownAnimation > 0.0F) {
			this.head.pitch = this.lerpAngle(this.head.pitch, -0.58177644F, this.headDownAnimation);
		}
	}

	protected float lerpAngle(float from, float to, float intermediate) {
		float f = to - from;

		while (f < (float) -Math.PI) {
			f += (float) (Math.PI * 2);
		}

		while (f >= (float) Math.PI) {
			f -= (float) (Math.PI * 2);
		}

		return from + intermediate * f;
	}
}

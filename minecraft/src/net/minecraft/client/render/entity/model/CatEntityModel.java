package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelUtil;
import net.minecraft.entity.passive.CatEntity;

@Environment(EnvType.CLIENT)
public class CatEntityModel<T extends CatEntity> extends OcelotEntityModel<T> {
	private float sleepAnimation;
	private float tailCurlAnimation;
	private float headDownAnimation;

	public CatEntityModel(float f) {
		super(f);
	}

	public void method_17074(T catEntity, float f, float g, float h) {
		this.sleepAnimation = catEntity.getSleepAnimation(h);
		this.tailCurlAnimation = catEntity.getTailCurlAnimation(h);
		this.headDownAnimation = catEntity.getHeadDownAnimation(h);
		if (this.sleepAnimation <= 0.0F) {
			this.head.pitch = 0.0F;
			this.head.roll = 0.0F;
			this.backLegLeft.pitch = 0.0F;
			this.backLegLeft.roll = 0.0F;
			this.backLegRight.pitch = 0.0F;
			this.backLegRight.roll = 0.0F;
			this.backLegRight.pivotX = -1.2F;
			this.frontLegLeft.pitch = 0.0F;
			this.frontLegRight.pitch = 0.0F;
			this.frontLegRight.roll = 0.0F;
			this.frontLegRight.pivotX = -1.1F;
			this.frontLegRight.pivotY = 18.0F;
		}

		super.animateModel(catEntity, f, g, h);
		if (catEntity.isSitting()) {
			this.body.pitch = (float) (Math.PI / 4);
			this.body.pivotY += -4.0F;
			this.body.pivotZ += 5.0F;
			this.head.pivotY += -3.3F;
			this.head.pivotZ++;
			this.tail1.pivotY += 8.0F;
			this.tail1.pivotZ += -2.0F;
			this.tail2.pivotY += 2.0F;
			this.tail2.pivotZ += -0.8F;
			this.tail1.pitch = 1.7278761F;
			this.tail2.pitch = 2.670354F;
			this.backLegLeft.pitch = (float) (-Math.PI / 20);
			this.backLegLeft.pivotY = 16.1F;
			this.backLegLeft.pivotZ = -7.0F;
			this.backLegRight.pitch = (float) (-Math.PI / 20);
			this.backLegRight.pivotY = 16.1F;
			this.backLegRight.pivotZ = -7.0F;
			this.frontLegLeft.pitch = (float) (-Math.PI / 2);
			this.frontLegLeft.pivotY = 21.0F;
			this.frontLegLeft.pivotZ = 1.0F;
			this.frontLegRight.pitch = (float) (-Math.PI / 2);
			this.frontLegRight.pivotY = 21.0F;
			this.frontLegRight.pivotZ = 1.0F;
			this.animationState = 3;
		}
	}

	public void method_17075(T catEntity, float f, float g, float h, float i, float j) {
		super.setAngles(catEntity, f, g, h, i, j);
		if (this.sleepAnimation > 0.0F) {
			this.head.roll = ModelUtil.interpolateAngle(this.head.roll, -1.2707963F, this.sleepAnimation);
			this.head.yaw = ModelUtil.interpolateAngle(this.head.yaw, 1.2707963F, this.sleepAnimation);
			this.backLegLeft.pitch = -1.2707963F;
			this.backLegRight.pitch = -0.47079635F;
			this.backLegRight.roll = -0.2F;
			this.backLegRight.pivotX = -0.2F;
			this.frontLegLeft.pitch = -0.4F;
			this.frontLegRight.pitch = 0.5F;
			this.frontLegRight.roll = -0.5F;
			this.frontLegRight.pivotX = -0.3F;
			this.frontLegRight.pivotY = 20.0F;
			this.tail1.pitch = ModelUtil.interpolateAngle(this.tail1.pitch, 0.8F, this.tailCurlAnimation);
			this.tail2.pitch = ModelUtil.interpolateAngle(this.tail2.pitch, -0.4F, this.tailCurlAnimation);
		}

		if (this.headDownAnimation > 0.0F) {
			this.head.pitch = ModelUtil.interpolateAngle(this.head.pitch, -0.58177644F, this.headDownAnimation);
		}
	}
}

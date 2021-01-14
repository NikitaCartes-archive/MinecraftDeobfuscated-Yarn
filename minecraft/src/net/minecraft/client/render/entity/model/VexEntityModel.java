package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class VexEntityModel extends BipedEntityModel<VexEntity> {
	private final ModelPart leftWing;
	private final ModelPart rightWing;

	public VexEntityModel() {
		super(0.0F, 0.0F, 64, 64);
		this.leftLeg.visible = false;
		this.hat.visible = false;
		this.rightLeg = new ModelPart(this, 32, 0);
		this.rightLeg.addCuboid(-1.0F, -1.0F, -2.0F, 6.0F, 10.0F, 4.0F, 0.0F);
		this.rightLeg.setPivot(-1.9F, 12.0F, 0.0F);
		this.rightWing = new ModelPart(this, 0, 32);
		this.rightWing.addCuboid(-20.0F, 0.0F, 0.0F, 20.0F, 12.0F, 1.0F);
		this.leftWing = new ModelPart(this, 0, 32);
		this.leftWing.mirror = true;
		this.leftWing.addCuboid(0.0F, 0.0F, 0.0F, 20.0F, 12.0F, 1.0F);
	}

	@Override
	protected Iterable<ModelPart> getBodyParts() {
		return Iterables.concat(super.getBodyParts(), ImmutableList.of(this.rightWing, this.leftWing));
	}

	public void setAngles(VexEntity vexEntity, float f, float g, float h, float i, float j) {
		super.setAngles(vexEntity, f, g, h, i, j);
		if (vexEntity.isCharging()) {
			if (vexEntity.getMainHandStack().isEmpty()) {
				this.rightArm.pitch = (float) (Math.PI * 3.0 / 2.0);
				this.leftArm.pitch = (float) (Math.PI * 3.0 / 2.0);
			} else if (vexEntity.getMainArm() == Arm.RIGHT) {
				this.rightArm.pitch = 3.7699115F;
			} else {
				this.leftArm.pitch = 3.7699115F;
			}
		}

		this.rightLeg.pitch += (float) (Math.PI / 5);
		this.rightWing.pivotZ = 2.0F;
		this.leftWing.pivotZ = 2.0F;
		this.rightWing.pivotY = 1.0F;
		this.leftWing.pivotY = 1.0F;
		this.rightWing.yaw = 0.47123894F + MathHelper.cos(h * 0.8F) * (float) Math.PI * 0.05F;
		this.leftWing.yaw = -this.rightWing.yaw;
		this.leftWing.roll = -0.47123894F;
		this.leftWing.pitch = 0.47123894F;
		this.rightWing.pitch = 0.47123894F;
		this.rightWing.roll = 0.47123894F;
	}
}

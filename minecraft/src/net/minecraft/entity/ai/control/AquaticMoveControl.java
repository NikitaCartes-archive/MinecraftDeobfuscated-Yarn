package net.minecraft.entity.ai.control;

import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;

public class AquaticMoveControl extends MoveControl {
	private final int pitchChange;
	private final int yawChange;
	private final float speedInWater;
	private final float speedInAir;
	private final boolean buoyant;

	public AquaticMoveControl(MobEntity entity, int pitchChange, int yawChange, float speedInWater, float speedInAir, boolean buoyant) {
		super(entity);
		this.pitchChange = pitchChange;
		this.yawChange = yawChange;
		this.speedInWater = speedInWater;
		this.speedInAir = speedInAir;
		this.buoyant = buoyant;
	}

	@Override
	public void tick() {
		if (this.buoyant && this.entity.isTouchingWater()) {
			this.entity.setVelocity(this.entity.getVelocity().add(0.0, 0.005, 0.0));
		}

		if (this.state == MoveControl.State.MOVE_TO && !this.entity.getNavigation().isIdle()) {
			double d = this.targetX - this.entity.getX();
			double e = this.targetY - this.entity.getY();
			double f = this.targetZ - this.entity.getZ();
			double g = d * d + e * e + f * f;
			if (g < 2.5000003E-7F) {
				this.entity.setForwardSpeed(0.0F);
			} else {
				float h = (float)(MathHelper.atan2(f, d) * 180.0F / (float)Math.PI) - 90.0F;
				this.entity.yaw = this.wrapDegrees(this.entity.yaw, h, (float)this.yawChange);
				this.entity.bodyYaw = this.entity.yaw;
				this.entity.headYaw = this.entity.yaw;
				float i = (float)(this.speed * this.entity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED));
				if (this.entity.isTouchingWater()) {
					this.entity.setMovementSpeed(i * this.speedInWater);
					float j = -((float)(MathHelper.atan2(e, (double)MathHelper.sqrt(d * d + f * f)) * 180.0F / (float)Math.PI));
					j = MathHelper.clamp(MathHelper.wrapDegrees(j), (float)(-this.pitchChange), (float)this.pitchChange);
					this.entity.pitch = this.wrapDegrees(this.entity.pitch, j, 5.0F);
					float k = MathHelper.cos(this.entity.pitch * (float) (Math.PI / 180.0));
					float l = MathHelper.sin(this.entity.pitch * (float) (Math.PI / 180.0));
					this.entity.forwardSpeed = k * i;
					this.entity.upwardSpeed = -l * i;
				} else {
					this.entity.setMovementSpeed(i * this.speedInAir);
				}
			}
		} else {
			this.entity.setMovementSpeed(0.0F);
			this.entity.setSidewaysSpeed(0.0F);
			this.entity.setUpwardSpeed(0.0F);
			this.entity.setForwardSpeed(0.0F);
		}
	}
}

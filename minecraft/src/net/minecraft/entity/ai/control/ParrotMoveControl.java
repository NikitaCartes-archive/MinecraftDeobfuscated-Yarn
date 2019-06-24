package net.minecraft.entity.ai.control;

import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;

public class ParrotMoveControl extends MoveControl {
	public ParrotMoveControl(MobEntity mobEntity) {
		super(mobEntity);
	}

	@Override
	public void tick() {
		if (this.state == MoveControl.State.MOVE_TO) {
			this.state = MoveControl.State.WAIT;
			this.entity.setNoGravity(true);
			double d = this.targetX - this.entity.x;
			double e = this.targetY - this.entity.y;
			double f = this.targetZ - this.entity.z;
			double g = d * d + e * e + f * f;
			if (g < 2.5000003E-7F) {
				this.entity.setUpwardSpeed(0.0F);
				this.entity.setForwardSpeed(0.0F);
				return;
			}

			float h = (float)(MathHelper.atan2(f, d) * 180.0F / (float)Math.PI) - 90.0F;
			this.entity.yaw = this.changeAngle(this.entity.yaw, h, 10.0F);
			float i;
			if (this.entity.onGround) {
				i = (float)(this.speed * this.entity.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue());
			} else {
				i = (float)(this.speed * this.entity.getAttributeInstance(EntityAttributes.FLYING_SPEED).getValue());
			}

			this.entity.setMovementSpeed(i);
			double j = (double)MathHelper.sqrt(d * d + f * f);
			float k = (float)(-(MathHelper.atan2(e, j) * 180.0F / (float)Math.PI));
			this.entity.pitch = this.changeAngle(this.entity.pitch, k, 10.0F);
			this.entity.setUpwardSpeed(e > 0.0 ? i : -i);
		} else {
			this.entity.setNoGravity(false);
			this.entity.setUpwardSpeed(0.0F);
			this.entity.setForwardSpeed(0.0F);
		}
	}
}

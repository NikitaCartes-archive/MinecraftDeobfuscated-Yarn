package net.minecraft.entity.ai.control;

import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;

public class FlightMoveControl extends MoveControl {
	private final int maxPitchChange;
	private final boolean noGravity;

	public FlightMoveControl(MobEntity entity, int maxPitchChange, boolean noGravity) {
		super(entity);
		this.maxPitchChange = maxPitchChange;
		this.noGravity = noGravity;
	}

	@Override
	public void tick() {
		if (this.state == MoveControl.State.MOVE_TO) {
			this.state = MoveControl.State.WAIT;
			this.entity.setNoGravity(true);
			double d = this.targetX - this.entity.getX();
			double e = this.targetY - this.entity.getY();
			double f = this.targetZ - this.entity.getZ();
			double g = d * d + e * e + f * f;
			if (g < 2.5000003E-7F) {
				this.entity.setUpwardSpeed(0.0F);
				this.entity.setForwardSpeed(0.0F);
				return;
			}

			float h = (float)(MathHelper.atan2(f, d) * 180.0F / (float)Math.PI) - 90.0F;
			this.entity.setYaw(this.wrapDegrees(this.entity.getYaw(), h, 90.0F));
			float i;
			if (this.entity.isOnGround()) {
				i = (float)(this.speed * this.entity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED));
			} else {
				i = (float)(this.speed * this.entity.getAttributeValue(EntityAttributes.GENERIC_FLYING_SPEED));
			}

			this.entity.setMovementSpeed(i);
			double j = Math.sqrt(d * d + f * f);
			if (Math.abs(e) > 1.0E-5F || Math.abs(j) > 1.0E-5F) {
				float k = (float)(-(MathHelper.atan2(e, j) * 180.0F / (float)Math.PI));
				this.entity.setPitch(this.wrapDegrees(this.entity.getPitch(), k, (float)this.maxPitchChange));
				this.entity.setUpwardSpeed(e > 0.0 ? i : -i);
			}
		} else {
			if (!this.noGravity) {
				this.entity.setNoGravity(false);
			}

			this.entity.setUpwardSpeed(0.0F);
			this.entity.setForwardSpeed(0.0F);
		}
	}
}

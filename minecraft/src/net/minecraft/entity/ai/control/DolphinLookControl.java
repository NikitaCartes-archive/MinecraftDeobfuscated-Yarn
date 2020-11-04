package net.minecraft.entity.ai.control;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;

public class DolphinLookControl extends LookControl {
	private final int maxYawDifference;

	public DolphinLookControl(MobEntity entity, int maxYawDifference) {
		super(entity);
		this.maxYawDifference = maxYawDifference;
	}

	@Override
	public void tick() {
		if (this.active) {
			this.active = false;
			this.entity.headYaw = this.changeAngle(this.entity.headYaw, this.getTargetYaw() + 20.0F, this.yawSpeed);
			this.entity.pitch = this.changeAngle(this.entity.pitch, this.getTargetPitch() + 10.0F, this.pitchSpeed);
		} else {
			if (this.entity.getNavigation().isIdle()) {
				this.entity.pitch = this.changeAngle(this.entity.pitch, 0.0F, 5.0F);
			}

			this.entity.headYaw = this.changeAngle(this.entity.headYaw, this.entity.bodyYaw, this.yawSpeed);
		}

		float f = MathHelper.wrapDegrees(this.entity.headYaw - this.entity.bodyYaw);
		if (f < (float)(-this.maxYawDifference)) {
			this.entity.bodyYaw -= 4.0F;
		} else if (f > (float)this.maxYawDifference) {
			this.entity.bodyYaw += 4.0F;
		}
	}
}

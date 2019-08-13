package net.minecraft.entity.ai.control;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;

public class DolphinLookControl extends LookControl {
	private final int field_6357;

	public DolphinLookControl(MobEntity mobEntity, int i) {
		super(mobEntity);
		this.field_6357 = i;
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

			this.entity.headYaw = this.changeAngle(this.entity.headYaw, this.entity.field_6283, this.yawSpeed);
		}

		float f = MathHelper.wrapDegrees(this.entity.headYaw - this.entity.field_6283);
		if (f < (float)(-this.field_6357)) {
			this.entity.field_6283 -= 4.0F;
		} else if (f > (float)this.field_6357) {
			this.entity.field_6283 += 4.0F;
		}
	}
}

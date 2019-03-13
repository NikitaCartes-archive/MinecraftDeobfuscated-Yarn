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
			double d = this.lookX - this.entity.x;
			double e = this.lookY - (this.entity.y + (double)this.entity.getStandingEyeHeight());
			double f = this.lookZ - this.entity.z;
			double g = (double)MathHelper.sqrt(d * d + f * f);
			float h = (float)(MathHelper.atan2(f, d) * 180.0F / (float)Math.PI) - 90.0F + 20.0F;
			float i = (float)(-(MathHelper.atan2(e, g) * 180.0F / (float)Math.PI)) + 10.0F;
			this.entity.pitch = this.method_6229(this.entity.pitch, i, this.field_6358);
			this.entity.headYaw = this.method_6229(this.entity.headYaw, h, this.field_6359);
		} else {
			if (this.entity.method_5942().isIdle()) {
				this.entity.pitch = this.method_6229(this.entity.pitch, 0.0F, 5.0F);
			}

			this.entity.headYaw = this.method_6229(this.entity.headYaw, this.entity.field_6283, this.field_6359);
		}

		float j = MathHelper.wrapDegrees(this.entity.headYaw - this.entity.field_6283);
		if (j < (float)(-this.field_6357)) {
			this.entity.field_6283 -= 4.0F;
		} else if (j > (float)this.field_6357) {
			this.entity.field_6283 += 4.0F;
		}
	}
}

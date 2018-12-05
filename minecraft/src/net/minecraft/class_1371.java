package net.minecraft;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.BlockView;

public class class_1371 extends Goal {
	private final BlockView field_6538;
	private final MobEntity field_6541;
	private LivingEntity field_6539;
	private int field_6540;

	public class_1371(MobEntity mobEntity) {
		this.field_6541 = mobEntity;
		this.field_6538 = mobEntity.world;
		this.setControlBits(3);
	}

	@Override
	public boolean canStart() {
		LivingEntity livingEntity = this.field_6541.getTarget();
		if (livingEntity == null) {
			return false;
		} else {
			this.field_6539 = livingEntity;
			return true;
		}
	}

	@Override
	public boolean shouldContinue() {
		if (!this.field_6539.isValid()) {
			return false;
		} else {
			return this.field_6541.squaredDistanceTo(this.field_6539) > 225.0 ? false : !this.field_6541.getNavigation().method_6357() || this.canStart();
		}
	}

	@Override
	public void onRemove() {
		this.field_6539 = null;
		this.field_6541.getNavigation().method_6340();
	}

	@Override
	public void tick() {
		this.field_6541.getLookControl().lookAt(this.field_6539, 30.0F, 30.0F);
		double d = (double)(this.field_6541.width * 2.0F * this.field_6541.width * 2.0F);
		double e = this.field_6541.squaredDistanceTo(this.field_6539.x, this.field_6539.getBoundingBox().minY, this.field_6539.z);
		double f = 0.8;
		if (e > d && e < 16.0) {
			f = 1.33;
		} else if (e < 225.0) {
			f = 0.6;
		}

		this.field_6541.getNavigation().method_6335(this.field_6539, f);
		this.field_6540 = Math.max(this.field_6540 - 1, 0);
		if (!(e > d)) {
			if (this.field_6540 <= 0) {
				this.field_6540 = 20;
				this.field_6541.method_6121(this.field_6539);
			}
		}
	}
}

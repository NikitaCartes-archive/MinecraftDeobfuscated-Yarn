package net.minecraft;

import java.util.EnumSet;
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
		this.field_6538 = mobEntity.field_6002;
		this.setControlBits(EnumSet.of(Goal.class_4134.field_18405, Goal.class_4134.field_18406));
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
			return this.field_6541.squaredDistanceTo(this.field_6539) > 225.0 ? false : !this.field_6541.method_5942().isIdle() || this.canStart();
		}
	}

	@Override
	public void onRemove() {
		this.field_6539 = null;
		this.field_6541.method_5942().stop();
	}

	@Override
	public void tick() {
		this.field_6541.method_5988().lookAt(this.field_6539, 30.0F, 30.0F);
		double d = (double)(this.field_6541.getWidth() * 2.0F * this.field_6541.getWidth() * 2.0F);
		double e = this.field_6541.squaredDistanceTo(this.field_6539.x, this.field_6539.method_5829().minY, this.field_6539.z);
		double f = 0.8;
		if (e > d && e < 16.0) {
			f = 1.33;
		} else if (e < 225.0) {
			f = 0.6;
		}

		this.field_6541.method_5942().startMovingTo(this.field_6539, f);
		this.field_6540 = Math.max(this.field_6540 - 1, 0);
		if (!(e > d)) {
			if (this.field_6540 <= 0) {
				this.field_6540 = 20;
				this.field_6541.attack(this.field_6539);
			}
		}
	}
}

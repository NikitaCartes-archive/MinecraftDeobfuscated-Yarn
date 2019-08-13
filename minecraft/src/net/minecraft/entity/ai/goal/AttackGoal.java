package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.BlockView;

public class AttackGoal extends Goal {
	private final BlockView world;
	private final MobEntity mob;
	private LivingEntity target;
	private int cooldown;

	public AttackGoal(MobEntity mobEntity) {
		this.mob = mobEntity;
		this.world = mobEntity.world;
		this.setControls(EnumSet.of(Goal.Control.field_18405, Goal.Control.field_18406));
	}

	@Override
	public boolean canStart() {
		LivingEntity livingEntity = this.mob.getTarget();
		if (livingEntity == null) {
			return false;
		} else {
			this.target = livingEntity;
			return true;
		}
	}

	@Override
	public boolean shouldContinue() {
		if (!this.target.isAlive()) {
			return false;
		} else {
			return this.mob.squaredDistanceTo(this.target) > 225.0 ? false : !this.mob.getNavigation().isIdle() || this.canStart();
		}
	}

	@Override
	public void stop() {
		this.target = null;
		this.mob.getNavigation().stop();
	}

	@Override
	public void tick() {
		this.mob.getLookControl().lookAt(this.target, 30.0F, 30.0F);
		double d = (double)(this.mob.getWidth() * 2.0F * this.mob.getWidth() * 2.0F);
		double e = this.mob.squaredDistanceTo(this.target.x, this.target.getBoundingBox().minY, this.target.z);
		double f = 0.8;
		if (e > d && e < 16.0) {
			f = 1.33;
		} else if (e < 225.0) {
			f = 0.6;
		}

		this.mob.getNavigation().startMovingTo(this.target, f);
		this.cooldown = Math.max(this.cooldown - 1, 0);
		if (!(e > d)) {
			if (this.cooldown <= 0) {
				this.cooldown = 20;
				this.mob.tryAttack(this.target);
			}
		}
	}
}

package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.CreeperEntity;

public class CreeperIgniteGoal extends Goal {
	private final CreeperEntity owner;
	private LivingEntity target;

	public CreeperIgniteGoal(CreeperEntity creeperEntity) {
		this.owner = creeperEntity;
		this.setControls(EnumSet.of(Goal.Control.field_18405));
	}

	@Override
	public boolean canStart() {
		LivingEntity livingEntity = this.owner.getTarget();
		return this.owner.getFuseSpeed() > 0 || livingEntity != null && this.owner.squaredDistanceTo(livingEntity) < 9.0;
	}

	@Override
	public void start() {
		this.owner.getNavigation().stop();
		this.target = this.owner.getTarget();
	}

	@Override
	public void stop() {
		this.target = null;
	}

	@Override
	public void tick() {
		if (this.target == null) {
			this.owner.setFuseSpeed(-1);
		} else if (this.owner.squaredDistanceTo(this.target) > 49.0) {
			this.owner.setFuseSpeed(-1);
		} else if (!this.owner.getVisibilityCache().canSee(this.target)) {
			this.owner.setFuseSpeed(-1);
		} else {
			this.owner.setFuseSpeed(1);
		}
	}
}

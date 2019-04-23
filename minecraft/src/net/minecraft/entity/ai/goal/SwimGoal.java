package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.mob.MobEntity;

public class SwimGoal extends Goal {
	private final MobEntity mob;

	public SwimGoal(MobEntity mobEntity) {
		this.mob = mobEntity;
		this.setControls(EnumSet.of(Goal.Control.field_18407));
		mobEntity.getNavigation().setCanSwim(true);
	}

	@Override
	public boolean canStart() {
		double d = (double)this.mob.getStandingEyeHeight() < 0.4 ? 0.2 : 0.4;
		return this.mob.isInsideWater() && this.mob.getWaterHeight() > d || this.mob.isInLava();
	}

	@Override
	public void tick() {
		if (this.mob.getRand().nextFloat() < 0.8F) {
			this.mob.getJumpControl().setActive();
		}
	}
}

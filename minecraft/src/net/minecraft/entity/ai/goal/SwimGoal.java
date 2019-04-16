package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.mob.MobEntity;

public class SwimGoal extends Goal {
	private final MobEntity entityMob;

	public SwimGoal(MobEntity mobEntity) {
		this.entityMob = mobEntity;
		this.setControls(EnumSet.of(Goal.Control.field_18407));
		mobEntity.getNavigation().setCanSwim(true);
	}

	@Override
	public boolean canStart() {
		double d = (double)this.entityMob.getStandingEyeHeight() < 0.4 ? 0.2 : 0.4;
		return this.entityMob.isInsideWater() && this.entityMob.getWaterHeight() > d || this.entityMob.isTouchingLava();
	}

	@Override
	public void tick() {
		if (this.entityMob.getRand().nextFloat() < 0.8F) {
			this.entityMob.getJumpControl().setActive();
		}
	}
}

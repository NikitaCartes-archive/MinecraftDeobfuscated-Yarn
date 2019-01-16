package net.minecraft.entity.ai.goal;

import net.minecraft.entity.mob.MobEntity;

public class SwimGoal extends Goal {
	private final MobEntity entityMob;

	public SwimGoal(MobEntity mobEntity) {
		this.entityMob = mobEntity;
		this.setControlBits(4);
		mobEntity.getNavigation().method_6354(true);
	}

	@Override
	public boolean canStart() {
		return this.entityMob.isInsideWater() && this.entityMob.method_5861() > 0.4 || this.entityMob.isTouchingLava();
	}

	@Override
	public void tick() {
		if (this.entityMob.getRand().nextFloat() < 0.8F) {
			this.entityMob.getJumpControl().setActive();
		}
	}
}

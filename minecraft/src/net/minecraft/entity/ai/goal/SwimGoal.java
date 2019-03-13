package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.mob.MobEntity;

public class SwimGoal extends Goal {
	private final MobEntity entityMob;

	public SwimGoal(MobEntity mobEntity) {
		this.entityMob = mobEntity;
		this.setControlBits(EnumSet.of(Goal.class_4134.field_18407));
		mobEntity.method_5942().setCanSwim(true);
	}

	@Override
	public boolean canStart() {
		return this.entityMob.isInsideWater() && this.entityMob.method_5861() > 0.4 || this.entityMob.isTouchingLava();
	}

	@Override
	public void tick() {
		if (this.entityMob.getRand().nextFloat() < 0.8F) {
			this.entityMob.method_5993().setActive();
		}
	}
}

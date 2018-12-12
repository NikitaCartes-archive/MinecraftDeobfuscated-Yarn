package net.minecraft;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.raid.RaiderEntity;

public class class_3760<T extends LivingEntity> extends FollowTargetGoal<T> {
	public class_3760(RaiderEntity raiderEntity, Class<T> class_, boolean bl) {
		super(raiderEntity, class_, bl);
	}

	@Override
	public boolean canStart() {
		return ((RaiderEntity)this.entity).hasActiveRaid() && this.field_6643.isAssignableFrom(RaiderEntity.class);
	}
}

package net.minecraft.entity.ai.goal;

import net.minecraft.class_5493;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.mob.PathAwareEntity;

public class AvoidSunlightGoal extends Goal {
	private final PathAwareEntity mob;

	public AvoidSunlightGoal(PathAwareEntity mob) {
		this.mob = mob;
	}

	@Override
	public boolean canStart() {
		return this.mob.world.isDay() && this.mob.getEquippedStack(EquipmentSlot.field_6169).isEmpty() && class_5493.method_30955(this.mob);
	}

	@Override
	public void start() {
		((MobNavigation)this.mob.getNavigation()).setAvoidSunlight(true);
	}

	@Override
	public void stop() {
		if (class_5493.method_30955(this.mob)) {
			((MobNavigation)this.mob.getNavigation()).setAvoidSunlight(false);
		}
	}
}

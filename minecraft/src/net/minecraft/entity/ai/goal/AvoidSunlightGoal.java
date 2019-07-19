package net.minecraft.entity.ai.goal;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.mob.MobEntityWithAi;

public class AvoidSunlightGoal extends Goal {
	private final MobEntityWithAi mob;

	public AvoidSunlightGoal(MobEntityWithAi mob) {
		this.mob = mob;
	}

	@Override
	public boolean canStart() {
		return this.mob.world.isDay() && this.mob.getEquippedStack(EquipmentSlot.HEAD).isEmpty() && this.mob.getNavigation() instanceof MobNavigation;
	}

	@Override
	public void start() {
		((MobNavigation)this.mob.getNavigation()).setAvoidSunlight(true);
	}

	@Override
	public void stop() {
		((MobNavigation)this.mob.getNavigation()).setAvoidSunlight(false);
	}
}

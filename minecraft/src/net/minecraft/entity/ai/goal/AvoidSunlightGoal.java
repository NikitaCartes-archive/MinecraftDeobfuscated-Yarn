package net.minecraft.entity.ai.goal;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.mob.MobEntityWithAi;

public class AvoidSunlightGoal extends Goal {
	private final MobEntityWithAi mob;

	public AvoidSunlightGoal(MobEntityWithAi mobEntityWithAi) {
		this.mob = mobEntityWithAi;
	}

	@Override
	public boolean canStart() {
		return this.mob.world.isDaylight() && this.mob.getEquippedStack(EquipmentSlot.field_6169).isEmpty() && this.mob.getNavigation() instanceof MobNavigation;
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

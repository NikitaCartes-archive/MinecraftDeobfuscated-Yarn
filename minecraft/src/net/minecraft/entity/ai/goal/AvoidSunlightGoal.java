package net.minecraft.entity.ai.goal;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.mob.MobEntityWithAi;

public class AvoidSunlightGoal extends Goal {
	private final MobEntityWithAi owner;

	public AvoidSunlightGoal(MobEntityWithAi mobEntityWithAi) {
		this.owner = mobEntityWithAi;
	}

	@Override
	public boolean canStart() {
		return this.owner.world.isDaylight() && this.owner.getEquippedStack(EquipmentSlot.HEAD).isEmpty() && this.owner.getNavigation() instanceof MobNavigation;
	}

	@Override
	public void start() {
		((MobNavigation)this.owner.getNavigation()).setAvoidSunlight(true);
	}

	@Override
	public void onRemove() {
		((MobNavigation)this.owner.getNavigation()).setAvoidSunlight(false);
	}
}

package net.minecraft.entity.ai.goal;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.pathing.EntityMobNavigation;
import net.minecraft.entity.mob.MobEntityWithAi;

public class AvoidSunlightGoal extends Goal {
	private final MobEntityWithAi owner;

	public AvoidSunlightGoal(MobEntityWithAi mobEntityWithAi) {
		this.owner = mobEntityWithAi;
	}

	@Override
	public boolean canStart() {
		return this.owner.field_6002.isDaylight() && this.owner.method_6118(EquipmentSlot.HEAD).isEmpty() && this.owner.method_5942() instanceof EntityMobNavigation;
	}

	@Override
	public void start() {
		((EntityMobNavigation)this.owner.method_5942()).setAvoidSunlight(true);
	}

	@Override
	public void onRemove() {
		((EntityMobNavigation)this.owner.method_5942()).setAvoidSunlight(false);
	}
}

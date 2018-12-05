package net.minecraft;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.EntityMobNavigation;
import net.minecraft.entity.mob.MobEntityWithAi;

public class class_1384 extends Goal {
	private final MobEntityWithAi field_6594;

	public class_1384(MobEntityWithAi mobEntityWithAi) {
		this.field_6594 = mobEntityWithAi;
	}

	@Override
	public boolean canStart() {
		return this.field_6594.world.isDaylight() && this.field_6594.getEquippedStack(EquipmentSlot.HEAD).isEmpty();
	}

	@Override
	public void start() {
		((EntityMobNavigation)this.field_6594.getNavigation()).method_6361(true);
	}

	@Override
	public void onRemove() {
		((EntityMobNavigation)this.field_6594.getNavigation()).method_6361(false);
	}
}

package net.minecraft;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.CreeperEntity;

public class class_1389 extends Goal {
	private final CreeperEntity field_6608;
	private LivingEntity field_6609;

	public class_1389(CreeperEntity creeperEntity) {
		this.field_6608 = creeperEntity;
		this.setControlBits(1);
	}

	@Override
	public boolean canStart() {
		LivingEntity livingEntity = this.field_6608.getTarget();
		return this.field_6608.getFuseSpeed() > 0 || livingEntity != null && this.field_6608.squaredDistanceTo(livingEntity) < 9.0;
	}

	@Override
	public void start() {
		this.field_6608.getNavigation().method_6340();
		this.field_6609 = this.field_6608.getTarget();
	}

	@Override
	public void onRemove() {
		this.field_6609 = null;
	}

	@Override
	public void tick() {
		if (this.field_6609 == null) {
			this.field_6608.setFuseSpeed(-1);
		} else if (this.field_6608.squaredDistanceTo(this.field_6609) > 49.0) {
			this.field_6608.setFuseSpeed(-1);
		} else if (!this.field_6608.getVisibilityCache().canSee(this.field_6609)) {
			this.field_6608.setFuseSpeed(-1);
		} else {
			this.field_6608.setFuseSpeed(1);
		}
	}
}

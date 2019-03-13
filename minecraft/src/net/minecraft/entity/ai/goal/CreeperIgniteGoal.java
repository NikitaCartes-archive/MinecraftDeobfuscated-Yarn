package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.CreeperEntity;

public class CreeperIgniteGoal extends Goal {
	private final CreeperEntity field_6608;
	private LivingEntity target;

	public CreeperIgniteGoal(CreeperEntity creeperEntity) {
		this.field_6608 = creeperEntity;
		this.setControlBits(EnumSet.of(Goal.class_4134.field_18405));
	}

	@Override
	public boolean canStart() {
		LivingEntity livingEntity = this.field_6608.getTarget();
		return this.field_6608.getFuseSpeed() > 0 || livingEntity != null && this.field_6608.squaredDistanceTo(livingEntity) < 9.0;
	}

	@Override
	public void start() {
		this.field_6608.method_5942().stop();
		this.target = this.field_6608.getTarget();
	}

	@Override
	public void onRemove() {
		this.target = null;
	}

	@Override
	public void tick() {
		if (this.target == null) {
			this.field_6608.setFuseSpeed(-1);
		} else if (this.field_6608.squaredDistanceTo(this.target) > 49.0) {
			this.field_6608.setFuseSpeed(-1);
		} else if (!this.field_6608.method_5985().canSee(this.target)) {
			this.field_6608.setFuseSpeed(-1);
		} else {
			this.field_6608.setFuseSpeed(1);
		}
	}
}

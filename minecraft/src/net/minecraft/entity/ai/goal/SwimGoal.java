package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.tag.FluidTags;

public class SwimGoal extends Goal {
	private final MobEntity mob;

	public SwimGoal(MobEntity mob) {
		this.mob = mob;
		this.setControls(EnumSet.of(Goal.Control.field_18407));
		mob.getNavigation().setCanSwim(true);
	}

	@Override
	public boolean canStart() {
		return this.mob.isTouchingWater() && this.mob.getFluidHeight(FluidTags.field_15517) > this.mob.method_29241() || this.mob.isInLava();
	}

	@Override
	public void tick() {
		if (this.mob.getRandom().nextFloat() < 0.8F) {
			this.mob.getJumpControl().setActive();
		}
	}
}

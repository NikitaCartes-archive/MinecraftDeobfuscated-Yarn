package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.tag.FluidTags;

public class SwimGoal extends Goal {
	private final MobEntity mob;

	public SwimGoal(MobEntity mob) {
		this.mob = mob;
		this.setControls(EnumSet.of(Goal.Control.JUMP));
		mob.getNavigation().setCanSwim(true);
	}

	@Override
	public boolean canStart() {
		return this.mob.isTouchingWater() && this.mob.getFluidHeight(FluidTags.WATER) > this.mob.getSwimHeight() || this.mob.isInLava();
	}

	@Override
	public boolean shouldRunEveryTick() {
		return true;
	}

	@Override
	public void tick() {
		if (this.mob.getRandom().nextFloat() < 0.8F) {
			this.mob.getJumpControl().setActive();
		}
	}
}

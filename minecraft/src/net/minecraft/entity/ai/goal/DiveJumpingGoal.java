package net.minecraft.entity.ai.goal;

import java.util.EnumSet;

public abstract class DiveJumpingGoal extends Goal {
	public DiveJumpingGoal() {
		this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.JUMP));
	}
}

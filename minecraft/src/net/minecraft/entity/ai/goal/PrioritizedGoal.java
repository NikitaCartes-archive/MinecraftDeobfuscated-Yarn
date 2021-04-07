package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import javax.annotation.Nullable;

public class PrioritizedGoal extends Goal {
	private final Goal goal;
	private final int priority;
	private boolean running;

	public PrioritizedGoal(int priority, Goal goal) {
		this.priority = priority;
		this.goal = goal;
	}

	public boolean canBeReplacedBy(PrioritizedGoal goal) {
		return this.canStop() && goal.getPriority() < this.getPriority();
	}

	@Override
	public boolean canStart() {
		return this.goal.canStart();
	}

	@Override
	public boolean shouldContinue() {
		return this.goal.shouldContinue();
	}

	@Override
	public boolean canStop() {
		return this.goal.canStop();
	}

	@Override
	public void start() {
		if (!this.running) {
			this.running = true;
			this.goal.start();
		}
	}

	@Override
	public void stop() {
		if (this.running) {
			this.running = false;
			this.goal.stop();
		}
	}

	@Override
	public void tick() {
		this.goal.tick();
	}

	@Override
	public void setControls(EnumSet<Goal.Control> controls) {
		this.goal.setControls(controls);
	}

	@Override
	public EnumSet<Goal.Control> getControls() {
		return this.goal.getControls();
	}

	public boolean isRunning() {
		return this.running;
	}

	public int getPriority() {
		return this.priority;
	}

	public Goal getGoal() {
		return this.goal;
	}

	public boolean equals(@Nullable Object o) {
		if (this == o) {
			return true;
		} else {
			return o != null && this.getClass() == o.getClass() ? this.goal.equals(((PrioritizedGoal)o).goal) : false;
		}
	}

	public int hashCode() {
		return this.goal.hashCode();
	}
}

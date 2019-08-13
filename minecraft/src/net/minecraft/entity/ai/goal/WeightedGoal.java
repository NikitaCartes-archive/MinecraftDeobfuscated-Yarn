package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import javax.annotation.Nullable;

public class WeightedGoal extends Goal {
	private final Goal goal;
	private final int weight;
	private boolean running;

	public WeightedGoal(int i, Goal goal) {
		this.weight = i;
		this.goal = goal;
	}

	public boolean canBeReplacedBy(WeightedGoal weightedGoal) {
		return this.canStop() && weightedGoal.getWeight() < this.getWeight();
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
	public void setControls(EnumSet<Goal.Control> enumSet) {
		this.goal.setControls(enumSet);
	}

	@Override
	public EnumSet<Goal.Control> getControls() {
		return this.goal.getControls();
	}

	public boolean isRunning() {
		return this.running;
	}

	public int getWeight() {
		return this.weight;
	}

	public Goal getGoal() {
		return this.goal;
	}

	public boolean equals(@Nullable Object object) {
		if (this == object) {
			return true;
		} else {
			return object != null && this.getClass() == object.getClass() ? this.goal.equals(((WeightedGoal)object).goal) : false;
		}
	}

	public int hashCode() {
		return this.goal.hashCode();
	}
}

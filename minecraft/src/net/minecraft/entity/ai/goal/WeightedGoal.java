package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import javax.annotation.Nullable;

public class WeightedGoal extends Goal {
	private final Goal goal;
	private final int weight;
	private boolean field_18418;

	public WeightedGoal(int i, Goal goal) {
		this.weight = i;
		this.goal = goal;
	}

	public boolean method_19055(WeightedGoal weightedGoal) {
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
		if (!this.field_18418) {
			this.field_18418 = true;
			this.goal.start();
		}
	}

	@Override
	public void onRemove() {
		if (this.field_18418) {
			this.field_18418 = false;
			this.goal.onRemove();
		}
	}

	@Override
	public void tick() {
		this.goal.tick();
	}

	@Override
	public void setControlBits(EnumSet<Goal.ControlBit> enumSet) {
		this.goal.setControlBits(enumSet);
	}

	@Override
	public EnumSet<Goal.ControlBit> getControlBits() {
		return this.goal.getControlBits();
	}

	public boolean method_19056() {
		return this.field_18418;
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

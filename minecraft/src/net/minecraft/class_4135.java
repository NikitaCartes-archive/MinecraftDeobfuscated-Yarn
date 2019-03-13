package net.minecraft;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.goal.Goal;

public class class_4135 extends Goal {
	private final Goal field_18416;
	private final int field_18417;
	private boolean field_18418;

	public class_4135(int i, Goal goal) {
		this.field_18417 = i;
		this.field_18416 = goal;
	}

	public boolean method_19055(class_4135 arg) {
		return this.canStop() && arg.method_19057() < this.method_19057();
	}

	@Override
	public boolean canStart() {
		return this.field_18416.canStart();
	}

	@Override
	public boolean shouldContinue() {
		return this.field_18416.shouldContinue();
	}

	@Override
	public boolean canStop() {
		return this.field_18416.canStop();
	}

	@Override
	public void start() {
		if (!this.field_18418) {
			this.field_18418 = true;
			this.field_18416.start();
		}
	}

	@Override
	public void onRemove() {
		if (this.field_18418) {
			this.field_18418 = false;
			this.field_18416.onRemove();
		}
	}

	@Override
	public void tick() {
		this.field_18416.tick();
	}

	@Override
	public void setControlBits(EnumSet<Goal.class_4134> enumSet) {
		this.field_18416.setControlBits(enumSet);
	}

	@Override
	public EnumSet<Goal.class_4134> getControlBits() {
		return this.field_18416.getControlBits();
	}

	public boolean method_19056() {
		return this.field_18418;
	}

	public int method_19057() {
		return this.field_18417;
	}

	public Goal method_19058() {
		return this.field_18416;
	}

	public boolean equals(@Nullable Object object) {
		if (this == object) {
			return true;
		} else {
			return object != null && this.getClass() == object.getClass() ? this.field_18416.equals(((class_4135)object).field_18416) : false;
		}
	}

	public int hashCode() {
		return this.field_18416.hashCode();
	}
}

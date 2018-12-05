package net.minecraft;

import javax.annotation.Nullable;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.util.math.Vec3d;

public class class_1379 extends Goal {
	protected final MobEntityWithAi field_6566;
	protected double field_6563;
	protected double field_6562;
	protected double field_6561;
	protected final double field_6567;
	protected int field_6564;
	protected boolean field_6565;

	public class_1379(MobEntityWithAi mobEntityWithAi, double d) {
		this(mobEntityWithAi, d, 120);
	}

	public class_1379(MobEntityWithAi mobEntityWithAi, double d, int i) {
		this.field_6566 = mobEntityWithAi;
		this.field_6567 = d;
		this.field_6564 = i;
		this.setControlBits(1);
	}

	@Override
	public boolean canStart() {
		if (this.field_6566.hasPassengers()) {
			return false;
		} else {
			if (!this.field_6565) {
				if (this.field_6566.method_6131() >= 100) {
					return false;
				}

				if (this.field_6566.getRand().nextInt(this.field_6564) != 0) {
					return false;
				}
			}

			Vec3d vec3d = this.method_6302();
			if (vec3d == null) {
				return false;
			} else {
				this.field_6563 = vec3d.x;
				this.field_6562 = vec3d.y;
				this.field_6561 = vec3d.z;
				this.field_6565 = false;
				return true;
			}
		}
	}

	@Nullable
	protected Vec3d method_6302() {
		return class_1414.method_6375(this.field_6566, 10, 7);
	}

	@Override
	public boolean shouldContinue() {
		return !this.field_6566.getNavigation().method_6357();
	}

	@Override
	public void start() {
		this.field_6566.getNavigation().method_6337(this.field_6563, this.field_6562, this.field_6561, this.field_6567);
	}

	public void method_6304() {
		this.field_6565 = true;
	}

	public void method_6303(int i) {
		this.field_6564 = i;
	}
}

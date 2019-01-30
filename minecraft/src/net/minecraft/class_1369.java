package net.minecraft;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.util.math.Vec3d;

public class class_1369 extends Goal {
	private final MobEntityWithAi field_6528;
	private LivingEntity field_6529;
	private double field_6527;
	private double field_6526;
	private double field_6531;
	private final double field_6530;
	private final float field_6532;

	public class_1369(MobEntityWithAi mobEntityWithAi, double d, float f) {
		this.field_6528 = mobEntityWithAi;
		this.field_6530 = d;
		this.field_6532 = f;
		this.setControlBits(1);
	}

	@Override
	public boolean canStart() {
		this.field_6529 = this.field_6528.getTarget();
		if (this.field_6529 == null) {
			return false;
		} else if (this.field_6529.squaredDistanceTo(this.field_6528) > (double)(this.field_6532 * this.field_6532)) {
			return false;
		} else {
			Vec3d vec3d = class_1414.method_6373(this.field_6528, 16, 7, new Vec3d(this.field_6529.x, this.field_6529.y, this.field_6529.z));
			if (vec3d == null) {
				return false;
			} else {
				this.field_6527 = vec3d.x;
				this.field_6526 = vec3d.y;
				this.field_6531 = vec3d.z;
				return true;
			}
		}
	}

	@Override
	public boolean shouldContinue() {
		return !this.field_6528.getNavigation().isIdle()
			&& this.field_6529.isValid()
			&& this.field_6529.squaredDistanceTo(this.field_6528) < (double)(this.field_6532 * this.field_6532);
	}

	@Override
	public void onRemove() {
		this.field_6529 = null;
	}

	@Override
	public void start() {
		this.field_6528.getNavigation().startMovingTo(this.field_6527, this.field_6526, this.field_6531, this.field_6530);
	}
}

package net.minecraft;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class class_1370 extends Goal {
	private final MobEntityWithAi field_6536;
	private double field_6535;
	private double field_6534;
	private double field_6533;
	private final double field_6537;

	public class_1370(MobEntityWithAi mobEntityWithAi, double d) {
		this.field_6536 = mobEntityWithAi;
		this.field_6537 = d;
		this.setControlBits(1);
	}

	@Override
	public boolean canStart() {
		if (this.field_6536.isInAiRange()) {
			return false;
		} else {
			BlockPos blockPos = this.field_6536.getAiHome();
			Vec3d vec3d = class_1414.method_6373(this.field_6536, 16, 7, new Vec3d((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ()));
			if (vec3d == null) {
				return false;
			} else {
				this.field_6535 = vec3d.x;
				this.field_6534 = vec3d.y;
				this.field_6533 = vec3d.z;
				return true;
			}
		}
	}

	@Override
	public boolean shouldContinue() {
		return !this.field_6536.getNavigation().method_6357();
	}

	@Override
	public void start() {
		this.field_6536.getNavigation().method_6337(this.field_6535, this.field_6534, this.field_6533, this.field_6537);
	}
}

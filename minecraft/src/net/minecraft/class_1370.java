package net.minecraft;

import java.util.EnumSet;
import net.minecraft.entity.ai.AiUtil;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class class_1370 extends Goal {
	private final MobEntityWithAi mob;
	private double x;
	private double y;
	private double z;
	private final double speed;

	public class_1370(MobEntityWithAi mobEntityWithAi, double d) {
		this.mob = mobEntityWithAi;
		this.speed = d;
		this.setControlBits(EnumSet.of(Goal.ControlBit.field_18405));
	}

	@Override
	public boolean canStart() {
		if (this.mob.method_18411()) {
			return false;
		} else {
			BlockPos blockPos = this.mob.method_18412();
			Vec3d vec3d = AiUtil.method_6373(this.mob, 16, 7, new Vec3d((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ()));
			if (vec3d == null) {
				return false;
			} else {
				this.x = vec3d.x;
				this.y = vec3d.y;
				this.z = vec3d.z;
				return true;
			}
		}
	}

	@Override
	public boolean shouldContinue() {
		return !this.mob.getNavigation().isIdle();
	}

	@Override
	public void start() {
		this.mob.getNavigation().startMovingTo(this.x, this.y, this.z, this.speed);
	}
}

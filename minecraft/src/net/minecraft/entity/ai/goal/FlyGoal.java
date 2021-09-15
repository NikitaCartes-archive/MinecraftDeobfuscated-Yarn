package net.minecraft.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.entity.ai.AboveGroundTargeting;
import net.minecraft.entity.ai.NoPenaltySolidTargeting;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.Vec3d;

public class FlyGoal extends WanderAroundFarGoal {
	public FlyGoal(PathAwareEntity pathAwareEntity, double d) {
		super(pathAwareEntity, d);
	}

	@Nullable
	@Override
	protected Vec3d getWanderTarget() {
		Vec3d vec3d = this.mob.getRotationVec(0.0F);
		int i = 8;
		Vec3d vec3d2 = AboveGroundTargeting.find(this.mob, 8, 7, vec3d.x, vec3d.z, (float) (Math.PI / 2), 3, 1);
		return vec3d2 != null ? vec3d2 : NoPenaltySolidTargeting.find(this.mob, 8, 4, -2, vec3d.x, vec3d.z, (float) (Math.PI / 2));
	}
}

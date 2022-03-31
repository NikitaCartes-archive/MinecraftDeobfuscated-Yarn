package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.ai.NoPenaltySolidTargeting;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.Vec3d;

public class NoPenaltyStrollTask extends StrollTask {
	public NoPenaltyStrollTask(float f) {
		this(f, true);
	}

	public NoPenaltyStrollTask(float f, boolean bl) {
		super(f, bl);
	}

	@Override
	protected Vec3d findWalkTarget(PathAwareEntity entity) {
		Vec3d vec3d = entity.getRotationVec(0.0F);
		return NoPenaltySolidTargeting.find(entity, this.horizontalRadius, this.verticalRadius, -2, vec3d.x, vec3d.z, (float) (Math.PI / 2));
	}
}

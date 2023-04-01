package net.minecraft.entity.ai.goal;

import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.Vec3d;

public class WanderAroundFarGoal extends WanderAroundGoal {
	public static final float CHANCE = 0.001F;
	protected final float probability;

	public WanderAroundFarGoal(PathAwareEntity pathAwareEntity, double d) {
		this(pathAwareEntity, d, 0.001F);
	}

	public WanderAroundFarGoal(PathAwareEntity pathAwareEntity, double d, BooleanSupplier booleanSupplier) {
		this(pathAwareEntity, d, 0.001F);
		this.field_44098 = booleanSupplier;
	}

	public WanderAroundFarGoal(PathAwareEntity mob, double speed, float probability) {
		super(mob, speed);
		this.probability = probability;
	}

	@Nullable
	@Override
	protected Vec3d getWanderTarget() {
		if (this.mob.isInsideWaterOrBubbleColumn()) {
			Vec3d vec3d = FuzzyTargeting.find(this.mob, 15, 7);
			return vec3d == null ? super.getWanderTarget() : vec3d;
		} else {
			return this.mob.getRandom().nextFloat() >= this.probability ? FuzzyTargeting.find(this.mob, 10, 7) : super.getWanderTarget();
		}
	}
}

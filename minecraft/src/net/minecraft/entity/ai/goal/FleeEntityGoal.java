package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.math.Vec3d;

public class FleeEntityGoal<T extends LivingEntity> extends Goal {
	protected final PathAwareEntity mob;
	private final double slowSpeed;
	private final double fastSpeed;
	protected T targetEntity;
	protected final float fleeDistance;
	protected Path fleePath;
	protected final EntityNavigation fleeingEntityNavigation;
	protected final Class<T> classToFleeFrom;
	protected final Predicate<LivingEntity> extraInclusionSelector;
	protected final Predicate<LivingEntity> inclusionSelector;
	private final TargetPredicate withinRangePredicate;

	public FleeEntityGoal(PathAwareEntity mob, Class<T> fleeFromType, float distance, double slowSpeed, double fastSpeed) {
		this(mob, fleeFromType, livingEntity -> true, distance, slowSpeed, fastSpeed, EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR::test);
	}

	public FleeEntityGoal(
		PathAwareEntity mob,
		Class<T> fleeFromType,
		Predicate<LivingEntity> extraInclusionSelector,
		float distance,
		double slowSpeed,
		double fastSpeed,
		Predicate<LivingEntity> inclusionSelector
	) {
		this.mob = mob;
		this.classToFleeFrom = fleeFromType;
		this.extraInclusionSelector = extraInclusionSelector;
		this.fleeDistance = distance;
		this.slowSpeed = slowSpeed;
		this.fastSpeed = fastSpeed;
		this.inclusionSelector = inclusionSelector;
		this.fleeingEntityNavigation = mob.getNavigation();
		this.setControls(EnumSet.of(Goal.Control.MOVE));
		this.withinRangePredicate = new TargetPredicate().setBaseMaxDistance((double)distance).setPredicate(inclusionSelector.and(extraInclusionSelector));
	}

	public FleeEntityGoal(
		PathAwareEntity fleeingEntity,
		Class<T> classToFleeFrom,
		float fleeDistance,
		double fleeSlowSpeed,
		double fleeFastSpeed,
		Predicate<LivingEntity> inclusionSelector
	) {
		this(fleeingEntity, classToFleeFrom, livingEntity -> true, fleeDistance, fleeSlowSpeed, fleeFastSpeed, inclusionSelector);
	}

	@Override
	public boolean canStart() {
		this.targetEntity = this.mob
			.world
			.getClosestEntityIncludingUngeneratedChunks(
				this.classToFleeFrom,
				this.withinRangePredicate,
				this.mob,
				this.mob.getX(),
				this.mob.getY(),
				this.mob.getZ(),
				this.mob.getBoundingBox().expand((double)this.fleeDistance, 3.0, (double)this.fleeDistance)
			);
		if (this.targetEntity == null) {
			return false;
		} else {
			Vec3d vec3d = TargetFinder.findTargetAwayFrom(this.mob, 16, 7, this.targetEntity.getPos());
			if (vec3d == null) {
				return false;
			} else if (this.targetEntity.squaredDistanceTo(vec3d.x, vec3d.y, vec3d.z) < this.targetEntity.squaredDistanceTo(this.mob)) {
				return false;
			} else {
				this.fleePath = this.fleeingEntityNavigation.findPathTo(vec3d.x, vec3d.y, vec3d.z, 0);
				return this.fleePath != null;
			}
		}
	}

	@Override
	public boolean shouldContinue() {
		return !this.fleeingEntityNavigation.isIdle();
	}

	@Override
	public void start() {
		this.fleeingEntityNavigation.startMovingAlong(this.fleePath, this.slowSpeed);
	}

	@Override
	public void stop() {
		this.targetEntity = null;
	}

	@Override
	public void tick() {
		if (this.mob.squaredDistanceTo(this.targetEntity) < 49.0) {
			this.mob.getNavigation().setSpeed(this.fastSpeed);
		} else {
			this.mob.getNavigation().setSpeed(this.slowSpeed);
		}
	}
}

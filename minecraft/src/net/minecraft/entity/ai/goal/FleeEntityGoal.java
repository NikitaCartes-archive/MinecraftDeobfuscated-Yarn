package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.PathfindingUtil;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.math.Vec3d;

public class FleeEntityGoal<T extends LivingEntity> extends Goal {
	protected final MobEntityWithAi mob;
	private final double slowSpeed;
	private final double fastSpeed;
	protected T targetEntity;
	protected final float fleeDistance;
	protected Path fleePath;
	protected final EntityNavigation fleeingEntityNavigation;
	protected final Class<T> classToFleeFrom;
	protected final Predicate<LivingEntity> field_6393;
	protected final Predicate<LivingEntity> field_6388;
	private final TargetPredicate withinRangePredicate;

	public FleeEntityGoal(MobEntityWithAi mobEntityWithAi, Class<T> class_, float f, double d, double e) {
		this(mobEntityWithAi, class_, livingEntity -> true, f, d, e, EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR::test);
	}

	public FleeEntityGoal(
		MobEntityWithAi mobEntityWithAi, Class<T> class_, Predicate<LivingEntity> predicate, float f, double d, double e, Predicate<LivingEntity> predicate2
	) {
		this.mob = mobEntityWithAi;
		this.classToFleeFrom = class_;
		this.field_6393 = predicate;
		this.fleeDistance = f;
		this.slowSpeed = d;
		this.fastSpeed = e;
		this.field_6388 = predicate2;
		this.fleeingEntityNavigation = mobEntityWithAi.getNavigation();
		this.setControls(EnumSet.of(Goal.Control.field_18405));
		this.withinRangePredicate = new TargetPredicate().setBaseMaxDistance((double)f).setPredicate(predicate2.and(predicate));
	}

	public FleeEntityGoal(MobEntityWithAi mobEntityWithAi, Class<T> class_, float f, double d, double e, Predicate<LivingEntity> predicate) {
		this(mobEntityWithAi, class_, livingEntity -> true, f, d, e, predicate);
	}

	@Override
	public boolean canStart() {
		this.targetEntity = this.mob
			.world
			.method_21727(
				this.classToFleeFrom,
				this.withinRangePredicate,
				this.mob,
				this.mob.x,
				this.mob.y,
				this.mob.z,
				this.mob.getBoundingBox().expand((double)this.fleeDistance, 3.0, (double)this.fleeDistance)
			);
		if (this.targetEntity == null) {
			return false;
		} else {
			Vec3d vec3d = PathfindingUtil.method_6379(this.mob, 16, 7, new Vec3d(this.targetEntity.x, this.targetEntity.y, this.targetEntity.z));
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

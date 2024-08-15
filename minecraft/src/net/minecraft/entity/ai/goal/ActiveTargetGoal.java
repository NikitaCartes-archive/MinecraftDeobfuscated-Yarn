package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;

/**
 * A target goal that finds a target by entity class when the goal starts.
 */
public class ActiveTargetGoal<T extends LivingEntity> extends TrackTargetGoal {
	private static final int DEFAULT_RECIPROCAL_CHANCE = 10;
	protected final Class<T> targetClass;
	/**
	 * The reciprocal of chance to actually search for a target on every tick
	 * when this goal is not started. This is also the average number of ticks
	 * between each search (as in a poisson distribution).
	 */
	protected final int reciprocalChance;
	@Nullable
	protected LivingEntity targetEntity;
	protected TargetPredicate targetPredicate;

	public ActiveTargetGoal(MobEntity mob, Class<T> targetClass, boolean checkVisibility) {
		this(mob, targetClass, 10, checkVisibility, false, null);
	}

	public ActiveTargetGoal(MobEntity mob, Class<T> targetClass, boolean checkVisibility, Predicate<LivingEntity> targetPredicate) {
		this(mob, targetClass, 10, checkVisibility, false, targetPredicate);
	}

	public ActiveTargetGoal(MobEntity mob, Class<T> targetClass, boolean checkVisibility, boolean checkCanNavigate) {
		this(mob, targetClass, 10, checkVisibility, checkCanNavigate, null);
	}

	public ActiveTargetGoal(
		MobEntity mob,
		Class<T> targetClass,
		int reciprocalChance,
		boolean checkVisibility,
		boolean checkCanNavigate,
		@Nullable Predicate<LivingEntity> targetPredicate
	) {
		super(mob, checkVisibility, checkCanNavigate);
		this.targetClass = targetClass;
		this.reciprocalChance = toGoalTicks(reciprocalChance);
		this.setControls(EnumSet.of(Goal.Control.TARGET));
		this.targetPredicate = TargetPredicate.createAttackable().setBaseMaxDistance(this.getFollowRange()).setPredicate(targetPredicate);
	}

	@Override
	public boolean canStart() {
		if (this.reciprocalChance > 0 && this.mob.getRandom().nextInt(this.reciprocalChance) != 0) {
			return false;
		} else {
			this.findClosestTarget();
			return this.targetEntity != null;
		}
	}

	protected Box getSearchBox(double distance) {
		return this.mob.getBoundingBox().expand(distance, distance, distance);
	}

	protected void findClosestTarget() {
		if (this.targetClass != PlayerEntity.class && this.targetClass != ServerPlayerEntity.class) {
			this.targetEntity = this.mob
				.getWorld()
				.getClosestEntity(
					this.mob.getWorld().getEntitiesByClass(this.targetClass, this.getSearchBox(this.getFollowRange()), livingEntity -> true),
					this.getAndUpdateTargetPredicate(),
					this.mob,
					this.mob.getX(),
					this.mob.getEyeY(),
					this.mob.getZ()
				);
		} else {
			this.targetEntity = this.mob.getWorld().getClosestPlayer(this.getAndUpdateTargetPredicate(), this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
		}
	}

	@Override
	public void start() {
		this.mob.setTarget(this.targetEntity);
		super.start();
	}

	public void setTargetEntity(@Nullable LivingEntity targetEntity) {
		this.targetEntity = targetEntity;
	}

	private TargetPredicate getAndUpdateTargetPredicate() {
		return this.targetPredicate.setBaseMaxDistance(this.getFollowRange());
	}
}

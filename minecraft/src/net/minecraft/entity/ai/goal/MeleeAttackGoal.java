package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.Hand;

public class MeleeAttackGoal extends Goal {
	protected final MobEntityWithAi mob;
	protected int ticksUntilAttack;
	private final double speed;
	private final boolean pauseWhenMobIdle;
	private Path path;
	private int updateCountdownTicks;
	private double targetX;
	private double targetY;
	private double targetZ;
	protected final int attackIntervalTicks = 20;
	private long lastUpdateTime;

	public MeleeAttackGoal(MobEntityWithAi mob, double speed, boolean pauseWhenMobIdle) {
		this.mob = mob;
		this.speed = speed;
		this.pauseWhenMobIdle = pauseWhenMobIdle;
		this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
	}

	@Override
	public boolean canStart() {
		long l = this.mob.world.getTime();
		if (l - this.lastUpdateTime < 20L) {
			return false;
		} else {
			this.lastUpdateTime = l;
			LivingEntity livingEntity = this.mob.getTarget();
			if (livingEntity == null) {
				return false;
			} else if (!livingEntity.isAlive()) {
				return false;
			} else {
				this.path = this.mob.getNavigation().findPathTo(livingEntity, 0);
				return this.path != null
					? true
					: this.getSquaredMaxAttackDistance(livingEntity) >= this.mob.squaredDistanceTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
			}
		}
	}

	@Override
	public boolean shouldContinue() {
		LivingEntity livingEntity = this.mob.getTarget();
		if (livingEntity == null) {
			return false;
		} else if (!livingEntity.isAlive()) {
			return false;
		} else if (!this.pauseWhenMobIdle) {
			return !this.mob.getNavigation().isIdle();
		} else {
			return !this.mob.isInWalkTargetRange(livingEntity.getSenseCenterPos())
				? false
				: !(livingEntity instanceof PlayerEntity) || !livingEntity.isSpectator() && !((PlayerEntity)livingEntity).isCreative();
		}
	}

	@Override
	public void start() {
		this.mob.getNavigation().startMovingAlong(this.path, this.speed);
		this.mob.setAttacking(true);
		this.updateCountdownTicks = 0;
	}

	@Override
	public void stop() {
		LivingEntity livingEntity = this.mob.getTarget();
		if (!EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR.test(livingEntity)) {
			this.mob.setTarget(null);
		}

		this.mob.setAttacking(false);
		this.mob.getNavigation().stop();
	}

	@Override
	public void tick() {
		LivingEntity livingEntity = this.mob.getTarget();
		this.mob.getLookControl().lookAt(livingEntity, 30.0F, 30.0F);
		double d = this.mob.squaredDistanceTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
		this.updateCountdownTicks--;
		if ((this.pauseWhenMobIdle || this.mob.getVisibilityCache().canSee(livingEntity))
			&& this.updateCountdownTicks <= 0
			&& (
				this.targetX == 0.0 && this.targetY == 0.0 && this.targetZ == 0.0
					|| livingEntity.squaredDistanceTo(this.targetX, this.targetY, this.targetZ) >= 1.0
					|| this.mob.getRandom().nextFloat() < 0.05F
			)) {
			this.targetX = livingEntity.getX();
			this.targetY = livingEntity.getY();
			this.targetZ = livingEntity.getZ();
			this.updateCountdownTicks = 4 + this.mob.getRandom().nextInt(7);
			if (d > 1024.0) {
				this.updateCountdownTicks += 10;
			} else if (d > 256.0) {
				this.updateCountdownTicks += 5;
			}

			if (!this.mob.getNavigation().startMovingTo(livingEntity, this.speed)) {
				this.updateCountdownTicks += 15;
			}
		}

		this.ticksUntilAttack = Math.max(this.ticksUntilAttack - 1, 0);
		this.attack(livingEntity, d);
	}

	protected void attack(LivingEntity target, double squaredDistance) {
		double d = this.getSquaredMaxAttackDistance(target);
		if (squaredDistance <= d && this.ticksUntilAttack <= 0) {
			this.ticksUntilAttack = 20;
			this.mob.swingHand(Hand.MAIN_HAND);
			this.mob.tryAttack(target);
		}
	}

	protected double getSquaredMaxAttackDistance(LivingEntity entity) {
		return (double)(this.mob.getWidth() * 2.0F * this.mob.getWidth() * 2.0F + entity.getWidth());
	}
}

package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public class MeleeAttackGoal extends Goal {
	protected final MobEntityWithAi mob;
	protected int ticksUntilAttack;
	private final double speed;
	private final boolean field_6502;
	private Path field_6509;
	private int field_6501;
	private double targetX;
	private double targetY;
	private double targetZ;
	protected final int field_6504 = 20;
	private long field_19200;

	public MeleeAttackGoal(MobEntityWithAi mobEntityWithAi, double d, boolean bl) {
		this.mob = mobEntityWithAi;
		this.speed = d;
		this.field_6502 = bl;
		this.setControls(EnumSet.of(Goal.Control.field_18405, Goal.Control.field_18406));
	}

	@Override
	public boolean canStart() {
		long l = this.mob.world.getTime();
		if (l - this.field_19200 < 20L) {
			return false;
		} else {
			this.field_19200 = l;
			LivingEntity livingEntity = this.mob.getTarget();
			if (livingEntity == null) {
				return false;
			} else if (!livingEntity.isAlive()) {
				return false;
			} else {
				this.field_6509 = this.mob.getNavigation().findPathTo(livingEntity, 0);
				return this.field_6509 != null
					? true
					: this.getSquaredMaxAttackDistance(livingEntity) >= this.mob.squaredDistanceTo(livingEntity.x, livingEntity.getBoundingBox().minY, livingEntity.z);
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
		} else if (!this.field_6502) {
			return !this.mob.getNavigation().isIdle();
		} else {
			return !this.mob.isInWalkTargetRange(new BlockPos(livingEntity))
				? false
				: !(livingEntity instanceof PlayerEntity) || !livingEntity.isSpectator() && !((PlayerEntity)livingEntity).isCreative();
		}
	}

	@Override
	public void start() {
		this.mob.getNavigation().startMovingAlong(this.field_6509, this.speed);
		this.mob.setAttacking(true);
		this.field_6501 = 0;
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
		double d = this.mob.squaredDistanceTo(livingEntity.x, livingEntity.getBoundingBox().minY, livingEntity.z);
		this.field_6501--;
		if ((this.field_6502 || this.mob.getVisibilityCache().canSee(livingEntity))
			&& this.field_6501 <= 0
			&& (
				this.targetX == 0.0 && this.targetY == 0.0 && this.targetZ == 0.0
					|| livingEntity.squaredDistanceTo(this.targetX, this.targetY, this.targetZ) >= 1.0
					|| this.mob.getRand().nextFloat() < 0.05F
			)) {
			this.targetX = livingEntity.x;
			this.targetY = livingEntity.getBoundingBox().minY;
			this.targetZ = livingEntity.z;
			this.field_6501 = 4 + this.mob.getRand().nextInt(7);
			if (d > 1024.0) {
				this.field_6501 += 10;
			} else if (d > 256.0) {
				this.field_6501 += 5;
			}

			if (!this.mob.getNavigation().startMovingTo(livingEntity, this.speed)) {
				this.field_6501 += 15;
			}
		}

		this.ticksUntilAttack = Math.max(this.ticksUntilAttack - 1, 0);
		this.attack(livingEntity, d);
	}

	protected void attack(LivingEntity livingEntity, double d) {
		double e = this.getSquaredMaxAttackDistance(livingEntity);
		if (d <= e && this.ticksUntilAttack <= 0) {
			this.ticksUntilAttack = 20;
			this.mob.swingHand(Hand.field_5808);
			this.mob.tryAttack(livingEntity);
		}
	}

	protected double getSquaredMaxAttackDistance(LivingEntity livingEntity) {
		return (double)(this.mob.getWidth() * 2.0F * this.mob.getWidth() * 2.0F + livingEntity.getWidth());
	}
}

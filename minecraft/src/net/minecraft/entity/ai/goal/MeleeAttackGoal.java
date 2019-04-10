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
	protected final MobEntityWithAi entity;
	protected int ticksUntilAttack;
	private final double field_6500;
	private final boolean field_6502;
	private Path field_6509;
	private int field_6501;
	private double field_6508;
	private double field_6507;
	private double field_6506;
	protected final int field_6504 = 20;
	private long field_19200;

	public MeleeAttackGoal(MobEntityWithAi mobEntityWithAi, double d, boolean bl) {
		this.entity = mobEntityWithAi;
		this.field_6500 = d;
		this.field_6502 = bl;
		this.setControls(EnumSet.of(Goal.Control.field_18405, Goal.Control.field_18406));
	}

	@Override
	public boolean canStart() {
		long l = this.entity.world.getTime();
		if (l - this.field_19200 < 20L) {
			return false;
		} else {
			this.field_19200 = l;
			LivingEntity livingEntity = this.entity.getTarget();
			if (livingEntity == null) {
				return false;
			} else if (!livingEntity.isAlive()) {
				return false;
			} else {
				this.field_6509 = this.entity.getNavigation().findPathTo(livingEntity);
				return this.field_6509 != null
					? true
					: this.getSquaredMaxAttackDistance(livingEntity) >= this.entity.squaredDistanceTo(livingEntity.x, livingEntity.getBoundingBox().minY, livingEntity.z);
			}
		}
	}

	@Override
	public boolean shouldContinue() {
		LivingEntity livingEntity = this.entity.getTarget();
		if (livingEntity == null) {
			return false;
		} else if (!livingEntity.isAlive()) {
			return false;
		} else if (!this.field_6502) {
			return !this.entity.getNavigation().isIdle();
		} else {
			return !this.entity.isInWalkTargetRange(new BlockPos(livingEntity))
				? false
				: !(livingEntity instanceof PlayerEntity) || !livingEntity.isSpectator() && !((PlayerEntity)livingEntity).isCreative();
		}
	}

	@Override
	public void start() {
		this.entity.getNavigation().startMovingAlong(this.field_6509, this.field_6500);
		this.entity.setAttacking(true);
		this.field_6501 = 0;
	}

	@Override
	public void stop() {
		LivingEntity livingEntity = this.entity.getTarget();
		if (!EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR.test(livingEntity)) {
			this.entity.setTarget(null);
		}

		this.entity.setAttacking(false);
		this.entity.getNavigation().stop();
	}

	@Override
	public void tick() {
		LivingEntity livingEntity = this.entity.getTarget();
		this.entity.getLookControl().lookAt(livingEntity, 30.0F, 30.0F);
		double d = this.entity.squaredDistanceTo(livingEntity.x, livingEntity.getBoundingBox().minY, livingEntity.z);
		this.field_6501--;
		if ((this.field_6502 || this.entity.getVisibilityCache().canSee(livingEntity))
			&& this.field_6501 <= 0
			&& (
				this.field_6508 == 0.0 && this.field_6507 == 0.0 && this.field_6506 == 0.0
					|| livingEntity.squaredDistanceTo(this.field_6508, this.field_6507, this.field_6506) >= 1.0
					|| this.entity.getRand().nextFloat() < 0.05F
			)) {
			this.field_6508 = livingEntity.x;
			this.field_6507 = livingEntity.getBoundingBox().minY;
			this.field_6506 = livingEntity.z;
			this.field_6501 = 4 + this.entity.getRand().nextInt(7);
			if (d > 1024.0) {
				this.field_6501 += 10;
			} else if (d > 256.0) {
				this.field_6501 += 5;
			}

			if (!this.entity.getNavigation().startMovingTo(livingEntity, this.field_6500)) {
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
			this.entity.swingHand(Hand.MAIN);
			this.entity.attack(livingEntity);
		}
	}

	protected double getSquaredMaxAttackDistance(LivingEntity livingEntity) {
		return (double)(this.entity.getWidth() * 2.0F * this.entity.getWidth() * 2.0F + livingEntity.getWidth());
	}
}

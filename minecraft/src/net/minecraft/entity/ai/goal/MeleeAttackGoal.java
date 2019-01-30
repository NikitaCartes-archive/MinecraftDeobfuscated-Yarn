package net.minecraft.entity.ai.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public class MeleeAttackGoal extends Goal {
	protected final MobEntityWithAi entity;
	protected int field_6505;
	private final double field_6500;
	private final boolean field_6502;
	private Path field_6509;
	private int field_6501;
	private double field_6508;
	private double field_6507;
	private double field_6506;
	protected final int field_6504 = 20;

	public MeleeAttackGoal(MobEntityWithAi mobEntityWithAi, double d, boolean bl) {
		this.entity = mobEntityWithAi;
		this.field_6500 = d;
		this.field_6502 = bl;
		this.setControlBits(3);
	}

	@Override
	public boolean canStart() {
		LivingEntity livingEntity = this.entity.getTarget();
		if (livingEntity == null) {
			return false;
		} else if (!livingEntity.isValid()) {
			return false;
		} else {
			this.field_6509 = this.entity.getNavigation().findPathTo(livingEntity);
			return this.field_6509 != null
				? true
				: this.method_6289(livingEntity) >= this.entity.squaredDistanceTo(livingEntity.x, livingEntity.getBoundingBox().minY, livingEntity.z);
		}
	}

	@Override
	public boolean shouldContinue() {
		LivingEntity livingEntity = this.entity.getTarget();
		if (livingEntity == null) {
			return false;
		} else if (!livingEntity.isValid()) {
			return false;
		} else if (!this.field_6502) {
			return !this.entity.getNavigation().isIdle();
		} else {
			return !this.entity.isInAiRange(new BlockPos(livingEntity))
				? false
				: !(livingEntity instanceof PlayerEntity) || !((PlayerEntity)livingEntity).isSpectator() && !((PlayerEntity)livingEntity).isCreative();
		}
	}

	@Override
	public void start() {
		this.entity.getNavigation().startMovingAlong(this.field_6509, this.field_6500);
		this.field_6501 = 0;
	}

	@Override
	public void onRemove() {
		LivingEntity livingEntity = this.entity.getTarget();
		if (livingEntity instanceof PlayerEntity && (((PlayerEntity)livingEntity).isSpectator() || ((PlayerEntity)livingEntity).isCreative())) {
			this.entity.setTarget(null);
		}

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

		this.field_6505 = Math.max(this.field_6505 - 1, 0);
		this.method_6288(livingEntity, d);
	}

	protected void method_6288(LivingEntity livingEntity, double d) {
		double e = this.method_6289(livingEntity);
		if (d <= e && this.field_6505 <= 0) {
			this.field_6505 = 20;
			this.entity.swingHand(Hand.MAIN);
			this.entity.method_6121(livingEntity);
		}
	}

	protected double method_6289(LivingEntity livingEntity) {
		return (double)(this.entity.getWidth() * 2.0F * this.entity.getWidth() * 2.0F + livingEntity.getWidth());
	}
}

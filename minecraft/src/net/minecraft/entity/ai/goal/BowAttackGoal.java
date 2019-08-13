package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ProjectileUtil;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.Items;

public class BowAttackGoal<T extends HostileEntity & RangedAttackMob> extends Goal {
	private final T actor;
	private final double speed;
	private int attackInterval;
	private final float squaredRange;
	private int cooldown = -1;
	private int field_6572;
	private boolean field_6573;
	private boolean field_6571;
	private int field_6568 = -1;

	public BowAttackGoal(T hostileEntity, double d, int i, float f) {
		this.actor = hostileEntity;
		this.speed = d;
		this.attackInterval = i;
		this.squaredRange = f * f;
		this.setControls(EnumSet.of(Goal.Control.field_18405, Goal.Control.field_18406));
	}

	public void setAttackInterval(int i) {
		this.attackInterval = i;
	}

	@Override
	public boolean canStart() {
		return this.actor.getTarget() == null ? false : this.isHoldingBow();
	}

	protected boolean isHoldingBow() {
		return this.actor.isHolding(Items.field_8102);
	}

	@Override
	public boolean shouldContinue() {
		return (this.canStart() || !this.actor.getNavigation().isIdle()) && this.isHoldingBow();
	}

	@Override
	public void start() {
		super.start();
		this.actor.setAttacking(true);
	}

	@Override
	public void stop() {
		super.stop();
		this.actor.setAttacking(false);
		this.field_6572 = 0;
		this.cooldown = -1;
		this.actor.clearActiveItem();
	}

	@Override
	public void tick() {
		LivingEntity livingEntity = this.actor.getTarget();
		if (livingEntity != null) {
			double d = this.actor.squaredDistanceTo(livingEntity.x, livingEntity.getBoundingBox().minY, livingEntity.z);
			boolean bl = this.actor.getVisibilityCache().canSee(livingEntity);
			boolean bl2 = this.field_6572 > 0;
			if (bl != bl2) {
				this.field_6572 = 0;
			}

			if (bl) {
				this.field_6572++;
			} else {
				this.field_6572--;
			}

			if (!(d > (double)this.squaredRange) && this.field_6572 >= 20) {
				this.actor.getNavigation().stop();
				this.field_6568++;
			} else {
				this.actor.getNavigation().startMovingTo(livingEntity, this.speed);
				this.field_6568 = -1;
			}

			if (this.field_6568 >= 20) {
				if ((double)this.actor.getRand().nextFloat() < 0.3) {
					this.field_6573 = !this.field_6573;
				}

				if ((double)this.actor.getRand().nextFloat() < 0.3) {
					this.field_6571 = !this.field_6571;
				}

				this.field_6568 = 0;
			}

			if (this.field_6568 > -1) {
				if (d > (double)(this.squaredRange * 0.75F)) {
					this.field_6571 = false;
				} else if (d < (double)(this.squaredRange * 0.25F)) {
					this.field_6571 = true;
				}

				this.actor.getMoveControl().strafeTo(this.field_6571 ? -0.5F : 0.5F, this.field_6573 ? 0.5F : -0.5F);
				this.actor.lookAtEntity(livingEntity, 30.0F, 30.0F);
			} else {
				this.actor.getLookControl().lookAt(livingEntity, 30.0F, 30.0F);
			}

			if (this.actor.isUsingItem()) {
				if (!bl && this.field_6572 < -60) {
					this.actor.clearActiveItem();
				} else if (bl) {
					int i = this.actor.getItemUseTime();
					if (i >= 20) {
						this.actor.clearActiveItem();
						this.actor.attack(livingEntity, BowItem.getPullProgress(i));
						this.cooldown = this.attackInterval;
					}
				}
			} else if (--this.cooldown <= 0 && this.field_6572 >= -60) {
				this.actor.setCurrentHand(ProjectileUtil.getHandPossiblyHolding(this.actor, Items.field_8102));
			}
		}
	}
}

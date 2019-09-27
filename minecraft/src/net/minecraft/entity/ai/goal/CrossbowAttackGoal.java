package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.CrossbowUser;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ProjectileUtil;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class CrossbowAttackGoal<T extends HostileEntity & RangedAttackMob & CrossbowUser> extends Goal {
	private final T actor;
	private CrossbowAttackGoal.Stage stage = CrossbowAttackGoal.Stage.UNCHARGED;
	private final double speed;
	private final float squaredRange;
	private int field_6592;
	private int field_16529;

	public CrossbowAttackGoal(T hostileEntity, double d, float f) {
		this.actor = hostileEntity;
		this.speed = d;
		this.squaredRange = f * f;
		this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
	}

	@Override
	public boolean canStart() {
		return this.method_19996() && this.isEntityHoldingCrossbow();
	}

	private boolean isEntityHoldingCrossbow() {
		return this.actor.isHolding(Items.CROSSBOW);
	}

	@Override
	public boolean shouldContinue() {
		return this.method_19996() && (this.canStart() || !this.actor.getNavigation().isIdle()) && this.isEntityHoldingCrossbow();
	}

	private boolean method_19996() {
		return this.actor.getTarget() != null && this.actor.getTarget().isAlive();
	}

	@Override
	public void stop() {
		super.stop();
		this.actor.setAttacking(false);
		this.actor.setTarget(null);
		this.field_6592 = 0;
		if (this.actor.isUsingItem()) {
			this.actor.clearActiveItem();
			this.actor.setCharging(false);
			CrossbowItem.setCharged(this.actor.getActiveItem(), false);
		}
	}

	@Override
	public void tick() {
		LivingEntity livingEntity = this.actor.getTarget();
		if (livingEntity != null) {
			boolean bl = this.actor.getVisibilityCache().canSee(livingEntity);
			boolean bl2 = this.field_6592 > 0;
			if (bl != bl2) {
				this.field_6592 = 0;
			}

			if (bl) {
				this.field_6592++;
			} else {
				this.field_6592--;
			}

			double d = this.actor.squaredDistanceTo(livingEntity);
			boolean bl3 = (d > (double)this.squaredRange || this.field_6592 < 5) && this.field_16529 == 0;
			if (bl3) {
				this.actor.getNavigation().startMovingTo(livingEntity, this.isUncharged() ? this.speed : this.speed * 0.5);
			} else {
				this.actor.getNavigation().stop();
			}

			this.actor.getLookControl().lookAt(livingEntity, 30.0F, 30.0F);
			if (this.stage == CrossbowAttackGoal.Stage.UNCHARGED) {
				if (!bl3) {
					this.actor.setCurrentHand(ProjectileUtil.getHandPossiblyHolding(this.actor, Items.CROSSBOW));
					this.stage = CrossbowAttackGoal.Stage.CHARGING;
					this.actor.setCharging(true);
				}
			} else if (this.stage == CrossbowAttackGoal.Stage.CHARGING) {
				if (!this.actor.isUsingItem()) {
					this.stage = CrossbowAttackGoal.Stage.UNCHARGED;
				}

				int i = this.actor.getItemUseTime();
				ItemStack itemStack = this.actor.getActiveItem();
				if (i >= CrossbowItem.getPullTime(itemStack)) {
					this.actor.stopUsingItem();
					this.stage = CrossbowAttackGoal.Stage.CHARGED;
					this.field_16529 = 20 + this.actor.getRandom().nextInt(20);
					this.actor.setCharging(false);
				}
			} else if (this.stage == CrossbowAttackGoal.Stage.CHARGED) {
				this.field_16529--;
				if (this.field_16529 == 0) {
					this.stage = CrossbowAttackGoal.Stage.READY_TO_ATTACK;
				}
			} else if (this.stage == CrossbowAttackGoal.Stage.READY_TO_ATTACK && bl) {
				this.actor.attack(livingEntity, 1.0F);
				ItemStack itemStack2 = this.actor.getStackInHand(ProjectileUtil.getHandPossiblyHolding(this.actor, Items.CROSSBOW));
				CrossbowItem.setCharged(itemStack2, false);
				this.stage = CrossbowAttackGoal.Stage.UNCHARGED;
			}
		}
	}

	private boolean isUncharged() {
		return this.stage == CrossbowAttackGoal.Stage.UNCHARGED;
	}

	static enum Stage {
		UNCHARGED,
		CHARGING,
		CHARGED,
		READY_TO_ATTACK;
	}
}

package net.minecraft.entity.ai.goal;

import net.minecraft.entity.CrossbowUser;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttacker;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

public class CrossbowAttackGoal<T extends HostileEntity & RangedAttacker & CrossbowUser> extends Goal {
	private final T entity;
	private CrossbowAttackGoal.Stage stage = CrossbowAttackGoal.Stage.field_16534;
	private final double field_6590;
	private final float field_6591;
	private int field_6592;
	private int field_16529;

	public CrossbowAttackGoal(T hostileEntity, double d, float f) {
		this.entity = hostileEntity;
		this.field_6590 = d;
		this.field_6591 = f * f;
		this.setControlBits(3);
	}

	@Override
	public boolean canStart() {
		return this.entity.getTarget() != null && this.isEntityHoldingCrossbow();
	}

	private boolean isEntityHoldingCrossbow() {
		return !this.entity.getMainHandStack().isEmpty() && this.entity.getMainHandStack().getItem() == Items.field_8399;
	}

	@Override
	public boolean shouldContinue() {
		return (this.canStart() || !this.entity.getNavigation().method_6357()) && this.isEntityHoldingCrossbow();
	}

	@Override
	public void onRemove() {
		super.onRemove();
		this.field_6592 = 0;
	}

	@Override
	public void tick() {
		LivingEntity livingEntity = this.entity.getTarget();
		if (livingEntity != null) {
			boolean bl = this.entity.getVisibilityCache().canSee(livingEntity);
			boolean bl2 = this.field_6592 > 0;
			if (bl != bl2) {
				this.field_6592 = 0;
			}

			if (bl) {
				this.field_6592++;
			} else {
				this.field_6592--;
			}

			double d = this.entity.squaredDistanceTo(livingEntity);
			boolean bl3 = (d > (double)this.field_6591 || this.field_6592 < 5) && this.field_16529 == 0;
			if (bl3) {
				this.entity.getNavigation().startMovingTo(livingEntity, this.isUncharged() ? this.field_6590 : this.field_6590 * 0.5);
			} else {
				this.entity.getNavigation().method_6340();
			}

			this.entity.getLookControl().lookAt(livingEntity, 30.0F, 30.0F);
			if (this.stage == CrossbowAttackGoal.Stage.field_16534) {
				if (!bl3) {
					this.entity.setCurrentHand(Hand.MAIN);
					this.stage = CrossbowAttackGoal.Stage.field_16530;
					this.entity.setCharging(true);
				}
			} else if (this.stage == CrossbowAttackGoal.Stage.field_16530) {
				int i = this.entity.method_6048();
				if (i >= CrossbowItem.getPullTime(this.entity.getMainHandStack())) {
					CrossbowItem.setCharged(this.entity.getMainHandStack(), true);
					this.entity.method_6075();
					this.stage = CrossbowAttackGoal.Stage.field_16532;
					this.field_16529 = 20 + this.entity.getRand().nextInt(20);
					this.entity.setCharging(false);
				}
			} else if (this.stage == CrossbowAttackGoal.Stage.field_16532) {
				this.field_16529--;
				if (this.field_16529 == 0) {
					this.stage = CrossbowAttackGoal.Stage.field_16533;
				}
			} else if (this.stage == CrossbowAttackGoal.Stage.field_16533 && bl) {
				this.entity.attack(livingEntity, 1.0F);
				CrossbowItem.setCharged(this.entity.getMainHandStack(), false);
				this.stage = CrossbowAttackGoal.Stage.field_16534;
			}
		}
	}

	private boolean isUncharged() {
		return this.stage == CrossbowAttackGoal.Stage.field_16534;
	}

	static enum Stage {
		field_16534,
		field_16530,
		field_16532,
		field_16533;
	}
}

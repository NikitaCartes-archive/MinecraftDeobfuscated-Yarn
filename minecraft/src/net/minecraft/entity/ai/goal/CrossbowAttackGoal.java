package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.CrossbowUser;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ProjectileUtil;
import net.minecraft.entity.ai.RangedAttacker;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

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
		this.setControlBits(EnumSet.of(Goal.ControlBit.field_18405, Goal.ControlBit.field_18406));
	}

	@Override
	public boolean canStart() {
		return this.entity.getTarget() != null && this.isEntityHoldingCrossbow();
	}

	private boolean isEntityHoldingCrossbow() {
		return this.entity.method_18809(Items.field_8399);
	}

	@Override
	public boolean shouldContinue() {
		return (this.canStart() || !this.entity.getNavigation().isIdle()) && this.isEntityHoldingCrossbow();
	}

	@Override
	public void onRemove() {
		super.onRemove();
		this.entity.method_19540(false);
		this.field_6592 = 0;
		if (this.entity.isUsingItem()) {
			this.entity.method_6021();
			this.entity.setCharging(false);
			CrossbowItem.setCharged(this.entity.getActiveItem(), false);
		}
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
				this.entity.getNavigation().stop();
			}

			this.entity.getLookControl().lookAt(livingEntity, 30.0F, 30.0F);
			if (this.stage == CrossbowAttackGoal.Stage.field_16534) {
				if (!bl3) {
					this.entity.setCurrentHand(ProjectileUtil.method_18812(this.entity, Items.field_8399));
					this.stage = CrossbowAttackGoal.Stage.field_16530;
					this.entity.setCharging(true);
				}
			} else if (this.stage == CrossbowAttackGoal.Stage.field_16530) {
				if (!this.entity.isUsingItem()) {
					this.stage = CrossbowAttackGoal.Stage.field_16534;
				}

				int i = this.entity.method_6048();
				ItemStack itemStack = this.entity.getActiveItem();
				if (i >= CrossbowItem.getPullTime(itemStack)) {
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
				ItemStack itemStack2 = this.entity.getStackInHand(ProjectileUtil.method_18812(this.entity, Items.field_8399));
				CrossbowItem.setCharged(itemStack2, false);
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

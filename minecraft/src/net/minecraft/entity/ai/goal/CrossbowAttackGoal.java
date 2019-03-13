package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.class_1675;
import net.minecraft.entity.CrossbowUser;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttacker;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class CrossbowAttackGoal<T extends HostileEntity & RangedAttacker & CrossbowUser> extends Goal {
	private final T field_6593;
	private CrossbowAttackGoal.Stage stage = CrossbowAttackGoal.Stage.field_16534;
	private final double field_6590;
	private final float field_6591;
	private int field_6592;
	private int field_16529;

	public CrossbowAttackGoal(T hostileEntity, double d, float f) {
		this.field_6593 = hostileEntity;
		this.field_6590 = d;
		this.field_6591 = f * f;
		this.setControlBits(EnumSet.of(Goal.class_4134.field_18405, Goal.class_4134.field_18406));
	}

	@Override
	public boolean canStart() {
		return this.field_6593.getTarget() != null && this.isEntityHoldingCrossbow();
	}

	private boolean isEntityHoldingCrossbow() {
		return this.field_6593.method_18809(Items.field_8399);
	}

	@Override
	public boolean shouldContinue() {
		return (this.canStart() || !this.field_6593.method_5942().isIdle()) && this.isEntityHoldingCrossbow();
	}

	@Override
	public void onRemove() {
		super.onRemove();
		this.field_6592 = 0;
		if (this.field_6593.isUsingItem()) {
			this.field_6593.method_6021();
			this.field_6593.setCharging(false);
			CrossbowItem.method_7782(this.field_6593.method_6030(), false);
		}
	}

	@Override
	public void tick() {
		LivingEntity livingEntity = this.field_6593.getTarget();
		if (livingEntity != null) {
			boolean bl = this.field_6593.method_5985().canSee(livingEntity);
			boolean bl2 = this.field_6592 > 0;
			if (bl != bl2) {
				this.field_6592 = 0;
			}

			if (bl) {
				this.field_6592++;
			} else {
				this.field_6592--;
			}

			double d = this.field_6593.squaredDistanceTo(livingEntity);
			boolean bl3 = (d > (double)this.field_6591 || this.field_6592 < 5) && this.field_16529 == 0;
			if (bl3) {
				this.field_6593.method_5942().startMovingTo(livingEntity, this.isUncharged() ? this.field_6590 : this.field_6590 * 0.5);
			} else {
				this.field_6593.method_5942().stop();
			}

			this.field_6593.method_5988().lookAt(livingEntity, 30.0F, 30.0F);
			if (this.stage == CrossbowAttackGoal.Stage.field_16534) {
				if (!bl3) {
					this.field_6593.setCurrentHand(class_1675.method_18812(this.field_6593, Items.field_8399));
					this.stage = CrossbowAttackGoal.Stage.field_16530;
					this.field_6593.setCharging(true);
				}
			} else if (this.stage == CrossbowAttackGoal.Stage.field_16530) {
				if (!this.field_6593.isUsingItem()) {
					this.stage = CrossbowAttackGoal.Stage.field_16534;
				}

				int i = this.field_6593.method_6048();
				ItemStack itemStack = this.field_6593.method_6030();
				if (i >= CrossbowItem.method_7775(itemStack)) {
					this.field_6593.method_6075();
					this.stage = CrossbowAttackGoal.Stage.field_16532;
					this.field_16529 = 20 + this.field_6593.getRand().nextInt(20);
					this.field_6593.setCharging(false);
				}
			} else if (this.stage == CrossbowAttackGoal.Stage.field_16532) {
				this.field_16529--;
				if (this.field_16529 == 0) {
					this.stage = CrossbowAttackGoal.Stage.field_16533;
				}
			} else if (this.stage == CrossbowAttackGoal.Stage.field_16533 && bl) {
				this.field_6593.attack(livingEntity, 1.0F);
				ItemStack itemStack2 = this.field_6593.method_5998(class_1675.method_18812(this.field_6593, Items.field_8399));
				CrossbowItem.method_7782(itemStack2, false);
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

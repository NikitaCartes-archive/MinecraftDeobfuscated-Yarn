package net.minecraft.entity.ai.goal;

import net.minecraft.class_3745;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttacker;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

public class CrossbowAttackGoal<T extends HostileEntity & RangedAttacker & class_3745> extends Goal {
	private final T entity;
	private CrossbowAttackGoal.class_3744 field_16528 = CrossbowAttackGoal.class_3744.field_16534;
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
		return this.entity.getTarget() != null && this.method_6310();
	}

	private boolean method_6310() {
		return !this.entity.getMainHandStack().isEmpty() && this.entity.getMainHandStack().getItem() == Items.field_8399;
	}

	@Override
	public boolean shouldContinue() {
		return (this.canStart() || !this.entity.getNavigation().method_6357()) && this.method_6310();
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
				this.entity.getNavigation().startMovingTo(livingEntity, this.method_16352() ? this.field_6590 : this.field_6590 * 0.5);
			} else {
				this.entity.getNavigation().method_6340();
			}

			this.entity.getLookControl().lookAt(livingEntity, 30.0F, 30.0F);
			if (this.field_16528 == CrossbowAttackGoal.class_3744.field_16534) {
				if (!bl3) {
					this.entity.setCurrentHand(Hand.MAIN);
					this.field_16528 = CrossbowAttackGoal.class_3744.field_16530;
					this.entity.method_7110(true);
				}
			} else if (this.field_16528 == CrossbowAttackGoal.class_3744.field_16530) {
				int i = this.entity.method_6048();
				if (i >= CrossbowItem.method_7775(this.entity.getMainHandStack())) {
					CrossbowItem.setCharged(this.entity.getMainHandStack(), true);
					this.entity.method_6075();
					this.field_16528 = CrossbowAttackGoal.class_3744.field_16532;
					this.field_16529 = 20 + this.entity.getRand().nextInt(20);
					this.entity.method_7110(false);
				}
			} else if (this.field_16528 == CrossbowAttackGoal.class_3744.field_16532) {
				this.field_16529--;
				if (this.field_16529 == 0) {
					this.field_16528 = CrossbowAttackGoal.class_3744.field_16533;
				}
			} else if (this.field_16528 == CrossbowAttackGoal.class_3744.field_16533 && bl) {
				this.entity.attack(livingEntity, 1.0F);
				CrossbowItem.setCharged(this.entity.getMainHandStack(), false);
				this.field_16528 = CrossbowAttackGoal.class_3744.field_16534;
			}
		}
	}

	private boolean method_16352() {
		return this.field_16528 == CrossbowAttackGoal.class_3744.field_16534;
	}

	static enum class_3744 {
		field_16534,
		field_16530,
		field_16532,
		field_16533;
	}
}

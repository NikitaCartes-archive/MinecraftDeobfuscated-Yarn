package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ProjectileUtil;
import net.minecraft.entity.ai.RangedAttacker;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.Items;

public class BowAttackGoal<T extends HostileEntity & RangedAttacker> extends Goal {
	private final T field_6576;
	private final double field_6569;
	private int field_6575;
	private final float field_6570;
	private int field_6574 = -1;
	private int field_6572;
	private boolean field_6573;
	private boolean field_6571;
	private int field_6568 = -1;

	public BowAttackGoal(T hostileEntity, double d, int i, float f) {
		this.field_6576 = hostileEntity;
		this.field_6569 = d;
		this.field_6575 = i;
		this.field_6570 = f * f;
		this.setControls(EnumSet.of(Goal.Control.field_18405, Goal.Control.field_18406));
	}

	public void setAttackInterval(int i) {
		this.field_6575 = i;
	}

	@Override
	public boolean canStart() {
		return this.field_6576.getTarget() == null ? false : this.isHoldingBow();
	}

	protected boolean isHoldingBow() {
		return this.field_6576.isHolding(Items.field_8102);
	}

	@Override
	public boolean shouldContinue() {
		return (this.canStart() || !this.field_6576.getNavigation().isIdle()) && this.isHoldingBow();
	}

	@Override
	public void start() {
		super.start();
		this.field_6576.setAttacking(true);
	}

	@Override
	public void stop() {
		super.stop();
		this.field_6576.setAttacking(false);
		this.field_6572 = 0;
		this.field_6574 = -1;
		this.field_6576.clearActiveItem();
	}

	@Override
	public void tick() {
		LivingEntity livingEntity = this.field_6576.getTarget();
		if (livingEntity != null) {
			double d = this.field_6576.squaredDistanceTo(livingEntity.x, livingEntity.getBoundingBox().minY, livingEntity.z);
			boolean bl = this.field_6576.getVisibilityCache().canSee(livingEntity);
			boolean bl2 = this.field_6572 > 0;
			if (bl != bl2) {
				this.field_6572 = 0;
			}

			if (bl) {
				this.field_6572++;
			} else {
				this.field_6572--;
			}

			if (!(d > (double)this.field_6570) && this.field_6572 >= 20) {
				this.field_6576.getNavigation().stop();
				this.field_6568++;
			} else {
				this.field_6576.getNavigation().startMovingTo(livingEntity, this.field_6569);
				this.field_6568 = -1;
			}

			if (this.field_6568 >= 20) {
				if ((double)this.field_6576.getRand().nextFloat() < 0.3) {
					this.field_6573 = !this.field_6573;
				}

				if ((double)this.field_6576.getRand().nextFloat() < 0.3) {
					this.field_6571 = !this.field_6571;
				}

				this.field_6568 = 0;
			}

			if (this.field_6568 > -1) {
				if (d > (double)(this.field_6570 * 0.75F)) {
					this.field_6571 = false;
				} else if (d < (double)(this.field_6570 * 0.25F)) {
					this.field_6571 = true;
				}

				this.field_6576.getMoveControl().method_6243(this.field_6571 ? -0.5F : 0.5F, this.field_6573 ? 0.5F : -0.5F);
				this.field_6576.lookAtEntity(livingEntity, 30.0F, 30.0F);
			} else {
				this.field_6576.getLookControl().lookAt(livingEntity, 30.0F, 30.0F);
			}

			if (this.field_6576.isUsingItem()) {
				if (!bl && this.field_6572 < -60) {
					this.field_6576.clearActiveItem();
				} else if (bl) {
					int i = this.field_6576.getItemUseTime();
					if (i >= 20) {
						this.field_6576.clearActiveItem();
						this.field_6576.attack(livingEntity, BowItem.method_7722(i));
						this.field_6574 = this.field_6575;
					}
				}
			} else if (--this.field_6574 <= 0 && this.field_6572 >= -60) {
				this.field_6576.setCurrentHand(ProjectileUtil.getHandPossiblyHolding(this.field_6576, Items.field_8102));
			}
		}
	}
}

package net.minecraft;

import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.TameableEntity;

public class class_1386 extends Goal {
	private final TameableEntity field_6597;
	private boolean field_6598;

	public class_1386(TameableEntity tameableEntity) {
		this.field_6597 = tameableEntity;
		this.setControlBits(EnumSet.of(Goal.class_4134.field_18407, Goal.class_4134.field_18405));
	}

	@Override
	public boolean canStart() {
		if (!this.field_6597.isTamed()) {
			return false;
		} else if (this.field_6597.isInsideWaterOrBubbleColumn()) {
			return false;
		} else if (!this.field_6597.onGround) {
			return false;
		} else {
			LivingEntity livingEntity = this.field_6597.getOwner();
			if (livingEntity == null) {
				return true;
			} else {
				return this.field_6597.squaredDistanceTo(livingEntity) < 144.0 && livingEntity.getAttacker() != null ? false : this.field_6598;
			}
		}
	}

	@Override
	public void start() {
		this.field_6597.method_5942().stop();
		this.field_6597.setSitting(true);
	}

	@Override
	public void onRemove() {
		this.field_6597.setSitting(false);
	}

	public void method_6311(boolean bl) {
		this.field_6598 = bl;
	}
}

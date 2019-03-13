package net.minecraft;

import java.util.EnumSet;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.entity.player.PlayerEntity;

public class class_1390 extends Goal {
	private final AbstractTraderEntity field_6610;

	public class_1390(AbstractTraderEntity abstractTraderEntity) {
		this.field_6610 = abstractTraderEntity;
		this.setControlBits(EnumSet.of(Goal.class_4134.field_18407, Goal.class_4134.field_18405));
	}

	@Override
	public boolean canStart() {
		if (!this.field_6610.isValid()) {
			return false;
		} else if (this.field_6610.isInsideWater()) {
			return false;
		} else if (!this.field_6610.onGround) {
			return false;
		} else if (this.field_6610.velocityModified) {
			return false;
		} else {
			PlayerEntity playerEntity = this.field_6610.getCurrentCustomer();
			if (playerEntity == null) {
				return false;
			} else {
				return this.field_6610.squaredDistanceTo(playerEntity) > 16.0 ? false : playerEntity.field_7512 != null;
			}
		}
	}

	@Override
	public void start() {
		this.field_6610.method_5942().stop();
	}

	@Override
	public void onRemove() {
		this.field_6610.setCurrentCustomer(null);
	}
}

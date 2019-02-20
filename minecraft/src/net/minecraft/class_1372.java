package net.minecraft;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;

public class class_1372 extends Goal {
	private static final class_4051 field_18089 = new class_4051().method_18418(6.0).method_18421().method_18417();
	private final IronGolemEntity field_6542;
	private VillagerEntity field_6544;
	private int field_6543;

	public class_1372(IronGolemEntity ironGolemEntity) {
		this.field_6542 = ironGolemEntity;
		this.setControlBits(3);
	}

	@Override
	public boolean canStart() {
		if (!this.field_6542.world.isDaylight()) {
			return false;
		} else if (this.field_6542.getRand().nextInt(8000) != 0) {
			return false;
		} else {
			this.field_6544 = this.field_6542
				.world
				.method_18465(
					VillagerEntity.class,
					field_18089,
					this.field_6542,
					this.field_6542.x,
					this.field_6542.y,
					this.field_6542.z,
					this.field_6542.getBoundingBox().expand(6.0, 2.0, 6.0)
				);
			return this.field_6544 != null;
		}
	}

	@Override
	public boolean shouldContinue() {
		return this.field_6543 > 0;
	}

	@Override
	public void start() {
		this.field_6543 = 400;
		this.field_6542.method_6497(true);
	}

	@Override
	public void onRemove() {
		this.field_6542.method_6497(false);
		this.field_6544 = null;
	}

	@Override
	public void tick() {
		this.field_6542.getLookControl().lookAt(this.field_6544, 30.0F, 30.0F);
		this.field_6543--;
	}
}

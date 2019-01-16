package net.minecraft;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;

public class class_1376 extends Goal {
	private final MobEntity field_6556;
	private double field_6554;
	private double field_6553;
	private int field_6555;

	public class_1376(MobEntity mobEntity) {
		this.field_6556 = mobEntity;
		this.setControlBits(3);
	}

	@Override
	public boolean canStart() {
		return this.field_6556.getRand().nextFloat() < 0.02F;
	}

	@Override
	public boolean shouldContinue() {
		return this.field_6555 >= 0;
	}

	@Override
	public void start() {
		double d = (Math.PI * 2) * this.field_6556.getRand().nextDouble();
		this.field_6554 = Math.cos(d);
		this.field_6553 = Math.sin(d);
		this.field_6555 = 20 + this.field_6556.getRand().nextInt(20);
	}

	@Override
	public void tick() {
		this.field_6555--;
		this.field_6556
			.getLookControl()
			.lookAt(
				this.field_6556.x + this.field_6554,
				this.field_6556.y + (double)this.field_6556.getEyeHeight(),
				this.field_6556.z + this.field_6553,
				(float)this.field_6556.method_5986(),
				(float)this.field_6556.method_5978()
			);
	}
}

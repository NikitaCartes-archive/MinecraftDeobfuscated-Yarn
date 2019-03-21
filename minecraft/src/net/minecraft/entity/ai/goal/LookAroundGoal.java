package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.mob.MobEntity;

public class LookAroundGoal extends Goal {
	private final MobEntity owner;
	private double deltaX;
	private double deltaZ;
	private int lookTime;

	public LookAroundGoal(MobEntity mobEntity) {
		this.owner = mobEntity;
		this.setControlBits(EnumSet.of(Goal.class_4134.field_18405, Goal.class_4134.field_18406));
	}

	@Override
	public boolean canStart() {
		return this.owner.getRand().nextFloat() < 0.02F;
	}

	@Override
	public boolean shouldContinue() {
		return this.lookTime >= 0;
	}

	@Override
	public void start() {
		double d = (Math.PI * 2) * this.owner.getRand().nextDouble();
		this.deltaX = Math.cos(d);
		this.deltaZ = Math.sin(d);
		this.lookTime = 20 + this.owner.getRand().nextInt(20);
	}

	@Override
	public void tick() {
		this.lookTime--;
		this.owner
			.getLookControl()
			.lookAt(
				this.owner.x + this.deltaX,
				this.owner.y + (double)this.owner.getStandingEyeHeight(),
				this.owner.z + this.deltaZ,
				(float)this.owner.method_5986(),
				(float)this.owner.method_5978()
			);
	}
}

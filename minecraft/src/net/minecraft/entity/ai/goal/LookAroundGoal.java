package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.mob.MobEntity;

public class LookAroundGoal extends Goal {
	private final MobEntity mob;
	private double deltaX;
	private double deltaZ;
	private int lookTime;

	public LookAroundGoal(MobEntity mobEntity) {
		this.mob = mobEntity;
		this.setControls(EnumSet.of(Goal.Control.field_18405, Goal.Control.field_18406));
	}

	@Override
	public boolean canStart() {
		return this.mob.getRand().nextFloat() < 0.02F;
	}

	@Override
	public boolean shouldContinue() {
		return this.lookTime >= 0;
	}

	@Override
	public void start() {
		double d = (Math.PI * 2) * this.mob.getRand().nextDouble();
		this.deltaX = Math.cos(d);
		this.deltaZ = Math.sin(d);
		this.lookTime = 20 + this.mob.getRand().nextInt(20);
	}

	@Override
	public void tick() {
		this.lookTime--;
		this.mob.getLookControl().lookAt(this.mob.x + this.deltaX, this.mob.y + (double)this.mob.getStandingEyeHeight(), this.mob.z + this.deltaZ);
	}
}

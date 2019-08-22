package net.minecraft.entity.ai.goal;

import net.minecraft.entity.mob.MobEntity;

public class LongDoorInteractGoal extends DoorInteractGoal {
	private final boolean field_19004;
	private int ticksLeft;

	public LongDoorInteractGoal(MobEntity mobEntity, boolean bl) {
		super(mobEntity);
		this.mob = mobEntity;
		this.field_19004 = bl;
	}

	@Override
	public boolean shouldContinue() {
		return this.field_19004 && this.ticksLeft > 0 && super.shouldContinue();
	}

	@Override
	public void start() {
		this.ticksLeft = 20;
		this.setDoorOpen(true);
	}

	@Override
	public void stop() {
		this.setDoorOpen(false);
	}

	@Override
	public void tick() {
		this.ticksLeft--;
		super.tick();
	}
}

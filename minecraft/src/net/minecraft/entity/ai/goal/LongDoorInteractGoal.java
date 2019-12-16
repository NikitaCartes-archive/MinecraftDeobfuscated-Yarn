package net.minecraft.entity.ai.goal;

import net.minecraft.entity.mob.MobEntity;

public class LongDoorInteractGoal extends DoorInteractGoal {
	private final boolean delayedClose;
	private int ticksLeft;

	public LongDoorInteractGoal(MobEntity mob, boolean delayedClose) {
		super(mob);
		this.mob = mob;
		this.delayedClose = delayedClose;
	}

	@Override
	public boolean shouldContinue() {
		return this.delayedClose && this.ticksLeft > 0 && super.shouldContinue();
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

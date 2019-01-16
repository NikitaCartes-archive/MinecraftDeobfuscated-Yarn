package net.minecraft.entity.ai.goal;

import net.minecraft.entity.mob.MobEntity;

public class OpenDoorGoal extends DoorInteractGoal {
	private final boolean closeDoor;
	private int timer;

	public OpenDoorGoal(MobEntity mobEntity, boolean bl) {
		super(mobEntity);
		this.owner = mobEntity;
		this.closeDoor = bl;
	}

	@Override
	public boolean shouldContinue() {
		return this.closeDoor && this.timer > 0 && super.shouldContinue();
	}

	@Override
	public void start() {
		this.timer = 20;
		this.method_6255(true);
	}

	@Override
	public void onRemove() {
		this.method_6255(false);
	}

	@Override
	public void tick() {
		this.timer--;
		super.tick();
	}
}

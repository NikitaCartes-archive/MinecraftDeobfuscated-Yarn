package net.minecraft.entity.ai.goal;

import net.minecraft.entity.mob.ZombieEntity;

public class ZombieAttackGoal extends MeleeAttackGoal {
	private final ZombieEntity zombie;
	private int ticks;

	public ZombieAttackGoal(ZombieEntity zombie, double speed, boolean pauseWhenMobIdle) {
		super(zombie, speed, pauseWhenMobIdle);
		this.zombie = zombie;
	}

	@Override
	public void start() {
		super.start();
		this.ticks = 0;
	}

	@Override
	public void stop() {
		super.stop();
		this.zombie.setAttacking(false);
	}

	@Override
	public void tick() {
		super.tick();
		this.ticks++;
		if (this.ticks >= 5 && this.getCooldown() < this.getMaxCooldown() / 2) {
			this.zombie.setAttacking(true);
		} else {
			this.zombie.setAttacking(false);
		}
	}
}

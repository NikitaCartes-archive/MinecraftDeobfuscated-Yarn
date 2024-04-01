package net.minecraft.entity.ai.goal;

import net.minecraft.entity.mob.PathAwareEntity;

public class ZombieAttackGoal<T extends PathAwareEntity> extends MeleeAttackGoal {
	private final T zombie;
	private int ticks;

	public ZombieAttackGoal(T pathAwareEntity, double d, boolean bl) {
		super(pathAwareEntity, d, bl);
		this.zombie = pathAwareEntity;
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

package net.minecraft.entity.ai.goal;

import net.minecraft.entity.mob.ZombieEntity;

public class ZombieAttackGoal extends MeleeAttackGoal {
	private final ZombieEntity zombie;
	private int field_6627;

	public ZombieAttackGoal(ZombieEntity zombieEntity, double d, boolean bl) {
		super(zombieEntity, d, bl);
		this.zombie = zombieEntity;
	}

	@Override
	public void start() {
		super.start();
		this.field_6627 = 0;
	}

	@Override
	public void stop() {
		super.stop();
		this.zombie.setAttacking(false);
	}

	@Override
	public void tick() {
		super.tick();
		this.field_6627++;
		if (this.field_6627 >= 5 && this.ticksUntilAttack < 10) {
			this.zombie.setAttacking(true);
		} else {
			this.zombie.setAttacking(false);
		}
	}
}

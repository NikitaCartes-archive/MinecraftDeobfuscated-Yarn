package net.minecraft.entity.ai.goal;

import net.minecraft.entity.mob.ZombieEntity;

public class ZombieRaiseArmsGoal extends MeleeAttackGoal {
	private final ZombieEntity zombie;
	private int field_6627;

	public ZombieRaiseArmsGoal(ZombieEntity zombieEntity, double d, boolean bl) {
		super(zombieEntity, d, bl);
		this.zombie = zombieEntity;
	}

	@Override
	public void start() {
		super.start();
		this.field_6627 = 0;
	}

	@Override
	public void onRemove() {
		super.onRemove();
		this.zombie.method_19540(false);
	}

	@Override
	public void tick() {
		super.tick();
		this.field_6627++;
		if (this.field_6627 >= 5 && this.field_6505 < 10) {
			this.zombie.method_19540(true);
		} else {
			this.zombie.method_19540(false);
		}
	}
}

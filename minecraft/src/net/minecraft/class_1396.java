package net.minecraft;

import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.mob.ZombieEntity;

public class class_1396 extends MeleeAttackGoal {
	private final ZombieEntity field_6628;
	private int field_6627;

	public class_1396(ZombieEntity zombieEntity, double d, boolean bl) {
		super(zombieEntity, d, bl);
		this.field_6628 = zombieEntity;
	}

	@Override
	public void start() {
		super.start();
		this.field_6627 = 0;
	}

	@Override
	public void onRemove() {
		super.onRemove();
		this.field_6628.setArmsRaised(false);
	}

	@Override
	public void tick() {
		super.tick();
		this.field_6627++;
		if (this.field_6627 >= 5 && this.field_6505 < 10) {
			this.field_6628.setArmsRaised(true);
		} else {
			this.field_6628.setArmsRaised(false);
		}
	}
}

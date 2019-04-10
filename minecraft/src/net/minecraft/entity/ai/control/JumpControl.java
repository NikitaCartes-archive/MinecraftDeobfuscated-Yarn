package net.minecraft.entity.ai.control;

import net.minecraft.entity.mob.MobEntity;

public class JumpControl {
	private final MobEntity entity;
	protected boolean active;

	public JumpControl(MobEntity mobEntity) {
		this.entity = mobEntity;
	}

	public void setActive() {
		this.active = true;
	}

	public void tick() {
		this.entity.setJumping(this.active);
		this.active = false;
	}
}

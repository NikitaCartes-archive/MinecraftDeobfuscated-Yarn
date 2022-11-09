package net.minecraft.entity;

import net.minecraft.entity.player.PlayerEntity;

public interface JumpingMount extends Mount {
	void setJumpStrength(int strength);

	boolean canJump(PlayerEntity player);

	void startJumping(int height);

	void stopJumping();

	default int getJumpCooldown() {
		return 0;
	}
}

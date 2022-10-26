package net.minecraft.entity;

public interface JumpingMount extends Mount {
	void setJumpStrength(int strength);

	boolean canJump();

	void startJumping(int height);

	void stopJumping();

	default int getJumpCooldown() {
		return 0;
	}
}

package net.minecraft.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public interface JumpingMount {
	@Environment(EnvType.CLIENT)
	void setJumpStrength(int strength);

	boolean canJump();

	void startJumping(int height);

	void stopJumping();
}

package net.minecraft.client.input;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.GameOptions;
import net.minecraft.util.PlayerInput;

@Environment(EnvType.CLIENT)
public class KeyboardInput extends Input {
	private final GameOptions settings;

	public KeyboardInput(GameOptions settings) {
		this.settings = settings;
	}

	private static float getMovementMultiplier(boolean positive, boolean negative) {
		if (positive == negative) {
			return 0.0F;
		} else {
			return positive ? 1.0F : -1.0F;
		}
	}

	@Override
	public void tick(boolean slowDown, float slowDownFactor) {
		this.playerInput = new PlayerInput(
			this.settings.forwardKey.isPressed(),
			this.settings.backKey.isPressed(),
			this.settings.leftKey.isPressed(),
			this.settings.rightKey.isPressed(),
			this.settings.jumpKey.isPressed(),
			this.settings.sneakKey.isPressed(),
			this.settings.sprintKey.isPressed()
		);
		this.movementForward = getMovementMultiplier(this.playerInput.forward(), this.playerInput.backward());
		this.movementSideways = getMovementMultiplier(this.playerInput.left(), this.playerInput.right());
		if (slowDown) {
			this.movementSideways *= slowDownFactor;
			this.movementForward *= slowDownFactor;
		}
	}
}

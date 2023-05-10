package net.minecraft.client.input;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.GameOptions;

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
		this.pressingForward = this.settings.forwardKey.isPressed();
		this.pressingBack = this.settings.backKey.isPressed();
		this.pressingLeft = this.settings.leftKey.isPressed();
		this.pressingRight = this.settings.rightKey.isPressed();
		this.movementForward = getMovementMultiplier(this.pressingForward, this.pressingBack);
		this.movementSideways = getMovementMultiplier(this.pressingLeft, this.pressingRight);
		this.jumping = this.settings.jumpKey.isPressed();
		this.sneaking = this.settings.sneakKey.isPressed();
		if (slowDown) {
			this.movementSideways *= slowDownFactor;
			this.movementForward *= slowDownFactor;
		}
	}
}

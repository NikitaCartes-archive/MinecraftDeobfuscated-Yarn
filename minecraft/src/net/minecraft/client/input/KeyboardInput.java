package net.minecraft.client.input;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.GameOptions;

@Environment(EnvType.CLIENT)
public class KeyboardInput extends Input {
	private final GameOptions settings;
	private static final double field_32670 = 0.3;

	public KeyboardInput(GameOptions settings) {
		this.settings = settings;
	}

	@Override
	public void tick(boolean slowDown) {
		this.pressingForward = this.settings.keyForward.isPressed();
		this.pressingBack = this.settings.keyBack.isPressed();
		this.pressingLeft = this.settings.keyLeft.isPressed();
		this.pressingRight = this.settings.keyRight.isPressed();
		this.movementForward = this.pressingForward == this.pressingBack ? 0.0F : (this.pressingForward ? 1.0F : -1.0F);
		this.movementSideways = this.pressingLeft == this.pressingRight ? 0.0F : (this.pressingLeft ? 1.0F : -1.0F);
		this.jumping = this.settings.keyJump.isPressed();
		this.sneaking = this.settings.keySneak.isPressed();
		if (slowDown) {
			this.movementSideways = (float)((double)this.movementSideways * 0.3);
			this.movementForward = (float)((double)this.movementForward * 0.3);
		}
	}
}

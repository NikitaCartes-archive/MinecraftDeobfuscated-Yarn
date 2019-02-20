package net.minecraft.client.input;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.options.GameOptions;

@Environment(EnvType.CLIENT)
public class KeyboardInput extends Input {
	private final GameOptions settings;

	public KeyboardInput(GameOptions gameOptions) {
		this.settings = gameOptions;
	}

	@Override
	public void tick() {
		this.movementSideways = 0.0F;
		this.movementForward = 0.0F;
		if (this.settings.keyForward.isPressed()) {
			this.movementForward++;
			this.pressingForward = true;
		} else {
			this.pressingForward = false;
		}

		if (this.settings.keyBack.isPressed()) {
			this.movementForward--;
			this.pressingBack = true;
		} else {
			this.pressingBack = false;
		}

		if (this.settings.keyLeft.isPressed()) {
			this.movementSideways++;
			this.pressingLeft = true;
		} else {
			this.pressingLeft = false;
		}

		if (this.settings.keyRight.isPressed()) {
			this.movementSideways--;
			this.pressingRight = true;
		} else {
			this.pressingRight = false;
		}

		this.jumping = this.settings.keyJump.isPressed();
		this.sneaking = this.settings.keySneak.isPressed();
		if (this.sneaking) {
			this.movementSideways = (float)((double)this.movementSideways * 0.3);
			this.movementForward = (float)((double)this.movementForward * 0.3);
		}
	}
}

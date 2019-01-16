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
		this.field_3907 = 0.0F;
		this.field_3905 = 0.0F;
		if (this.settings.keyForward.isPressed()) {
			this.field_3905++;
			this.forward = true;
		} else {
			this.forward = false;
		}

		if (this.settings.keyBack.isPressed()) {
			this.field_3905--;
			this.back = true;
		} else {
			this.back = false;
		}

		if (this.settings.keyLeft.isPressed()) {
			this.field_3907++;
			this.left = true;
		} else {
			this.left = false;
		}

		if (this.settings.keyRight.isPressed()) {
			this.field_3907--;
			this.right = true;
		} else {
			this.right = false;
		}

		this.jumping = this.settings.keyJump.isPressed();
		this.sneaking = this.settings.keySneak.isPressed();
		if (this.sneaking) {
			this.field_3907 = (float)((double)this.field_3907 * 0.3);
			this.field_3905 = (float)((double)this.field_3905 * 0.3);
		}
	}
}

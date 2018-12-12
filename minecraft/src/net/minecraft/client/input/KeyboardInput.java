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
		if (this.settings.keyForward.method_1434()) {
			this.field_3905++;
			this.forward = true;
		} else {
			this.forward = false;
		}

		if (this.settings.keyBack.method_1434()) {
			this.field_3905--;
			this.back = true;
		} else {
			this.back = false;
		}

		if (this.settings.keyLeft.method_1434()) {
			this.field_3907++;
			this.left = true;
		} else {
			this.left = false;
		}

		if (this.settings.keyRight.method_1434()) {
			this.field_3907--;
			this.right = true;
		} else {
			this.right = false;
		}

		this.jumping = this.settings.keyJump.method_1434();
		this.sneaking = this.settings.keySneak.method_1434();
		if (this.sneaking) {
			this.field_3907 = (float)((double)this.field_3907 * 0.3);
			this.field_3905 = (float)((double)this.field_3905 * 0.3);
		}
	}
}

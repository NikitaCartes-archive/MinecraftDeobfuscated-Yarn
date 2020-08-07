package net.minecraft.client.toast;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public interface Toast {
	Identifier TOASTS_TEX = new Identifier("textures/gui/toasts.png");
	Object TYPE = new Object();

	Toast.Visibility draw(MatrixStack matrices, ToastManager manager, long startTime);

	default Object getType() {
		return TYPE;
	}

	default int getWidth() {
		return 160;
	}

	default int getHeight() {
		return 32;
	}

	@Environment(EnvType.CLIENT)
	public static enum Visibility {
		field_2210(SoundEvents.field_14561),
		field_2209(SoundEvents.field_14641);

		private final SoundEvent sound;

		private Visibility(SoundEvent sound) {
			this.sound = sound;
		}

		public void playSound(SoundManager soundManager) {
			soundManager.play(PositionedSoundInstance.master(this.sound, 1.0F, 1.0F));
		}
	}
}

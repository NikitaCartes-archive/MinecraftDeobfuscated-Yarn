package net.minecraft.client.toast;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.audio.PositionedSoundInstance;
import net.minecraft.client.audio.SoundLoader;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public interface Toast {
	Identifier TOASTS_TEX = new Identifier("textures/gui/toasts.png");
	Object field_2208 = new Object();

	Toast.class_369 draw(ToastManager toastManager, long l);

	default Object method_1987() {
		return field_2208;
	}

	@Environment(EnvType.CLIENT)
	public static enum class_369 {
		field_2210(SoundEvents.field_14561),
		field_2209(SoundEvents.field_14641);

		private final SoundEvent sound;

		private class_369(SoundEvent soundEvent) {
			this.sound = soundEvent;
		}

		public void play(SoundLoader soundLoader) {
			soundLoader.play(PositionedSoundInstance.master(this.sound, 1.0F, 1.0F));
		}
	}
}

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
	Identifier field_2207 = new Identifier("textures/gui/toasts.png");
	Object field_2208 = new Object();

	Toast.Visibility method_1986(ToastManager toastManager, long l);

	default Object method_1987() {
		return field_2208;
	}

	@Environment(EnvType.CLIENT)
	public static enum Visibility {
		field_2210(SoundEvents.field_14561),
		field_2209(SoundEvents.field_14641);

		private final SoundEvent field_2211;

		private Visibility(SoundEvent soundEvent) {
			this.field_2211 = soundEvent;
		}

		public void method_1988(SoundLoader soundLoader) {
			soundLoader.play(PositionedSoundInstance.method_4757(this.field_2211, 1.0F, 1.0F));
		}
	}
}

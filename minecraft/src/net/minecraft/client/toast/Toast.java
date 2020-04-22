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
	Object field_2208 = new Object();

	Toast.Visibility draw(MatrixStack matrixStack, ToastManager toastManager, long l);

	default Object getType() {
		return field_2208;
	}

	@Environment(EnvType.CLIENT)
	public static enum Visibility {
		SHOW(SoundEvents.UI_TOAST_IN),
		HIDE(SoundEvents.UI_TOAST_OUT);

		private final SoundEvent sound;

		private Visibility(SoundEvent soundEvent) {
			this.sound = soundEvent;
		}

		public void playSound(SoundManager soundManager) {
			soundManager.play(PositionedSoundInstance.master(this.sound, 1.0F, 1.0F));
		}
	}
}

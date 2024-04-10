package net.minecraft.client.toast;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public interface Toast {
	Object TYPE = new Object();
	int BASE_HEIGHT = 32;

	Toast.Visibility draw(DrawContext context, ToastManager manager, long startTime);

	default Object getType() {
		return TYPE;
	}

	default int getWidth() {
		return 160;
	}

	default int getHeight() {
		return 32;
	}

	default int getRequiredSpaceCount() {
		return MathHelper.ceilDiv(this.getHeight(), 32);
	}

	@Environment(EnvType.CLIENT)
	public static enum Visibility {
		SHOW(SoundEvents.UI_TOAST_IN),
		HIDE(SoundEvents.UI_TOAST_OUT);

		private final SoundEvent sound;

		private Visibility(final SoundEvent sound) {
			this.sound = sound;
		}

		public void playSound(SoundManager soundManager) {
			soundManager.play(PositionedSoundInstance.master(this.sound, 1.0F, 1.0F));
		}
	}
}

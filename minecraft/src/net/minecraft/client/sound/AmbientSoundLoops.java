package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

@Environment(EnvType.CLIENT)
public class AmbientSoundLoops {
	@Environment(EnvType.CLIENT)
	public static class MusicLoop extends MovingSoundInstance {
		private final ClientPlayerEntity player;

		protected MusicLoop(ClientPlayerEntity player, SoundEvent soundEvent) {
			super(soundEvent, SoundCategory.AMBIENT, SoundInstance.createRandom());
			this.player = player;
			this.repeat = false;
			this.repeatDelay = 0;
			this.volume = 1.0F;
			this.relative = true;
		}

		@Override
		public void tick() {
			if (this.player.isRemoved() || !this.player.isSubmergedInWater()) {
				this.setDone();
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Underwater extends MovingSoundInstance {
		public static final int MAX_TRANSITION_TIMER = 40;
		private final ClientPlayerEntity player;
		private int transitionTimer;

		public Underwater(ClientPlayerEntity player) {
			super(SoundEvents.AMBIENT_UNDERWATER_LOOP, SoundCategory.AMBIENT, SoundInstance.createRandom());
			this.player = player;
			this.repeat = true;
			this.repeatDelay = 0;
			this.volume = 1.0F;
			this.relative = true;
		}

		@Override
		public void tick() {
			if (!this.player.isRemoved() && this.transitionTimer >= 0) {
				if (this.player.isSubmergedInWater()) {
					this.transitionTimer++;
				} else {
					this.transitionTimer -= 2;
				}

				this.transitionTimer = Math.min(this.transitionTimer, 40);
				this.volume = Math.max(0.0F, Math.min((float)this.transitionTimer / 40.0F, 1.0F));
			} else {
				this.setDone();
			}
		}
	}
}

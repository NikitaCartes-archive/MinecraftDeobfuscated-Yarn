package net.minecraft.client.audio;

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

		protected MusicLoop(ClientPlayerEntity clientPlayerEntity, SoundEvent soundEvent) {
			super(soundEvent, SoundCategory.field_15256);
			this.player = clientPlayerEntity;
			this.repeat = false;
			this.repeatDelay = 0;
			this.volume = 1.0F;
			this.field_18935 = true;
		}

		@Override
		public void tick() {
			if (!this.player.invalid && this.player.isInWater()) {
				this.x = (float)this.player.x;
				this.y = (float)this.player.y;
				this.z = (float)this.player.z;
			} else {
				this.done = true;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Underwater extends MovingSoundInstance {
		private final ClientPlayerEntity player;
		private int transitionTimer;

		public Underwater(ClientPlayerEntity clientPlayerEntity) {
			super(SoundEvents.field_14951, SoundCategory.field_15256);
			this.player = clientPlayerEntity;
			this.repeat = true;
			this.repeatDelay = 0;
			this.volume = 1.0F;
			this.field_18935 = true;
		}

		@Override
		public void tick() {
			if (!this.player.invalid && this.transitionTimer >= 0) {
				this.x = (float)this.player.x;
				this.y = (float)this.player.y;
				this.z = (float)this.player.z;
				if (this.player.isInWater()) {
					this.transitionTimer++;
				} else {
					this.transitionTimer -= 2;
				}

				this.transitionTimer = Math.min(this.transitionTimer, 40);
				this.volume = Math.max(0.0F, Math.min((float)this.transitionTimer / 40.0F, 1.0F));
			} else {
				this.done = true;
			}
		}
	}
}

package net.minecraft.client.audio;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class MusicTracker {
	private final Random random = new Random();
	private final MinecraftClient client;
	private SoundInstance current;
	private int timeUntilNextSong = 100;
	private boolean field_5573;

	public MusicTracker(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
	}

	public void method_18669() {
		MusicTracker.MusicType musicType = this.client.getMusicType();
		if (this.current != null) {
			if (!musicType.getSound().getId().equals(this.current.getId())) {
				this.client.getSoundLoader().stop(this.current);
				this.timeUntilNextSong = MathHelper.nextInt(this.random, 0, musicType.getMinDelay() / 2);
				this.field_5573 = false;
			}

			if (!this.field_5573 && !this.client.getSoundLoader().isPlaying(this.current)) {
				this.current = null;
				this.timeUntilNextSong = Math.min(MathHelper.nextInt(this.random, musicType.getMinDelay(), musicType.getMaxDelay()), this.timeUntilNextSong);
			} else if (this.client.getSoundLoader().isPlaying(this.current)) {
				this.field_5573 = false;
			}
		}

		this.timeUntilNextSong = Math.min(this.timeUntilNextSong, musicType.getMaxDelay());
		if (this.current == null && this.timeUntilNextSong-- <= 0) {
			this.play(musicType);
		}
	}

	public void play(MusicTracker.MusicType musicType) {
		this.current = PositionedSoundInstance.music(musicType.getSound());
		this.client.getSoundLoader().play(this.current);
		this.timeUntilNextSong = Integer.MAX_VALUE;
		this.field_5573 = true;
	}

	public void stop() {
		if (this.current != null) {
			this.client.getSoundLoader().stop(this.current);
			this.current = null;
			this.timeUntilNextSong = 0;
			this.field_5573 = false;
		}
	}

	public boolean isPlayingType(MusicTracker.MusicType musicType) {
		return this.current == null ? false : musicType.getSound().getId().equals(this.current.getId());
	}

	@Environment(EnvType.CLIENT)
	public static enum MusicType {
		field_5585(SoundEvents.field_15129, 20, 600),
		field_5586(SoundEvents.field_14681, 12000, 24000),
		field_5581(SoundEvents.field_14995, 1200, 3600),
		field_5578(SoundEvents.field_14755, 0, 0),
		field_5582(SoundEvents.field_14893, 1200, 3600),
		field_5580(SoundEvents.field_14837, 0, 0),
		field_5583(SoundEvents.field_14631, 6000, 24000),
		field_5576(SoundEvents.field_15198, 12000, 24000);

		private final SoundEvent sound;
		private final int minDelay;
		private final int maxDelay;

		private MusicType(SoundEvent soundEvent, int j, int k) {
			this.sound = soundEvent;
			this.minDelay = j;
			this.maxDelay = k;
		}

		public SoundEvent getSound() {
			return this.sound;
		}

		public int getMinDelay() {
			return this.minDelay;
		}

		public int getMaxDelay() {
			return this.maxDelay;
		}
	}
}

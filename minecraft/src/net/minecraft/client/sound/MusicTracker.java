package net.minecraft.client.sound;

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

	public MusicTracker(MinecraftClient client) {
		this.client = client;
	}

	public void tick() {
		MusicTracker.MusicType musicType = this.client.getMusicType();
		if (this.current != null) {
			if (!musicType.getSound().getId().equals(this.current.getId())) {
				this.client.getSoundManager().stop(this.current);
				this.timeUntilNextSong = MathHelper.nextInt(this.random, 0, musicType.getMinDelay() / 2);
			}

			if (!this.client.getSoundManager().isPlaying(this.current)) {
				this.current = null;
				this.timeUntilNextSong = Math.min(MathHelper.nextInt(this.random, musicType.getMinDelay(), musicType.getMaxDelay()), this.timeUntilNextSong);
			}
		}

		this.timeUntilNextSong = Math.min(this.timeUntilNextSong, musicType.getMaxDelay());
		if (this.current == null && this.timeUntilNextSong-- <= 0) {
			this.play(musicType);
		}
	}

	public void play(MusicTracker.MusicType musicType) {
		this.current = PositionedSoundInstance.music(musicType.getSound());
		this.client.getSoundManager().play(this.current);
		this.timeUntilNextSong = Integer.MAX_VALUE;
	}

	public void stop() {
		if (this.current != null) {
			this.client.getSoundManager().stop(this.current);
			this.current = null;
			this.timeUntilNextSong = 0;
		}
	}

	public boolean isPlayingType(MusicTracker.MusicType musicType) {
		return this.current == null ? false : musicType.getSound().getId().equals(this.current.getId());
	}

	@Environment(EnvType.CLIENT)
	public static enum MusicType {
		MENU(SoundEvents.MUSIC_MENU, 20, 600),
		GAME(SoundEvents.MUSIC_GAME, 12000, 24000),
		CREATIVE(SoundEvents.MUSIC_CREATIVE, 1200, 3600),
		CREDITS(SoundEvents.MUSIC_CREDITS, 0, 0),
		NETHER(SoundEvents.MUSIC_NETHER, 1200, 3600),
		END_BOSS(SoundEvents.MUSIC_DRAGON, 0, 0),
		END(SoundEvents.MUSIC_END, 6000, 24000),
		UNDER_WATER(SoundEvents.MUSIC_UNDER_WATER, 12000, 24000);

		private final SoundEvent sound;
		private final int minDelay;
		private final int maxDelay;

		private MusicType(SoundEvent soundEvent, int minDelay, int maxDelay) {
			this.sound = soundEvent;
			this.minDelay = minDelay;
			this.maxDelay = maxDelay;
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

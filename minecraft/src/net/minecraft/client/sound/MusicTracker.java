package net.minecraft.client.sound;

import java.util.Random;
import javax.annotation.Nullable;
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
	@Nullable
	private SoundInstance current;
	private int timeUntilNextSong = 100;

	public MusicTracker(MinecraftClient client) {
		this.client = client;
	}

	public void tick() {
		MusicTracker.MusicType musicType = this.client.getMusicType();
		if (this.current != null) {
			if (!musicType.getSound().getId().equals(this.current.getId()) && musicType.field_23944) {
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

	public void play(MusicTracker.MusicType type) {
		this.current = PositionedSoundInstance.music(type.getSound());
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

	public boolean isPlayingType(MusicTracker.MusicType type) {
		return this.current == null ? false : type.getSound().getId().equals(this.current.getId());
	}

	@Environment(EnvType.CLIENT)
	public static enum MusicType {
		MENU(SoundEvents.MUSIC_MENU, 20, 600),
		GAME(SoundEvents.MUSIC_GAME, 12000, 24000),
		CREATIVE(SoundEvents.MUSIC_CREATIVE, 1200, 3600),
		CREDITS(SoundEvents.MUSIC_CREDITS, 0, 0),
		BASALT_DELTAS(SoundEvents.MUSIC_NETHER_BASALT_DELTAS, 1200, 3600, false),
		NETHER_WASTES(SoundEvents.MUSIC_NETHER_NETHER_WASTES, 1200, 3600, false),
		SOUL_SAND_VALLEY(SoundEvents.MUSIC_NETHER_SOUL_SAND_VALLEY, 1200, 3600, false),
		CRIMSON_FOREST(SoundEvents.MUSIC_NETHER_CRIMSON_FOREST, 1200, 3600, false),
		WARPED_FOREST(SoundEvents.MUSIC_NETHER_WARPED_FOREST, 1200, 3600, false),
		END_BOSS(SoundEvents.MUSIC_DRAGON, 0, 0),
		END(SoundEvents.MUSIC_END, 6000, 24000),
		UNDER_WATER(SoundEvents.MUSIC_UNDER_WATER, 12000, 24000);

		private final SoundEvent sound;
		private final int minDelay;
		private final int maxDelay;
		private final boolean field_23944;

		private MusicType(SoundEvent soundEvent, int minDelay, int maxDelay) {
			this(soundEvent, minDelay, maxDelay, true);
		}

		private MusicType(SoundEvent soundEvent, int minDelay, int maxDelay, boolean bl) {
			this.sound = soundEvent;
			this.minDelay = minDelay;
			this.maxDelay = maxDelay;
			this.field_23944 = bl;
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

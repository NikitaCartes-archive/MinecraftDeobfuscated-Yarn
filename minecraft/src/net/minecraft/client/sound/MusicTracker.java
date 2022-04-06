package net.minecraft.client.sound;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.MusicSound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.AbstractRandom;

@Environment(EnvType.CLIENT)
public class MusicTracker {
	private static final int field_33019 = 100;
	private final AbstractRandom random = AbstractRandom.createAtomic();
	private final MinecraftClient client;
	@Nullable
	private SoundInstance current;
	private int timeUntilNextSong = 100;

	public MusicTracker(MinecraftClient client) {
		this.client = client;
	}

	public void tick() {
		MusicSound musicSound = this.client.getMusicType();
		if (this.current != null) {
			if (!musicSound.getSound().getId().equals(this.current.getId()) && musicSound.shouldReplaceCurrentMusic()) {
				this.client.getSoundManager().stop(this.current);
				this.timeUntilNextSong = MathHelper.nextInt(this.random, 0, musicSound.getMinDelay() / 2);
			}

			if (!this.client.getSoundManager().isPlaying(this.current)) {
				this.current = null;
				this.timeUntilNextSong = Math.min(this.timeUntilNextSong, MathHelper.nextInt(this.random, musicSound.getMinDelay(), musicSound.getMaxDelay()));
			}
		}

		this.timeUntilNextSong = Math.min(this.timeUntilNextSong, musicSound.getMaxDelay());
		if (this.current == null && this.timeUntilNextSong-- <= 0) {
			this.play(musicSound);
		}
	}

	public void play(MusicSound type) {
		this.current = PositionedSoundInstance.music(type.getSound());
		if (this.current.getSound() != SoundManager.MISSING_SOUND) {
			this.client.getSoundManager().play(this.current);
		}

		this.timeUntilNextSong = Integer.MAX_VALUE;
	}

	public void stop() {
		if (this.current != null) {
			this.client.getSoundManager().stop(this.current);
			this.current = null;
		}

		this.timeUntilNextSong += 100;
	}

	public boolean isPlayingType(MusicSound type) {
		return this.current == null ? false : type.getSound().getId().equals(this.current.getId());
	}
}

package net.minecraft.world.biome;

import java.util.Optional;
import java.util.OptionalInt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.sound.BiomeAdditionsSound;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvent;

public class BiomeEffects {
	private final int fogColor;
	private final int waterColor;
	private final int waterFogColor;
	private final Optional<BiomeParticleConfig> particleConfig;
	private final Optional<SoundEvent> loopSound;
	private final Optional<BiomeMoodSound> moodSound;
	private final Optional<BiomeAdditionsSound> additionsSound;
	private final Optional<MusicSound> music;

	private BiomeEffects(
		int fogColor,
		int waterColor,
		int waterFogCOlor,
		Optional<BiomeParticleConfig> particleConfig,
		Optional<SoundEvent> loopSound,
		Optional<BiomeMoodSound> moodSound,
		Optional<BiomeAdditionsSound> additionsSound,
		Optional<MusicSound> music
	) {
		this.fogColor = fogColor;
		this.waterColor = waterColor;
		this.waterFogColor = waterFogCOlor;
		this.particleConfig = particleConfig;
		this.loopSound = loopSound;
		this.moodSound = moodSound;
		this.additionsSound = additionsSound;
		this.music = music;
	}

	@Environment(EnvType.CLIENT)
	public int getFogColor() {
		return this.fogColor;
	}

	@Environment(EnvType.CLIENT)
	public int getWaterColor() {
		return this.waterColor;
	}

	@Environment(EnvType.CLIENT)
	public int getWaterFogColor() {
		return this.waterFogColor;
	}

	@Environment(EnvType.CLIENT)
	public Optional<BiomeParticleConfig> getParticleConfig() {
		return this.particleConfig;
	}

	/**
	 * Returns the loop sound.
	 * 
	 * <p>A loop sound is played continuously as an ambient sound whenever the
	 * player is in the biome with this effect.
	 */
	@Environment(EnvType.CLIENT)
	public Optional<SoundEvent> getLoopSound() {
		return this.loopSound;
	}

	/**
	 * Returns the mood sound.
	 * 
	 * <p>A mood sound is played once every 6000 to 17999 ticks as an ambient
	 * sound whenever the player is in the biome with this effect and near a
	 * position that has 0 sky light and less than 7 combined light.
	 * 
	 * <p>Overworld biomes have the regular cave sound as their mood sound,
	 * while three nether biomes in 20w10a have their dedicated mood sounds.
	 */
	@Environment(EnvType.CLIENT)
	public Optional<BiomeMoodSound> getMoodSound() {
		return this.moodSound;
	}

	/**
	 * Returns the additions sound.
	 * 
	 * <p>An additions sound is played at 1.1% chance every tick as an ambient
	 * sound whenever the player is in the biome with this effect.
	 */
	@Environment(EnvType.CLIENT)
	public Optional<BiomeAdditionsSound> getAdditionsSound() {
		return this.additionsSound;
	}

	@Environment(EnvType.CLIENT)
	public Optional<MusicSound> method_27345() {
		return this.music;
	}

	public static class Builder {
		private OptionalInt fogColor = OptionalInt.empty();
		private OptionalInt waterColor = OptionalInt.empty();
		private OptionalInt waterFogColor = OptionalInt.empty();
		private Optional<BiomeParticleConfig> particleConfig = Optional.empty();
		private Optional<SoundEvent> loopSound = Optional.empty();
		private Optional<BiomeMoodSound> moodSound = Optional.empty();
		private Optional<BiomeAdditionsSound> additionsSound = Optional.empty();
		private Optional<MusicSound> musicSound = Optional.empty();

		public BiomeEffects.Builder fogColor(int fogColor) {
			this.fogColor = OptionalInt.of(fogColor);
			return this;
		}

		public BiomeEffects.Builder waterColor(int waterColor) {
			this.waterColor = OptionalInt.of(waterColor);
			return this;
		}

		public BiomeEffects.Builder waterFogColor(int waterFogColor) {
			this.waterFogColor = OptionalInt.of(waterFogColor);
			return this;
		}

		public BiomeEffects.Builder particleConfig(BiomeParticleConfig particleConfig) {
			this.particleConfig = Optional.of(particleConfig);
			return this;
		}

		public BiomeEffects.Builder loopSound(SoundEvent sound) {
			this.loopSound = Optional.of(sound);
			return this;
		}

		public BiomeEffects.Builder moodSound(BiomeMoodSound moodSound) {
			this.moodSound = Optional.of(moodSound);
			return this;
		}

		public BiomeEffects.Builder additionsSound(BiomeAdditionsSound additionsSound) {
			this.additionsSound = Optional.of(additionsSound);
			return this;
		}

		public BiomeEffects.Builder music(MusicSound music) {
			this.musicSound = Optional.of(music);
			return this;
		}

		public BiomeEffects build() {
			return new BiomeEffects(
				this.fogColor.orElseThrow(() -> new IllegalStateException("Missing 'fog' color.")),
				this.waterColor.orElseThrow(() -> new IllegalStateException("Missing 'water' color.")),
				this.waterFogColor.orElseThrow(() -> new IllegalStateException("Missing 'water fog' color.")),
				this.particleConfig,
				this.loopSound,
				this.moodSound,
				this.additionsSound,
				this.musicSound
			);
		}
	}
}

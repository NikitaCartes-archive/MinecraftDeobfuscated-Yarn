package net.minecraft.sound;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * Represents an "additions sound" for a biome.
 */
public class BiomeAdditionsSound {
	public static final Codec<BiomeAdditionsSound> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					SoundEvent.CODEC.fieldOf("sound").forGetter(biomeAdditionsSound -> biomeAdditionsSound.sound),
					Codec.DOUBLE.fieldOf("tick_chance").forGetter(biomeAdditionsSound -> biomeAdditionsSound.chance)
				)
				.apply(instance, BiomeAdditionsSound::new)
	);
	private final SoundEvent sound;
	private final double chance;

	public BiomeAdditionsSound(SoundEvent sound, double chance) {
		this.sound = sound;
		this.chance = chance;
	}

	public SoundEvent getSound() {
		return this.sound;
	}

	/**
	 * Returns the chance of this addition sound to play at any tick.
	 */
	public double getChance() {
		return this.chance;
	}
}

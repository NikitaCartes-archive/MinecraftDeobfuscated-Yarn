package net.minecraft.sound;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.entry.RegistryEntry;

/**
 * Represents an "additions sound" for a biome.
 */
public class BiomeAdditionsSound {
	public static final Codec<BiomeAdditionsSound> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					SoundEvent.ENTRY_CODEC.fieldOf("sound").forGetter(sound -> sound.sound), Codec.DOUBLE.fieldOf("tick_chance").forGetter(sound -> sound.chance)
				)
				.apply(instance, BiomeAdditionsSound::new)
	);
	private final RegistryEntry<SoundEvent> sound;
	private final double chance;

	public BiomeAdditionsSound(RegistryEntry<SoundEvent> sound, double chance) {
		this.sound = sound;
		this.chance = chance;
	}

	public RegistryEntry<SoundEvent> getSound() {
		return this.sound;
	}

	/**
	 * Returns the chance of this addition sound to play at any tick.
	 */
	public double getChance() {
		return this.chance;
	}
}

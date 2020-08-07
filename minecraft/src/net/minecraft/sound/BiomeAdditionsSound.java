package net.minecraft.sound;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Represents an "additions sound" for a biome.
 */
public class BiomeAdditionsSound {
	public static final Codec<BiomeAdditionsSound> field_24673 = RecordCodecBuilder.create(
		instance -> instance.group(
					SoundEvent.field_24628.fieldOf("sound").forGetter(biomeAdditionsSound -> biomeAdditionsSound.event),
					Codec.DOUBLE.fieldOf("tick_chance").forGetter(biomeAdditionsSound -> biomeAdditionsSound.chance)
				)
				.apply(instance, BiomeAdditionsSound::new)
	);
	private SoundEvent event;
	private double chance;

	public BiomeAdditionsSound(SoundEvent event, double chance) {
		this.event = event;
		this.chance = chance;
	}

	@Environment(EnvType.CLIENT)
	public SoundEvent getEvent() {
		return this.event;
	}

	/**
	 * Returns the chance of this addition sound to play at any tick.
	 */
	@Environment(EnvType.CLIENT)
	public double getChance() {
		return this.chance;
	}
}

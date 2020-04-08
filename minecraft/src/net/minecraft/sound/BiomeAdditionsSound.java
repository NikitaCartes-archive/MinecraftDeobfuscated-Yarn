package net.minecraft.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Represents an "additions sound" for a biome.
 */
public class BiomeAdditionsSound {
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

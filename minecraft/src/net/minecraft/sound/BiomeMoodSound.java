package net.minecraft.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class BiomeMoodSound {
	public static final BiomeMoodSound CAVE = new BiomeMoodSound(SoundEvents.AMBIENT_CAVE, 6000, 8, 2.0);
	private SoundEvent event;
	private int cultivationTicks;
	private int spawnRange;
	private double extraDistance;

	public BiomeMoodSound(SoundEvent event, int cultivationTicks, int spawnRange, double d) {
		this.event = event;
		this.cultivationTicks = cultivationTicks;
		this.spawnRange = spawnRange;
		this.extraDistance = d;
	}

	@Environment(EnvType.CLIENT)
	public SoundEvent getEvent() {
		return this.event;
	}

	/**
	 * Returns the ticks it takes for entering the mood environment (a totally
	 * dark cave) to playing the mood sound, or the inverse of the per-tick.
	 */
	@Environment(EnvType.CLIENT)
	public int getCultivationTicks() {
		return this.cultivationTicks;
	}

	/**
	 * Returns the chebyshev distance from which the mood sound can play to
	 * the player.
	 */
	@Environment(EnvType.CLIENT)
	public int getSpawnRange() {
		return this.spawnRange;
	}

	/**
	 * Returns the extra distance of the sound from the player when the sound
	 * plays from the mood position.
	 * 
	 * <p>The sound is actually played at a position along the line on the
	 * three-dimensional vector from the player to the chosen mood position that
	 * is this distance to the mood position and this distance farther from the
	 * player.
	 */
	@Environment(EnvType.CLIENT)
	public double getExtraDistance() {
		return this.extraDistance;
	}
}

package net.minecraft.sound;

import net.minecraft.util.Identifier;

public class SoundEvent {
	private static final float DEFAULT_DISTANCE_TO_TRAVEL = 16.0F;
	private final Identifier id;
	private final float distanceToTravel;
	private final boolean staticDistance;

	static SoundEvent of(Identifier id) {
		return new SoundEvent(id, 16.0F, false);
	}

	static SoundEvent of(Identifier id, float distanceToTravel) {
		return new SoundEvent(id, distanceToTravel, true);
	}

	private SoundEvent(Identifier id, float distanceToTravel, boolean useStaticDistance) {
		this.id = id;
		this.distanceToTravel = distanceToTravel;
		this.staticDistance = useStaticDistance;
	}

	public Identifier getId() {
		return this.id;
	}

	public float getDistanceToTravel(float volume) {
		return this.staticDistance ? this.distanceToTravel : getDistanceToTravelForVolume(volume);
	}

	public static float getDistanceToTravelForVolume(float volume) {
		return volume > 1.0F ? 16.0F * volume : 16.0F;
	}
}

package net.minecraft.sound;

import com.mojang.serialization.Codec;
import net.minecraft.util.Identifier;

public class SoundEvent {
	public static final Codec<SoundEvent> CODEC = Identifier.CODEC.xmap(SoundEvent::new, soundEvent -> soundEvent.id);
	private final Identifier id;
	private final float distanceToTravel;
	private final boolean staticDistance;

	public SoundEvent(Identifier id) {
		this(id, 16.0F, false);
	}

	public SoundEvent(Identifier id, float distanceToTravel) {
		this(id, distanceToTravel, true);
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
		if (this.staticDistance) {
			return this.distanceToTravel;
		} else {
			return volume > 1.0F ? 16.0F * volume : 16.0F;
		}
	}
}

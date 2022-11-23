/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.sound;

import net.minecraft.util.Identifier;

public class SoundEvent {
    private static final float DEFAULT_DISTANCE_TO_TRAVEL = 16.0f;
    private final Identifier id;
    private final float distanceToTravel;
    private final boolean staticDistance;

    static SoundEvent of(Identifier id) {
        return new SoundEvent(id, 16.0f, false);
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
        if (this.staticDistance) {
            return this.distanceToTravel;
        }
        return SoundEvent.getDistanceToTravelForVolume(volume);
    }

    public static float getDistanceToTravelForVolume(float volume) {
        return volume > 1.0f ? 16.0f * volume : 16.0f;
    }
}


/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.mob;

import java.util.Arrays;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Util;

public enum Angriness {
    CALM(0, SoundEvents.ENTITY_WARDEN_AMBIENT),
    AGITATED(40, SoundEvents.ENTITY_WARDEN_AGITATED),
    ANGRY(80, SoundEvents.ENTITY_WARDEN_ANGRY);

    private static final Angriness[] VALUES;
    private final int threshold;
    private final SoundEvent sound;

    private Angriness(int threshold, SoundEvent sound) {
        this.threshold = threshold;
        this.sound = sound;
    }

    public int getThreshold() {
        return this.threshold;
    }

    public SoundEvent getSound() {
        return this.sound;
    }

    public static Angriness getForAnger(int anger) {
        for (Angriness angriness : VALUES) {
            if (anger < angriness.threshold) continue;
            return angriness;
        }
        return CALM;
    }

    static {
        VALUES = Util.make(Angriness.values(), values -> Arrays.sort(values, (a, b) -> Integer.compare(b.threshold, a.threshold)));
    }
}


/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.AbstractRandom;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public interface SoundInstance {
    public Identifier getId();

    @Nullable
    public WeightedSoundSet getSoundSet(SoundManager var1);

    public Sound getSound();

    public SoundCategory getCategory();

    public boolean isRepeatable();

    public boolean isRelative();

    public int getRepeatDelay();

    public float getVolume();

    public float getPitch();

    public double getX();

    public double getY();

    public double getZ();

    public AttenuationType getAttenuationType();

    default public boolean shouldAlwaysPlay() {
        return false;
    }

    default public boolean canPlay() {
        return true;
    }

    public static AbstractRandom createRandom() {
        return AbstractRandom.createAtomic();
    }

    @Environment(value=EnvType.CLIENT)
    public static enum AttenuationType {
        NONE,
        LINEAR;

    }
}


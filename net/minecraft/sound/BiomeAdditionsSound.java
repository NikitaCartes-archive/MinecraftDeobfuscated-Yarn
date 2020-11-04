/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.sound;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.sound.SoundEvent;

/**
 * Represents an "additions sound" for a biome.
 */
public class BiomeAdditionsSound {
    public static final Codec<BiomeAdditionsSound> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)SoundEvent.CODEC.fieldOf("sound")).forGetter(biomeAdditionsSound -> biomeAdditionsSound.sound), ((MapCodec)Codec.DOUBLE.fieldOf("tick_chance")).forGetter(biomeAdditionsSound -> biomeAdditionsSound.chance)).apply((Applicative<BiomeAdditionsSound, ?>)instance, BiomeAdditionsSound::new));
    private final SoundEvent sound;
    private final double chance;

    public BiomeAdditionsSound(SoundEvent sound, double chance) {
        this.sound = sound;
        this.chance = chance;
    }

    @Environment(value=EnvType.CLIENT)
    public SoundEvent getSound() {
        return this.sound;
    }

    /**
     * Returns the chance of this addition sound to play at any tick.
     */
    @Environment(value=EnvType.CLIENT)
    public double getChance() {
        return this.chance;
    }
}


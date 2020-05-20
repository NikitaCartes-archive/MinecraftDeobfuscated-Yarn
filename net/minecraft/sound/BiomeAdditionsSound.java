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
    public static final Codec<BiomeAdditionsSound> field_24673 = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)SoundEvent.field_24628.fieldOf("sound")).forGetter(biomeAdditionsSound -> biomeAdditionsSound.event), ((MapCodec)Codec.DOUBLE.fieldOf("tick_chance")).forGetter(biomeAdditionsSound -> biomeAdditionsSound.chance)).apply((Applicative<BiomeAdditionsSound, ?>)instance, BiomeAdditionsSound::new));
    private SoundEvent event;
    private double chance;

    public BiomeAdditionsSound(SoundEvent event, double chance) {
        this.event = event;
        this.chance = chance;
    }

    @Environment(value=EnvType.CLIENT)
    public SoundEvent getEvent() {
        return this.event;
    }

    /**
     * Returns the chance of this addition sound to play at any tick.
     */
    @Environment(value=EnvType.CLIENT)
    public double getChance() {
        return this.chance;
    }
}


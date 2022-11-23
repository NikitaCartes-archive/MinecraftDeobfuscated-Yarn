/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.dynamic.Codecs;

public record Instrument(SoundEvent soundEvent, int useDuration, float range) {
    public static final Codec<Instrument> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Registries.SOUND_EVENT.getCodec().fieldOf("sound_event")).forGetter(Instrument::soundEvent), ((MapCodec)Codecs.POSITIVE_INT.fieldOf("use_duration")).forGetter(Instrument::useDuration), ((MapCodec)Codecs.POSITIVE_FLOAT.fieldOf("range")).forGetter(Instrument::range)).apply((Applicative<Instrument, ?>)instance, Instrument::new));
}


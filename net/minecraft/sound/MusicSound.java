/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.sound;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.sound.SoundEvent;

public class MusicSound {
    public static final Codec<MusicSound> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)SoundEvent.CODEC.fieldOf("sound")).forGetter(musicSound -> musicSound.sound), ((MapCodec)Codec.INT.fieldOf("min_delay")).forGetter(musicSound -> musicSound.minDelay), ((MapCodec)Codec.INT.fieldOf("max_delay")).forGetter(musicSound -> musicSound.maxDelay), ((MapCodec)Codec.BOOL.fieldOf("replace_current_music")).forGetter(musicSound -> musicSound.replaceCurrentMusic)).apply((Applicative<MusicSound, ?>)instance, MusicSound::new));
    private final SoundEvent sound;
    private final int minDelay;
    private final int maxDelay;
    private final boolean replaceCurrentMusic;

    public MusicSound(SoundEvent sound, int minDelay, int maxDelay, boolean replaceCurrentMusic) {
        this.sound = sound;
        this.minDelay = minDelay;
        this.maxDelay = maxDelay;
        this.replaceCurrentMusic = replaceCurrentMusic;
    }

    public SoundEvent getSound() {
        return this.sound;
    }

    public int getMinDelay() {
        return this.minDelay;
    }

    public int getMaxDelay() {
        return this.maxDelay;
    }

    public boolean shouldReplaceCurrentMusic() {
        return this.replaceCurrentMusic;
    }
}


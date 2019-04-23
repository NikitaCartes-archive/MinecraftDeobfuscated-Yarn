/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.AbstractSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

@Environment(value=EnvType.CLIENT)
public class PositionedSoundInstance
extends AbstractSoundInstance {
    public PositionedSoundInstance(SoundEvent soundEvent, SoundCategory soundCategory, float f, float g, BlockPos blockPos) {
        this(soundEvent, soundCategory, f, g, (float)blockPos.getX() + 0.5f, (float)blockPos.getY() + 0.5f, (float)blockPos.getZ() + 0.5f);
    }

    public static PositionedSoundInstance master(SoundEvent soundEvent, float f) {
        return PositionedSoundInstance.master(soundEvent, f, 0.25f);
    }

    public static PositionedSoundInstance master(SoundEvent soundEvent, float f, float g) {
        return new PositionedSoundInstance(soundEvent.getId(), SoundCategory.MASTER, g, f, false, 0, SoundInstance.AttenuationType.NONE, 0.0f, 0.0f, 0.0f, true);
    }

    public static PositionedSoundInstance music(SoundEvent soundEvent) {
        return new PositionedSoundInstance(soundEvent.getId(), SoundCategory.MUSIC, 1.0f, 1.0f, false, 0, SoundInstance.AttenuationType.NONE, 0.0f, 0.0f, 0.0f, true);
    }

    public static PositionedSoundInstance record(SoundEvent soundEvent, float f, float g, float h) {
        return new PositionedSoundInstance(soundEvent, SoundCategory.RECORDS, 4.0f, 1.0f, false, 0, SoundInstance.AttenuationType.LINEAR, f, g, h);
    }

    public PositionedSoundInstance(SoundEvent soundEvent, SoundCategory soundCategory, float f, float g, float h, float i, float j) {
        this(soundEvent, soundCategory, f, g, false, 0, SoundInstance.AttenuationType.LINEAR, h, i, j);
    }

    private PositionedSoundInstance(SoundEvent soundEvent, SoundCategory soundCategory, float f, float g, boolean bl, int i, SoundInstance.AttenuationType attenuationType, float h, float j, float k) {
        this(soundEvent.getId(), soundCategory, f, g, bl, i, attenuationType, h, j, k, false);
    }

    public PositionedSoundInstance(Identifier identifier, SoundCategory soundCategory, float f, float g, boolean bl, int i, SoundInstance.AttenuationType attenuationType, float h, float j, float k, boolean bl2) {
        super(identifier, soundCategory);
        this.volume = f;
        this.pitch = g;
        this.x = h;
        this.y = j;
        this.z = k;
        this.repeat = bl;
        this.repeatDelay = i;
        this.attenuationType = attenuationType;
        this.looping = bl2;
    }
}


/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.passive.SnifferEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

@Environment(value=EnvType.CLIENT)
public class SnifferDigSoundInstance
extends MovingSoundInstance {
    private static final float field_42931 = 1.0f;
    private static final float field_42932 = 1.0f;
    private final SnifferEntity sniffer;

    public SnifferDigSoundInstance(SnifferEntity sniffer) {
        super(SoundEvents.ENTITY_SNIFFER_DIGGING, SoundCategory.NEUTRAL, SoundInstance.createRandom());
        this.sniffer = sniffer;
        this.attenuationType = SoundInstance.AttenuationType.NONE;
        this.repeat = false;
        this.repeatDelay = 0;
    }

    @Override
    public boolean canPlay() {
        return !this.sniffer.isSilent();
    }

    @Override
    public void tick() {
        if (this.sniffer.isRemoved() || this.sniffer.getTarget() != null || !this.sniffer.isDiggingOrSearching()) {
            this.setDone();
            return;
        }
        this.x = (float)this.sniffer.getX();
        this.y = (float)this.sniffer.getY();
        this.z = (float)this.sniffer.getZ();
        this.volume = 1.0f;
        this.pitch = 1.0f;
    }
}


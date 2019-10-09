/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

@Environment(value=EnvType.CLIENT)
public class EntityTrackingSoundInstance
extends MovingSoundInstance {
    private final Entity entity;

    public EntityTrackingSoundInstance(SoundEvent soundEvent, SoundCategory soundCategory, Entity entity) {
        this(soundEvent, soundCategory, 1.0f, 1.0f, entity);
    }

    public EntityTrackingSoundInstance(SoundEvent soundEvent, SoundCategory soundCategory, float f, float g, Entity entity) {
        super(soundEvent, soundCategory);
        this.volume = f;
        this.pitch = g;
        this.entity = entity;
        this.x = (float)this.entity.getX();
        this.y = (float)this.entity.getY();
        this.z = (float)this.entity.getZ();
    }

    @Override
    public void tick() {
        if (this.entity.removed) {
            this.done = true;
            return;
        }
        this.x = (float)this.entity.getX();
        this.y = (float)this.entity.getY();
        this.z = (float)this.entity.getZ();
    }
}


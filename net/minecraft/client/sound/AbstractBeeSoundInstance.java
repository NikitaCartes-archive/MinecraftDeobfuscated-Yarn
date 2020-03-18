/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public abstract class AbstractBeeSoundInstance
extends MovingSoundInstance {
    protected final BeeEntity bee;
    private boolean replaced;

    public AbstractBeeSoundInstance(BeeEntity beeEntity, SoundEvent soundEvent, SoundCategory soundCategory) {
        super(soundEvent, soundCategory);
        this.bee = beeEntity;
        this.x = (float)beeEntity.getX();
        this.y = (float)beeEntity.getY();
        this.z = (float)beeEntity.getZ();
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 0.0f;
    }

    @Override
    public void tick() {
        boolean bl = this.shouldReplace();
        if (bl && !this.isDone()) {
            MinecraftClient.getInstance().getSoundManager().playNextTick(this.getReplacement());
            this.replaced = true;
        }
        if (this.bee.removed || this.replaced) {
            this.setDone();
            return;
        }
        this.x = (float)this.bee.getX();
        this.y = (float)this.bee.getY();
        this.z = (float)this.bee.getZ();
        float f = MathHelper.sqrt(Entity.squaredHorizontalLength(this.bee.getVelocity()));
        if ((double)f >= 0.01) {
            this.pitch = MathHelper.lerp(MathHelper.clamp(f, this.getMinPitch(), this.getMaxPitch()), this.getMinPitch(), this.getMaxPitch());
            this.volume = MathHelper.lerp(MathHelper.clamp(f, 0.0f, 0.5f), 0.0f, 1.2f);
        } else {
            this.pitch = 0.0f;
            this.volume = 0.0f;
        }
    }

    private float getMinPitch() {
        if (this.bee.isBaby()) {
            return 1.1f;
        }
        return 0.7f;
    }

    private float getMaxPitch() {
        if (this.bee.isBaby()) {
            return 1.5f;
        }
        return 1.1f;
    }

    @Override
    public boolean shouldAlwaysPlay() {
        return true;
    }

    @Override
    public boolean canPlay() {
        return !this.bee.isSilent();
    }

    protected abstract MovingSoundInstance getReplacement();

    protected abstract boolean shouldReplace();
}


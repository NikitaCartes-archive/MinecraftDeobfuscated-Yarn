/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;

/**
 * A sound instance played when a minecart is moving.
 */
@Environment(value=EnvType.CLIENT)
public class MovingMinecartSoundInstance
extends MovingSoundInstance {
    private static final float field_33001 = 0.0f;
    private static final float field_33002 = 0.7f;
    private static final float field_33003 = 0.0f;
    private static final float field_33004 = 1.0f;
    private static final float field_33005 = 0.0025f;
    private final AbstractMinecartEntity minecart;
    private float distance = 0.0f;

    public MovingMinecartSoundInstance(AbstractMinecartEntity minecart) {
        super(SoundEvents.ENTITY_MINECART_RIDING, SoundCategory.NEUTRAL);
        this.minecart = minecart;
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 0.0f;
        this.x = (float)minecart.getX();
        this.y = (float)minecart.getY();
        this.z = (float)minecart.getZ();
    }

    @Override
    public boolean canPlay() {
        return !this.minecart.isSilent();
    }

    @Override
    public boolean shouldAlwaysPlay() {
        return true;
    }

    @Override
    public void tick() {
        if (this.minecart.isRemoved()) {
            this.setDone();
            return;
        }
        this.x = (float)this.minecart.getX();
        this.y = (float)this.minecart.getY();
        this.z = (float)this.minecart.getZ();
        float f = (float)this.minecart.getVelocity().horizontalLength();
        if (f >= 0.01f) {
            this.distance = MathHelper.clamp(this.distance + 0.0025f, 0.0f, 1.0f);
            this.volume = MathHelper.lerp(MathHelper.clamp(f, 0.0f, 0.5f), 0.0f, 0.7f);
        } else {
            this.distance = 0.0f;
            this.volume = 0.0f;
        }
    }
}


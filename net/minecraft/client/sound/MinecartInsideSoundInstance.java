/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;

/**
 * A sound instance played when a player is riding a minecart.
 */
@Environment(value=EnvType.CLIENT)
public class MinecartInsideSoundInstance
extends MovingSoundInstance {
    private final PlayerEntity player;
    private final AbstractMinecartEntity minecart;

    public MinecartInsideSoundInstance(PlayerEntity player, AbstractMinecartEntity minecart) {
        super(SoundEvents.ENTITY_MINECART_INSIDE, SoundCategory.NEUTRAL);
        this.player = player;
        this.minecart = minecart;
        this.attenuationType = SoundInstance.AttenuationType.NONE;
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 0.0f;
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
        if (this.minecart.removed || !this.player.hasVehicle() || this.player.getVehicle() != this.minecart) {
            this.setDone();
            return;
        }
        float f = MathHelper.sqrt(Entity.squaredHorizontalLength(this.minecart.getVelocity()));
        this.volume = (double)f >= 0.01 ? 0.0f + MathHelper.clamp(f, 0.0f, 1.0f) * 0.75f : 0.0f;
    }
}


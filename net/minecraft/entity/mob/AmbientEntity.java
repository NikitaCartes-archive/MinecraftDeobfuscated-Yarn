/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.mob;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public abstract class AmbientEntity
extends MobEntity {
    protected AmbientEntity(EntityType<? extends AmbientEntity> entityType, World world) {
        super((EntityType<? extends MobEntity>)entityType, world);
    }

    @Override
    public boolean canBeLeashedBy(PlayerEntity playerEntity) {
        return false;
    }
}


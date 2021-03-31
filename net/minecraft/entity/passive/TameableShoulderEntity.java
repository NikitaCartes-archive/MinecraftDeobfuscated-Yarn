/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public abstract class TameableShoulderEntity
extends TameableEntity {
    private static final int field_30372 = 100;
    private int ticks;

    protected TameableShoulderEntity(EntityType<? extends TameableShoulderEntity> entityType, World world) {
        super((EntityType<? extends TameableEntity>)entityType, world);
    }

    public boolean mountOnto(ServerPlayerEntity player) {
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.putString("id", this.getSavedEntityId());
        this.writeNbt(nbtCompound);
        if (player.addShoulderEntity(nbtCompound)) {
            this.discard();
            return true;
        }
        return false;
    }

    @Override
    public void tick() {
        ++this.ticks;
        super.tick();
    }

    public boolean isReadyToSitOnPlayer() {
        return this.ticks > 100;
    }
}


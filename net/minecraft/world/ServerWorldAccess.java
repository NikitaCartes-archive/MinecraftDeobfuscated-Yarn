/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.WorldAccess;

/**
 * Represents access to a world on a logical Minecraft server.
 */
public interface ServerWorldAccess
extends WorldAccess {
    public ServerWorld toServerWorld();

    /**
     * Spawns an entity and all its passengers (recursively) into the world.
     */
    default public void spawnEntityAndPassengers(Entity entity) {
        entity.streamSelfAndPassengers().forEach(this::spawnEntity);
    }
}


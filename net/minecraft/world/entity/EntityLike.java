/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.entity;

import java.util.UUID;
import java.util.stream.Stream;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.entity.EntityChangeListener;

/**
 * A prototype of entity that's suitable for entity manager to handle.
 */
public interface EntityLike {
    /**
     * Returns the network ID of this entity.
     */
    public int getId();

    public UUID getUuid();

    public BlockPos getBlockPos();

    public Box getBoundingBox();

    public void setListener(EntityChangeListener var1);

    /**
     * Returns a stream consisting of this entity and its passengers in which
     * this entity's passengers are iterated before this entity.
     * 
     * <p>Moreover, this stream guarantees that any entity only appears after
     * all its passengers have appeared in the stream. This is useful for
     * certain actions that must be applied on passengers before applying on
     * this entity.
     * 
     * @implNote The default implementation is very costly.
     * 
     * @see net.minecraft.entity.Entity#streamSelfAndPassengers()
     */
    public Stream<? extends EntityLike> streamPassengersAndSelf();

    public void setRemoved(Entity.RemovalReason var1);

    public boolean shouldSave();

    public boolean isPlayer();
}


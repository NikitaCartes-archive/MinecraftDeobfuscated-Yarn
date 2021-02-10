/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.entity;

/**
 * The entity handler exposes world's entity handling to entity managers.
 * 
 * <p>Each handler is usually associated with a {@link net.minecraft.world.World}.
 * 
 * @param <T> the type of entity handled
 */
public interface EntityHandler<T> {
    /**
     * Called when an entity is newly created.
     * 
     * @param entity the created entity
     */
    public void create(T var1);

    /**
     * Called when an entity is permanently destroyed.
     * 
     * @param entity the destroyed entity
     */
    public void destroy(T var1);

    /**
     * Registers an entity for ticking.
     * 
     * @param entity the entity to tick
     */
    public void startTicking(T var1);

    /**
     * Unregisters an entity for ticking.
     * 
     * @param entity the ticked entity
     */
    public void stopTicking(T var1);

    /**
     * Registers an entity for tracking.
     * 
     * @param entity the entity to track
     */
    public void startTracking(T var1);

    /**
     * Unregisters an entity for tracking.
     * 
     * @param entity the tracked entity
     */
    public void stopTracking(T var1);
}


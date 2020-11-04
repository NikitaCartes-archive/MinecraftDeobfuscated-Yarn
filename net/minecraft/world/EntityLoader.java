/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

/**
 * Isolates client and server side specific logic when loading and unloading entities to and from a world.
 */
public interface EntityLoader<T> {
    public void method_31802(T var1);

    public void destroyEntity(T var1);

    public void addEntity(T var1);

    public void removeEntity(T var1);

    public void onLoadEntity(T var1);

    public void onUnloadEntity(T var1);
}


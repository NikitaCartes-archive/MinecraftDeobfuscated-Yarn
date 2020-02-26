/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server;

import org.jetbrains.annotations.Nullable;

public abstract class ServerConfigEntry<T> {
    @Nullable
    private final T object;

    public ServerConfigEntry(@Nullable T object) {
        this.object = object;
    }

    @Nullable
    T getKey() {
        return this.object;
    }

    boolean isInvalid() {
        return false;
    }
}


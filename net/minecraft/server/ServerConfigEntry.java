/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;

public abstract class ServerConfigEntry<T> {
    @Nullable
    private final T key;

    public ServerConfigEntry(@Nullable T key) {
        this.key = key;
    }

    @Nullable
    T getKey() {
        return this.key;
    }

    boolean isInvalid() {
        return false;
    }

    protected abstract void fromJson(JsonObject var1);
}


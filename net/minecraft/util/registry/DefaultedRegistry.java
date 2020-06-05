/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.registry;

import com.mojang.serialization.Lifecycle;
import java.util.Random;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DefaultedRegistry<T>
extends SimpleRegistry<T> {
    private final Identifier defaultId;
    private T defaultValue;

    public DefaultedRegistry(String defaultId, RegistryKey<Registry<T>> registryKey, Lifecycle lifecycle) {
        super(registryKey, lifecycle);
        this.defaultId = new Identifier(defaultId);
    }

    @Override
    public <V extends T> V set(int rawId, RegistryKey<T> key, V entry) {
        if (this.defaultId.equals(key.getValue())) {
            this.defaultValue = entry;
        }
        return super.set(rawId, key, entry);
    }

    @Override
    public int getRawId(@Nullable T entry) {
        int i = super.getRawId(entry);
        return i == -1 ? super.getRawId(this.defaultValue) : i;
    }

    @Override
    @NotNull
    public Identifier getId(T entry) {
        Identifier identifier = super.getId(entry);
        return identifier == null ? this.defaultId : identifier;
    }

    @Override
    @NotNull
    public T get(@Nullable Identifier id) {
        Object object = super.get(id);
        return object == null ? this.defaultValue : object;
    }

    @Override
    @NotNull
    public T get(int index) {
        Object object = super.get(index);
        return object == null ? this.defaultValue : object;
    }

    @Override
    @NotNull
    public T getRandom(Random random) {
        Object object = super.getRandom(random);
        return object == null ? this.defaultValue : object;
    }

    public Identifier getDefaultId() {
        return this.defaultId;
    }
}


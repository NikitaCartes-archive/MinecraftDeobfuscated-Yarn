/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.registry;

import com.mojang.serialization.Lifecycle;
import java.util.Optional;
import net.minecraft.registry.DefaultedRegistry;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An implementation of {@link Registry} with a default ID and value for unknown lookups.
 */
public class SimpleDefaultedRegistry<T>
extends SimpleRegistry<T>
implements DefaultedRegistry<T> {
    private final Identifier defaultId;
    private RegistryEntry.Reference<T> defaultEntry;

    public SimpleDefaultedRegistry(String defaultId, RegistryKey<? extends Registry<T>> key, Lifecycle lifecycle, boolean intrusive) {
        super(key, lifecycle, intrusive);
        this.defaultId = new Identifier(defaultId);
    }

    @Override
    public RegistryEntry.Reference<T> set(int i, RegistryKey<T> registryKey, T object, Lifecycle lifecycle) {
        RegistryEntry reference = super.set(i, (RegistryKey)registryKey, (Object)object, lifecycle);
        if (this.defaultId.equals(registryKey.getValue())) {
            this.defaultEntry = reference;
        }
        return reference;
    }

    @Override
    public int getRawId(@Nullable T value) {
        int i = super.getRawId(value);
        return i == -1 ? super.getRawId(this.defaultEntry.value()) : i;
    }

    @Override
    @NotNull
    public Identifier getId(T value) {
        Identifier identifier = super.getId(value);
        return identifier == null ? this.defaultId : identifier;
    }

    @Override
    @NotNull
    public T get(@Nullable Identifier id) {
        Object object = super.get(id);
        return object == null ? this.defaultEntry.value() : object;
    }

    @Override
    public Optional<T> getOrEmpty(@Nullable Identifier id) {
        return Optional.ofNullable(super.get(id));
    }

    @Override
    @NotNull
    public T get(int index) {
        Object object = super.get(index);
        return object == null ? this.defaultEntry.value() : object;
    }

    @Override
    public Optional<RegistryEntry.Reference<T>> getRandom(Random random) {
        return super.getRandom(random).or(() -> Optional.of(this.defaultEntry));
    }

    @Override
    public Identifier getDefaultId() {
        return this.defaultId;
    }

    @Override
    public /* synthetic */ RegistryEntry set(int rawId, RegistryKey key, Object value, Lifecycle lifecycle) {
        return this.set(rawId, key, value, lifecycle);
    }
}


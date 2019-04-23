/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.registry;

import java.util.Random;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.SimpleRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DefaultedRegistry<T>
extends SimpleRegistry<T> {
    private final Identifier defaultId;
    private T defaultValue;

    public DefaultedRegistry(String string) {
        this.defaultId = new Identifier(string);
    }

    @Override
    public <V extends T> V set(int i, Identifier identifier, V object) {
        if (this.defaultId.equals(identifier)) {
            this.defaultValue = object;
        }
        return super.set(i, identifier, object);
    }

    @Override
    public int getRawId(@Nullable T object) {
        int i = super.getRawId(object);
        return i == -1 ? super.getRawId(this.defaultValue) : i;
    }

    @Override
    @NotNull
    public Identifier getId(T object) {
        Identifier identifier = super.getId(object);
        return identifier == null ? this.defaultId : identifier;
    }

    @Override
    @NotNull
    public T get(@Nullable Identifier identifier) {
        Object object = super.get(identifier);
        return object == null ? this.defaultValue : object;
    }

    @Override
    @NotNull
    public T get(int i) {
        Object object = super.get(i);
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


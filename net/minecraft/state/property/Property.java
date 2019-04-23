/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.state.property;

import java.util.Collection;
import java.util.Optional;

public interface Property<T extends Comparable<T>> {
    public String getName();

    public Collection<T> getValues();

    public Class<T> getValueClass();

    public Optional<T> getValue(String var1);

    public String getValueAsString(T var1);
}


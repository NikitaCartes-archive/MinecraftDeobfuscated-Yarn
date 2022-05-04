/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.search;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * A functional interface that provides searching.
 */
@Environment(value=EnvType.CLIENT)
public interface SearchProvider<T> {
    /**
     * {@return the search result of {@code text}}
     */
    public List<T> findAll(String var1);
}


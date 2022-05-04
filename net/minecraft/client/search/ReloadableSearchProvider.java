/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.search;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.search.SearchProvider;

/**
 * A functional interface that provides searching and can be reloaded.
 */
@Environment(value=EnvType.CLIENT)
public interface ReloadableSearchProvider<T>
extends SearchProvider<T> {
    /**
     * {@return a search provider that always returns no results}
     */
    public static <T> ReloadableSearchProvider<T> empty() {
        return text -> List.of();
    }

    /**
     * Reloads the search provider.
     * 
     * @apiNote Implementations must supply the context (e.g. the new data) separately,
     * for example by having a getter as a field on an implementing class.
     */
    default public void reload() {
    }
}


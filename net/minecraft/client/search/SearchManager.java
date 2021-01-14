/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.search;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.search.SearchableContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloader;

@Environment(value=EnvType.CLIENT)
public class SearchManager
implements SynchronousResourceReloader {
    public static final Key<ItemStack> ITEM_TOOLTIP = new Key();
    public static final Key<ItemStack> ITEM_TAG = new Key();
    public static final Key<RecipeResultCollection> RECIPE_OUTPUT = new Key();
    private final Map<Key<?>, SearchableContainer<?>> instances = Maps.newHashMap();

    @Override
    public void reload(ResourceManager manager) {
        for (SearchableContainer<?> searchableContainer : this.instances.values()) {
            searchableContainer.reload();
        }
    }

    public <T> void put(Key<T> key, SearchableContainer<T> value) {
        this.instances.put(key, value);
    }

    public <T> SearchableContainer<T> get(Key<T> key) {
        return this.instances.get(key);
    }

    @Environment(value=EnvType.CLIENT)
    public static class Key<T> {
    }
}


/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.search;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.search.ReloadableSearchProvider;
import net.minecraft.client.search.SearchProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloader;

@Environment(value=EnvType.CLIENT)
public class SearchManager
implements SynchronousResourceReloader {
    public static final Key<ItemStack> ITEM_TOOLTIP = new Key();
    public static final Key<ItemStack> ITEM_TAG = new Key();
    public static final Key<RecipeResultCollection> RECIPE_OUTPUT = new Key();
    private final Map<Key<?>, Instance<?>> instances = new HashMap();

    @Override
    public void reload(ResourceManager manager) {
        for (Instance<?> instance : this.instances.values()) {
            instance.reload();
        }
    }

    public <T> void put(Key<T> key, ProviderGetter<T> providerGetter) {
        this.instances.put(key, new Instance<T>(providerGetter));
    }

    private <T> Instance<T> getInstance(Key<T> key) {
        Instance<?> instance = this.instances.get(key);
        if (instance == null) {
            throw new IllegalStateException("Tree builder not registered");
        }
        return instance;
    }

    public <T> void reload(Key<T> key, List<T> values) {
        this.getInstance(key).reload(values);
    }

    public <T> SearchProvider<T> get(Key<T> key) {
        return this.getInstance(key).provider;
    }

    @Environment(value=EnvType.CLIENT)
    static class Instance<T> {
        private final ProviderGetter<T> providerGetter;
        ReloadableSearchProvider<T> provider = ReloadableSearchProvider.empty();

        Instance(ProviderGetter<T> providerGetter) {
            this.providerGetter = providerGetter;
        }

        void reload(List<T> values) {
            this.provider = (ReloadableSearchProvider)this.providerGetter.apply(values);
            this.provider.reload();
        }

        void reload() {
            this.provider.reload();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static interface ProviderGetter<T>
    extends Function<List<T>, ReloadableSearchProvider<T>> {
    }

    @Environment(value=EnvType.CLIENT)
    public static class Key<T> {
    }
}


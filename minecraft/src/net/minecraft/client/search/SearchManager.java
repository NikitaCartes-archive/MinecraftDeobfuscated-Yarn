package net.minecraft.client.search;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloader;

@Environment(EnvType.CLIENT)
public class SearchManager implements SynchronousResourceReloader {
	public static final SearchManager.Key<ItemStack> ITEM_TOOLTIP = new SearchManager.Key<>();
	public static final SearchManager.Key<ItemStack> ITEM_TAG = new SearchManager.Key<>();
	public static final SearchManager.Key<RecipeResultCollection> RECIPE_OUTPUT = new SearchManager.Key<>();
	private final Map<SearchManager.Key<?>, SearchManager.Instance<?>> instances = new HashMap();

	@Override
	public void reload(ResourceManager manager) {
		for (SearchManager.Instance<?> instance : this.instances.values()) {
			instance.reload();
		}
	}

	public <T> void put(SearchManager.Key<T> key, SearchManager.ProviderGetter<T> providerGetter) {
		this.instances.put(key, new SearchManager.Instance<>(providerGetter));
	}

	private <T> SearchManager.Instance<T> getInstance(SearchManager.Key<T> key) {
		SearchManager.Instance<T> instance = (SearchManager.Instance<T>)this.instances.get(key);
		if (instance == null) {
			throw new IllegalStateException("Tree builder not registered");
		} else {
			return instance;
		}
	}

	public <T> void reload(SearchManager.Key<T> key, List<T> values) {
		this.getInstance(key).reload(values);
	}

	public <T> SearchProvider<T> get(SearchManager.Key<T> key) {
		return this.getInstance(key).provider;
	}

	@Environment(EnvType.CLIENT)
	static class Instance<T> {
		private final SearchManager.ProviderGetter<T> providerGetter;
		ReloadableSearchProvider<T> provider = ReloadableSearchProvider.empty();

		Instance(SearchManager.ProviderGetter<T> providerGetter) {
			this.providerGetter = providerGetter;
		}

		void reload(List<T> values) {
			this.provider = (ReloadableSearchProvider<T>)this.providerGetter.apply(values);
			this.provider.reload();
		}

		void reload() {
			this.provider.reload();
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Key<T> {
	}

	@Environment(EnvType.CLIENT)
	public interface ProviderGetter<T> extends Function<List<T>, ReloadableSearchProvider<T>> {
	}
}

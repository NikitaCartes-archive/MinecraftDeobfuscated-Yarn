package net.minecraft.client.search;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.recipe.book.RecipeResultCollection;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;

@Environment(EnvType.CLIENT)
public class SearchManager implements ResourceReloadListener {
	public static final SearchManager.Key<ItemStack> ITEMS_TOOLTIP = new SearchManager.Key<>();
	public static final SearchManager.Key<ItemStack> ITEMS_TAG = new SearchManager.Key<>();
	public static final SearchManager.Key<RecipeResultCollection> field_5496 = new SearchManager.Key<>();
	private final Map<SearchManager.Key<?>, SearchableContainer<?>> instances = Maps.<SearchManager.Key<?>, SearchableContainer<?>>newHashMap();

	@Override
	public void onResourceReload(ResourceManager resourceManager) {
		for (SearchableContainer<?> searchableContainer : this.instances.values()) {
			searchableContainer.reload();
		}
	}

	public <T> void put(SearchManager.Key<T> key, SearchableContainer<T> searchableContainer) {
		this.instances.put(key, searchableContainer);
	}

	public <T> SearchableContainer<T> get(SearchManager.Key<T> key) {
		return (SearchableContainer<T>)this.instances.get(key);
	}

	@Environment(EnvType.CLIENT)
	public static class Key<T> {
	}
}

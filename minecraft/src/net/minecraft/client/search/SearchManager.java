package net.minecraft.client.search;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.item.TooltipType;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class SearchManager {
	private static final SearchManager.Key RECIPE_OUTPUT = new SearchManager.Key();
	private static final SearchManager.Key ITEM_TOOLTIP = new SearchManager.Key();
	private static final SearchManager.Key ITEM_TAG = new SearchManager.Key();
	private CompletableFuture<SearchProvider<ItemStack>> field_51826 = CompletableFuture.completedFuture(SearchProvider.empty());
	private CompletableFuture<SearchProvider<ItemStack>> field_51827 = CompletableFuture.completedFuture(SearchProvider.empty());
	private CompletableFuture<SearchProvider<RecipeResultCollection>> field_51828 = CompletableFuture.completedFuture(SearchProvider.empty());
	private final Map<SearchManager.Key, Runnable> field_51829 = new IdentityHashMap();

	private void method_60353(SearchManager.Key key, Runnable runnable) {
		runnable.run();
		this.field_51829.put(key, runnable);
	}

	public void method_60348() {
		for (Runnable runnable : this.field_51829.values()) {
			runnable.run();
		}
	}

	private static Stream<String> method_60356(Stream<ItemStack> stream, Item.TooltipContext tooltipContext, TooltipType tooltipType) {
		return stream.flatMap(itemStack -> itemStack.getTooltip(tooltipContext, null, tooltipType).stream())
			.map(text -> Formatting.strip(text.getString()).trim())
			.filter(string -> !string.isEmpty());
	}

	public void method_60352(ClientRecipeBook clientRecipeBook, DynamicRegistryManager.Immutable immutable) {
		this.method_60353(
			RECIPE_OUTPUT,
			() -> {
				List<RecipeResultCollection> list = clientRecipeBook.getOrderedResults();
				Registry<Item> registry = immutable.get(RegistryKeys.ITEM);
				Item.TooltipContext tooltipContext = Item.TooltipContext.create(immutable);
				TooltipType tooltipType = TooltipType.Default.BASIC;
				CompletableFuture<?> completableFuture = this.field_51828;
				this.field_51828 = CompletableFuture.supplyAsync(
					() -> new TextSearchProvider(
							recipeResultCollection -> method_60356(
									recipeResultCollection.getAllRecipes().stream().map(recipeEntry -> recipeEntry.value().getResult(immutable)), tooltipContext, tooltipType
								),
							recipeResultCollection -> recipeResultCollection.getAllRecipes()
									.stream()
									.map(recipeEntry -> registry.getId(recipeEntry.value().getResult(immutable).getItem())),
							list
						),
					Util.getMainWorkerExecutor()
				);
				completableFuture.cancel(true);
			}
		);
	}

	public SearchProvider<RecipeResultCollection> method_60364() {
		return (SearchProvider<RecipeResultCollection>)this.field_51828.join();
	}

	public void method_60355(List<ItemStack> list) {
		this.method_60353(
			ITEM_TAG,
			() -> {
				CompletableFuture<?> completableFuture = this.field_51827;
				this.field_51827 = CompletableFuture.supplyAsync(
					() -> new IdentifierSearchProvider(itemStack -> itemStack.streamTags().map(TagKey::id), list), Util.getMainWorkerExecutor()
				);
				completableFuture.cancel(true);
			}
		);
	}

	public SearchProvider<ItemStack> method_60370() {
		return (SearchProvider<ItemStack>)this.field_51827.join();
	}

	public void method_60357(RegistryWrapper.WrapperLookup wrapperLookup, List<ItemStack> list) {
		this.method_60353(
			ITEM_TOOLTIP,
			() -> {
				Item.TooltipContext tooltipContext = Item.TooltipContext.create(wrapperLookup);
				TooltipType tooltipType = TooltipType.Default.BASIC.withCreative();
				CompletableFuture<?> completableFuture = this.field_51826;
				this.field_51826 = CompletableFuture.supplyAsync(
					() -> new TextSearchProvider(
							itemStack -> method_60356(Stream.of(itemStack), tooltipContext, tooltipType),
							itemStack -> itemStack.getRegistryEntry().getKey().map(RegistryKey::getValue).stream(),
							list
						),
					Util.getMainWorkerExecutor()
				);
				completableFuture.cancel(true);
			}
		);
	}

	public SearchProvider<ItemStack> method_60372() {
		return (SearchProvider<ItemStack>)this.field_51826.join();
	}

	@Environment(EnvType.CLIENT)
	static class Key {
	}
}

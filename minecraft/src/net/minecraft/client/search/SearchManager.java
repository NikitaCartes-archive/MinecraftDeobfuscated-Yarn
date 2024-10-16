package net.minecraft.client.search;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.recipe.display.SlotDisplayContexts;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.context.ContextParameterMap;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class SearchManager {
	private static final SearchManager.Key RECIPE_OUTPUT = new SearchManager.Key();
	private static final SearchManager.Key ITEM_TOOLTIP = new SearchManager.Key();
	private static final SearchManager.Key ITEM_TAG = new SearchManager.Key();
	private CompletableFuture<SearchProvider<ItemStack>> itemTooltipReloadFuture = CompletableFuture.completedFuture(SearchProvider.empty());
	private CompletableFuture<SearchProvider<ItemStack>> itemTagReloadFuture = CompletableFuture.completedFuture(SearchProvider.empty());
	private CompletableFuture<SearchProvider<RecipeResultCollection>> recipeOutputReloadFuture = CompletableFuture.completedFuture(SearchProvider.empty());
	private final Map<SearchManager.Key, Runnable> reloaders = new IdentityHashMap();

	private void addReloader(SearchManager.Key key, Runnable reloader) {
		reloader.run();
		this.reloaders.put(key, reloader);
	}

	public void refresh() {
		for (Runnable runnable : this.reloaders.values()) {
			runnable.run();
		}
	}

	private static Stream<String> collectItemTooltips(Stream<ItemStack> stacks, Item.TooltipContext context, TooltipType type) {
		return stacks.flatMap(stack -> stack.getTooltip(context, null, type).stream())
			.map(tooltip -> Formatting.strip(tooltip.getString()).trim())
			.filter(string -> !string.isEmpty());
	}

	public void addRecipeOutputReloader(ClientRecipeBook recipeBook, World world) {
		this.addReloader(
			RECIPE_OUTPUT,
			() -> {
				List<RecipeResultCollection> list = recipeBook.getOrderedResults();
				DynamicRegistryManager dynamicRegistryManager = world.getRegistryManager();
				Registry<Item> registry = dynamicRegistryManager.getOrThrow(RegistryKeys.ITEM);
				Item.TooltipContext tooltipContext = Item.TooltipContext.create(dynamicRegistryManager);
				ContextParameterMap contextParameterMap = SlotDisplayContexts.createParameters(world);
				TooltipType tooltipType = TooltipType.Default.BASIC;
				CompletableFuture<?> completableFuture = this.recipeOutputReloadFuture;
				this.recipeOutputReloadFuture = CompletableFuture.supplyAsync(
					() -> new TextSearchProvider(
							resultCollection -> collectItemTooltips(
									resultCollection.getAllRecipes().stream().flatMap(display -> display.getStacks(contextParameterMap).stream()), tooltipContext, tooltipType
								),
							resultCollection -> resultCollection.getAllRecipes()
									.stream()
									.flatMap(display -> display.getStacks(contextParameterMap).stream())
									.map(stack -> registry.getId(stack.getItem())),
							list
						),
					Util.getMainWorkerExecutor()
				);
				completableFuture.cancel(true);
			}
		);
	}

	public SearchProvider<RecipeResultCollection> getRecipeOutputReloadFuture() {
		return (SearchProvider<RecipeResultCollection>)this.recipeOutputReloadFuture.join();
	}

	public void addItemTagReloader(List<ItemStack> stacks) {
		this.addReloader(
			ITEM_TAG,
			() -> {
				CompletableFuture<?> completableFuture = this.itemTagReloadFuture;
				this.itemTagReloadFuture = CompletableFuture.supplyAsync(
					() -> new IdentifierSearchProvider(stack -> stack.streamTags().map(TagKey::id), stacks), Util.getMainWorkerExecutor()
				);
				completableFuture.cancel(true);
			}
		);
	}

	public SearchProvider<ItemStack> getItemTagReloadFuture() {
		return (SearchProvider<ItemStack>)this.itemTagReloadFuture.join();
	}

	public void addItemTooltipReloader(RegistryWrapper.WrapperLookup registries, List<ItemStack> stacks) {
		this.addReloader(
			ITEM_TOOLTIP,
			() -> {
				Item.TooltipContext tooltipContext = Item.TooltipContext.create(registries);
				TooltipType tooltipType = TooltipType.Default.BASIC.withCreative();
				CompletableFuture<?> completableFuture = this.itemTooltipReloadFuture;
				this.itemTooltipReloadFuture = CompletableFuture.supplyAsync(
					() -> new TextSearchProvider(
							stack -> collectItemTooltips(Stream.of(stack), tooltipContext, tooltipType),
							stack -> stack.getRegistryEntry().getKey().map(RegistryKey::getValue).stream(),
							stacks
						),
					Util.getMainWorkerExecutor()
				);
				completableFuture.cancel(true);
			}
		);
	}

	public SearchProvider<ItemStack> getItemTooltipReloadFuture() {
		return (SearchProvider<ItemStack>)this.itemTooltipReloadFuture.join();
	}

	@Environment(EnvType.CLIENT)
	static class Key {
	}
}

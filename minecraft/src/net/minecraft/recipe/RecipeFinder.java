package net.minecraft.recipe;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;

public class RecipeFinder {
	private final RecipeMatcher<RegistryEntry<Item>> recipeMatcher = new RecipeMatcher<>();

	public void addInputIfUsable(ItemStack item) {
		if (PlayerInventory.usableWhenFillingSlot(item)) {
			this.addInput(item);
		}
	}

	public void addInput(ItemStack item) {
		this.addInput(item, item.getMaxCount());
	}

	public void addInput(ItemStack item, int maxCount) {
		if (!item.isEmpty()) {
			int i = Math.min(maxCount, item.getCount());
			this.recipeMatcher.add(item.getRegistryEntry(), i);
		}
	}

	public static RecipeMatcher.RawIngredient<RegistryEntry<Item>> sort(Stream<RegistryEntry<Item>> items) {
		List<RegistryEntry<Item>> list = items.sorted(Comparator.comparingInt(item -> Registries.ITEM.getRawId((Item)item.value()))).toList();
		return new RecipeMatcher.RawIngredient<>(list);
	}

	public boolean isCraftable(Recipe<?> recipe, @Nullable RecipeMatcher.ItemCallback<RegistryEntry<Item>> itemCallback) {
		return this.isCraftable(recipe, 1, itemCallback);
	}

	public boolean isCraftable(Recipe<?> recipe, int quantity, @Nullable RecipeMatcher.ItemCallback<RegistryEntry<Item>> itemCallback) {
		return this.recipeMatcher.match(recipe.getIngredientPlacement().getRawIngredients(), quantity, itemCallback);
	}

	public int countCrafts(Recipe<?> recipe, @Nullable RecipeMatcher.ItemCallback<RegistryEntry<Item>> itemCallback) {
		return this.countCrafts(recipe, Integer.MAX_VALUE, itemCallback);
	}

	public int countCrafts(Recipe<?> recipe, int max, @Nullable RecipeMatcher.ItemCallback<RegistryEntry<Item>> itemCallback) {
		return this.recipeMatcher.countCrafts(recipe.getIngredientPlacement().getRawIngredients(), max, itemCallback);
	}

	public void clear() {
		this.recipeMatcher.clear();
	}
}

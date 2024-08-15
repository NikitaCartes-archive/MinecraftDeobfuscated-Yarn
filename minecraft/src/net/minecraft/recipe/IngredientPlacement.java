package net.minecraft.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;

public class IngredientPlacement {
	public static final IngredientPlacement NONE = new IngredientPlacement(List.of(), List.of());
	private final List<RecipeMatcher.RawIngredient<RegistryEntry<Item>>> rawIngredients;
	private final List<Optional<IngredientPlacement.PlacementSlot>> placementSlots;

	private IngredientPlacement(
		List<RecipeMatcher.RawIngredient<RegistryEntry<Item>>> rawIngredients, List<Optional<IngredientPlacement.PlacementSlot>> placementSlots
	) {
		this.rawIngredients = rawIngredients;
		this.placementSlots = placementSlots;
	}

	private static RecipeMatcher.RawIngredient<RegistryEntry<Item>> toRawIngredient(List<ItemStack> stacks) {
		return RecipeFinder.sort(stacks.stream().map(ItemStack::getRegistryEntry));
	}

	private static List<ItemStack> getMatchingStacks(Ingredient ingredient) {
		return ingredient.getMatchingStacks().stream().map(ItemStack::new).toList();
	}

	public static IngredientPlacement forSingleSlot(Ingredient ingredient) {
		List<ItemStack> list = getMatchingStacks(ingredient);
		if (list.isEmpty()) {
			return NONE;
		} else {
			RecipeMatcher.RawIngredient<RegistryEntry<Item>> rawIngredient = toRawIngredient(list);
			IngredientPlacement.PlacementSlot placementSlot = new IngredientPlacement.PlacementSlot(list, 0);
			return new IngredientPlacement(List.of(rawIngredient), List.of(Optional.of(placementSlot)));
		}
	}

	public static IngredientPlacement forMultipleSlots(List<Optional<Ingredient>> ingredients) {
		int i = ingredients.size();
		List<RecipeMatcher.RawIngredient<RegistryEntry<Item>>> list = new ArrayList(i);
		List<Optional<IngredientPlacement.PlacementSlot>> list2 = new ArrayList(i);
		int j = 0;

		for (Optional<Ingredient> optional : ingredients) {
			if (optional.isPresent()) {
				List<ItemStack> list3 = getMatchingStacks((Ingredient)optional.get());
				if (list3.isEmpty()) {
					return NONE;
				}

				list.add(toRawIngredient(list3));
				list2.add(Optional.of(new IngredientPlacement.PlacementSlot(list3, j++)));
			} else {
				list2.add(Optional.empty());
			}
		}

		return new IngredientPlacement(list, list2);
	}

	public static IngredientPlacement forShapeless(List<Ingredient> ingredients) {
		int i = ingredients.size();
		List<RecipeMatcher.RawIngredient<RegistryEntry<Item>>> list = new ArrayList(i);
		List<Optional<IngredientPlacement.PlacementSlot>> list2 = new ArrayList(i);

		for (int j = 0; j < i; j++) {
			Ingredient ingredient = (Ingredient)ingredients.get(j);
			List<ItemStack> list3 = getMatchingStacks(ingredient);
			if (list3.isEmpty()) {
				return NONE;
			}

			list.add(toRawIngredient(list3));
			list2.add(Optional.of(new IngredientPlacement.PlacementSlot(list3, j)));
		}

		return new IngredientPlacement(list, list2);
	}

	public List<Optional<IngredientPlacement.PlacementSlot>> getPlacementSlots() {
		return this.placementSlots;
	}

	public List<RecipeMatcher.RawIngredient<RegistryEntry<Item>>> getRawIngredients() {
		return this.rawIngredients;
	}

	public boolean hasNoPlacement() {
		return this.placementSlots.isEmpty();
	}

	public static record PlacementSlot(List<ItemStack> possibleItems, int placerOutputPosition) {
		public PlacementSlot(List<ItemStack> possibleItems, int placerOutputPosition) {
			if (possibleItems.isEmpty()) {
				throw new IllegalArgumentException("Possible items list must be not empty");
			} else {
				this.possibleItems = possibleItems;
				this.placerOutputPosition = placerOutputPosition;
			}
		}
	}
}

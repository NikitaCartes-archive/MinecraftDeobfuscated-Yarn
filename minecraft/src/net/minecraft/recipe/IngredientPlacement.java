package net.minecraft.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.item.Item;
import net.minecraft.registry.entry.RegistryEntry;

public class IngredientPlacement {
	public static final IngredientPlacement NONE = new IngredientPlacement(List.of(), List.of(), List.of());
	private final List<Ingredient> ingredients;
	private final List<RecipeMatcher.RawIngredient<RegistryEntry<Item>>> rawIngredients;
	private final List<Optional<IngredientPlacement.PlacementSlot>> placementSlots;

	private IngredientPlacement(
		List<Ingredient> ingredients,
		List<RecipeMatcher.RawIngredient<RegistryEntry<Item>>> rawIngredients,
		List<Optional<IngredientPlacement.PlacementSlot>> placementSlots
	) {
		this.ingredients = ingredients;
		this.rawIngredients = rawIngredients;
		this.placementSlots = placementSlots;
	}

	public static RecipeMatcher.RawIngredient<RegistryEntry<Item>> sort(Ingredient ingredient) {
		return RecipeFinder.sort(ingredient.getMatchingStacks().stream());
	}

	public static IngredientPlacement forSingleSlot(Ingredient ingredient) {
		if (ingredient.getMatchingStacks().isEmpty()) {
			return NONE;
		} else {
			RecipeMatcher.RawIngredient<RegistryEntry<Item>> rawIngredient = sort(ingredient);
			IngredientPlacement.PlacementSlot placementSlot = new IngredientPlacement.PlacementSlot(0);
			return new IngredientPlacement(List.of(ingredient), List.of(rawIngredient), List.of(Optional.of(placementSlot)));
		}
	}

	public static IngredientPlacement forMultipleSlots(List<Optional<Ingredient>> ingredients) {
		int i = ingredients.size();
		List<Ingredient> list = new ArrayList(i);
		List<RecipeMatcher.RawIngredient<RegistryEntry<Item>>> list2 = new ArrayList(i);
		List<Optional<IngredientPlacement.PlacementSlot>> list3 = new ArrayList(i);
		int j = 0;

		for (Optional<Ingredient> optional : ingredients) {
			if (optional.isPresent()) {
				Ingredient ingredient = (Ingredient)optional.get();
				if (ingredient.getMatchingStacks().isEmpty()) {
					return NONE;
				}

				list.add(ingredient);
				list2.add(sort(ingredient));
				list3.add(Optional.of(new IngredientPlacement.PlacementSlot(j++)));
			} else {
				list3.add(Optional.empty());
			}
		}

		return new IngredientPlacement(list, list2, list3);
	}

	public static IngredientPlacement forShapeless(List<Ingredient> ingredients) {
		int i = ingredients.size();
		List<RecipeMatcher.RawIngredient<RegistryEntry<Item>>> list = new ArrayList(i);
		List<Optional<IngredientPlacement.PlacementSlot>> list2 = new ArrayList(i);

		for (int j = 0; j < i; j++) {
			Ingredient ingredient = (Ingredient)ingredients.get(j);
			if (ingredient.getMatchingStacks().isEmpty()) {
				return NONE;
			}

			list.add(sort(ingredient));
			list2.add(Optional.of(new IngredientPlacement.PlacementSlot(j)));
		}

		return new IngredientPlacement(ingredients, list, list2);
	}

	public List<Optional<IngredientPlacement.PlacementSlot>> getPlacementSlots() {
		return this.placementSlots;
	}

	public List<Ingredient> getIngredients() {
		return this.ingredients;
	}

	public List<RecipeMatcher.RawIngredient<RegistryEntry<Item>>> getRawIngredients() {
		return this.rawIngredients;
	}

	public boolean hasNoPlacement() {
		return this.placementSlots.isEmpty();
	}

	public static record PlacementSlot(int placerOutputPosition) {
	}
}

package net.minecraft.client.recipebook;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Table;
import com.google.common.collect.ImmutableList.Builder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.recipe.NetworkRecipeId;
import net.minecraft.recipe.RecipeDisplayEntry;
import net.minecraft.recipe.book.RecipeBook;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.book.RecipeBookGroup;

@Environment(EnvType.CLIENT)
public class ClientRecipeBook extends RecipeBook {
	private final Map<NetworkRecipeId, RecipeDisplayEntry> recipes = new HashMap();
	private final Set<NetworkRecipeId> highlightedRecipes = new HashSet();
	private Map<RecipeBookGroup, List<RecipeResultCollection>> resultsByCategory = Map.of();
	private List<RecipeResultCollection> orderedResults = List.of();

	public void add(RecipeDisplayEntry entry) {
		this.recipes.put(entry.id(), entry);
	}

	public void remove(NetworkRecipeId recipeId) {
		this.recipes.remove(recipeId);
		this.highlightedRecipes.remove(recipeId);
	}

	public void clear() {
		this.recipes.clear();
		this.highlightedRecipes.clear();
	}

	public boolean isHighlighted(NetworkRecipeId recipeId) {
		return this.highlightedRecipes.contains(recipeId);
	}

	public void unmarkHighlighted(NetworkRecipeId recipeId) {
		this.highlightedRecipes.remove(recipeId);
	}

	public void markHighlighted(NetworkRecipeId recipeId) {
		this.highlightedRecipes.add(recipeId);
	}

	public void refresh() {
		Map<RecipeBookCategory, List<List<RecipeDisplayEntry>>> map = toGroupedMap(this.recipes.values());
		Map<RecipeBookGroup, List<RecipeResultCollection>> map2 = new HashMap();
		Builder<RecipeResultCollection> builder = ImmutableList.builder();
		map.forEach(
			(group, resultCollections) -> map2.put(
					group, (List)resultCollections.stream().map(RecipeResultCollection::new).peek(builder::add).collect(ImmutableList.toImmutableList())
				)
		);

		for (RecipeBookType recipeBookType : RecipeBookType.values()) {
			map2.put(
				recipeBookType,
				(List)recipeBookType.getCategories()
					.stream()
					.flatMap(group -> ((List)map2.getOrDefault(group, List.of())).stream())
					.collect(ImmutableList.toImmutableList())
			);
		}

		this.resultsByCategory = Map.copyOf(map2);
		this.orderedResults = builder.build();
	}

	private static Map<RecipeBookCategory, List<List<RecipeDisplayEntry>>> toGroupedMap(Iterable<RecipeDisplayEntry> recipes) {
		Map<RecipeBookCategory, List<List<RecipeDisplayEntry>>> map = new HashMap();
		Table<RecipeBookCategory, Integer, List<RecipeDisplayEntry>> table = HashBasedTable.create();

		for (RecipeDisplayEntry recipeDisplayEntry : recipes) {
			RecipeBookCategory recipeBookCategory = recipeDisplayEntry.category();
			OptionalInt optionalInt = recipeDisplayEntry.group();
			if (optionalInt.isEmpty()) {
				((List)map.computeIfAbsent(recipeBookCategory, group -> new ArrayList())).add(List.of(recipeDisplayEntry));
			} else {
				List<RecipeDisplayEntry> list = table.get(recipeBookCategory, optionalInt.getAsInt());
				if (list == null) {
					list = new ArrayList();
					table.put(recipeBookCategory, optionalInt.getAsInt(), list);
					((List)map.computeIfAbsent(recipeBookCategory, group -> new ArrayList())).add(list);
				}

				list.add(recipeDisplayEntry);
			}
		}

		return map;
	}

	public List<RecipeResultCollection> getOrderedResults() {
		return this.orderedResults;
	}

	public List<RecipeResultCollection> getResultsForCategory(RecipeBookGroup category) {
		return (List<RecipeResultCollection>)this.resultsByCategory.getOrDefault(category, Collections.emptyList());
	}
}

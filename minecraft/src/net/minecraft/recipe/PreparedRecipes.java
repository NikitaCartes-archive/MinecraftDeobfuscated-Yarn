package net.minecraft.recipe;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;

public class PreparedRecipes {
	public static final PreparedRecipes EMPTY = new PreparedRecipes(ImmutableMultimap.of(), Map.of());
	private final Multimap<RecipeType<?>, RecipeEntry<?>> byType;
	private final Map<RegistryKey<Recipe<?>>, RecipeEntry<?>> byKey;

	private PreparedRecipes(Multimap<RecipeType<?>, RecipeEntry<?>> byType, Map<RegistryKey<Recipe<?>>, RecipeEntry<?>> byKey) {
		this.byType = byType;
		this.byKey = byKey;
	}

	public static PreparedRecipes of(Iterable<RecipeEntry<?>> recipes) {
		Builder<RecipeType<?>, RecipeEntry<?>> builder = ImmutableMultimap.builder();
		com.google.common.collect.ImmutableMap.Builder<RegistryKey<Recipe<?>>, RecipeEntry<?>> builder2 = ImmutableMap.builder();

		for (RecipeEntry<?> recipeEntry : recipes) {
			builder.put(recipeEntry.value().getType(), recipeEntry);
			builder2.put(recipeEntry.id(), recipeEntry);
		}

		return new PreparedRecipes(builder.build(), builder2.build());
	}

	public <I extends RecipeInput, T extends Recipe<I>> Collection<RecipeEntry<T>> getAll(RecipeType<T> type) {
		return (Collection<RecipeEntry<T>>)this.byType.get(type);
	}

	public Collection<RecipeEntry<?>> recipes() {
		return this.byKey.values();
	}

	@Nullable
	public RecipeEntry<?> get(RegistryKey<Recipe<?>> key) {
		return (RecipeEntry<?>)this.byKey.get(key);
	}

	public <I extends RecipeInput, T extends Recipe<I>> Stream<RecipeEntry<T>> find(RecipeType<T> type, I input, World world) {
		return input.isEmpty() ? Stream.empty() : this.getAll(type).stream().filter(entry -> entry.value().matches(input, world));
	}
}

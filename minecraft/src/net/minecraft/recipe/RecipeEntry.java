package net.minecraft.recipe;

import net.minecraft.util.Identifier;

public record RecipeEntry<T extends Recipe<?>>(Identifier id, T value) {
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else {
			if (o instanceof RecipeEntry<?> recipeEntry && this.id.equals(recipeEntry.id)) {
				return true;
			}

			return false;
		}
	}

	public int hashCode() {
		return this.id.hashCode();
	}

	public String toString() {
		return this.id.toString();
	}
}

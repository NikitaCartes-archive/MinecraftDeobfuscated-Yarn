package net.minecraft.recipe.book;

public enum RecipeCategory {
	BUILDING_BLOCKS("building_blocks"),
	DECORATIONS("decorations"),
	REDSTONE("redstone"),
	TRANSPORTATION("transportation"),
	TOOLS("tools"),
	COMBAT("combat"),
	FOOD("food"),
	BREWING("brewing"),
	MISC("misc");

	private final String name;

	private RecipeCategory(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
}

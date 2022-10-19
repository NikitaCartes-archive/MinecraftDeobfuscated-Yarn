package net.minecraft.recipe.book;

import net.minecraft.util.StringIdentifiable;

public enum CookingRecipeCategory implements StringIdentifiable {
	FOOD("food"),
	BLOCKS("blocks"),
	MISC("misc");

	public static final StringIdentifiable.Codec<CookingRecipeCategory> CODEC = StringIdentifiable.createCodec(CookingRecipeCategory::values);
	private final String id;

	private CookingRecipeCategory(String id) {
		this.id = id;
	}

	@Override
	public String asString() {
		return this.id;
	}
}

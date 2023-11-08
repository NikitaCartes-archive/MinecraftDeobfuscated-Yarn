package net.minecraft.recipe.book;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

public enum CraftingRecipeCategory implements StringIdentifiable {
	BUILDING("building"),
	REDSTONE("redstone"),
	EQUIPMENT("equipment"),
	MISC("misc");

	public static final Codec<CraftingRecipeCategory> CODEC = StringIdentifiable.createCodec(CraftingRecipeCategory::values);
	private final String id;

	private CraftingRecipeCategory(String id) {
		this.id = id;
	}

	@Override
	public String asString() {
		return this.id;
	}
}

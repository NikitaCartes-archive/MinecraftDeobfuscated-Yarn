package net.minecraft.recipe;

import com.google.gson.JsonObject;
import net.minecraft.class_4317;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;

public interface RecipeSerializer<T extends Recipe<?>> {
	RecipeSerializer<ShapedRecipe> SHAPED = register("crafting_shaped", new ShapedRecipe.Serializer());
	RecipeSerializer<ShapelessRecipe> SHAPELESS = register("crafting_shapeless", new ShapelessRecipe.Serializer());
	SpecialRecipeSerializer<ArmorDyeRecipe> field_9028 = register("crafting_special_armordye", new SpecialRecipeSerializer<>(ArmorDyeRecipe::new));
	SpecialRecipeSerializer<BookCloningRecipe> field_9029 = register("crafting_special_bookcloning", new SpecialRecipeSerializer<>(BookCloningRecipe::new));
	SpecialRecipeSerializer<MapCloningRecipe> field_9044 = register("crafting_special_mapcloning", new SpecialRecipeSerializer<>(MapCloningRecipe::new));
	SpecialRecipeSerializer<MapExtendingRecipe> field_9039 = register("crafting_special_mapextending", new SpecialRecipeSerializer<>(MapExtendingRecipe::new));
	SpecialRecipeSerializer<FireworkRocketRecipe> field_9043 = register(
		"crafting_special_firework_rocket", new SpecialRecipeSerializer<>(FireworkRocketRecipe::new)
	);
	SpecialRecipeSerializer<FireworkStarRecipe> field_9036 = register("crafting_special_firework_star", new SpecialRecipeSerializer<>(FireworkStarRecipe::new));
	SpecialRecipeSerializer<FireworkStarFadeRecipe> field_9034 = register(
		"crafting_special_firework_star_fade", new SpecialRecipeSerializer<>(FireworkStarFadeRecipe::new)
	);
	SpecialRecipeSerializer<TippedArrowRecipe> field_9037 = register("crafting_special_tippedarrow", new SpecialRecipeSerializer<>(TippedArrowRecipe::new));
	SpecialRecipeSerializer<BannerDuplicateRecipe> field_9038 = register(
		"crafting_special_bannerduplicate", new SpecialRecipeSerializer<>(BannerDuplicateRecipe::new)
	);
	SpecialRecipeSerializer<ShieldDecorationRecipe> field_9040 = register(
		"crafting_special_shielddecoration", new SpecialRecipeSerializer<>(ShieldDecorationRecipe::new)
	);
	SpecialRecipeSerializer<ShulkerBoxColoringRecipe> field_9041 = register(
		"crafting_special_shulkerboxcoloring", new SpecialRecipeSerializer<>(ShulkerBoxColoringRecipe::new)
	);
	SpecialRecipeSerializer<SuspiciousStewRecipe> field_9030 = register(
		"crafting_special_suspiciousstew", new SpecialRecipeSerializer<>(SuspiciousStewRecipe::new)
	);
	SpecialRecipeSerializer<class_4317> field_19421 = register("crafting_special_repairitem", new SpecialRecipeSerializer<>(class_4317::new));
	CookingRecipeSerializer<SmeltingRecipe> field_9042 = register("smelting", new CookingRecipeSerializer<>(SmeltingRecipe::new, 200));
	CookingRecipeSerializer<BlastingRecipe> field_17084 = register("blasting", new CookingRecipeSerializer<>(BlastingRecipe::new, 100));
	CookingRecipeSerializer<SmokingRecipe> field_17085 = register("smoking", new CookingRecipeSerializer<>(SmokingRecipe::new, 100));
	CookingRecipeSerializer<CampfireCookingRecipe> field_17347 = register("campfire_cooking", new CookingRecipeSerializer<>(CampfireCookingRecipe::new, 100));
	RecipeSerializer<StonecuttingRecipe> field_17640 = register("stonecutting", new CuttingRecipe.Serializer<>(StonecuttingRecipe::new));

	T read(Identifier identifier, JsonObject jsonObject);

	T read(Identifier identifier, PacketByteBuf packetByteBuf);

	void write(PacketByteBuf packetByteBuf, T recipe);

	static <S extends RecipeSerializer<T>, T extends Recipe<?>> S register(String string, S recipeSerializer) {
		return Registry.register(Registry.RECIPE_SERIALIZER, string, recipeSerializer);
	}
}

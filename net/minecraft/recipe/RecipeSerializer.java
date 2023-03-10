/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.recipe;

import com.google.gson.JsonObject;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.ArmorDyeRecipe;
import net.minecraft.recipe.BannerDuplicateRecipe;
import net.minecraft.recipe.BlastingRecipe;
import net.minecraft.recipe.BookCloningRecipe;
import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.recipe.CookingRecipeSerializer;
import net.minecraft.recipe.CraftingDecoratedPotRecipe;
import net.minecraft.recipe.CuttingRecipe;
import net.minecraft.recipe.FireworkRocketRecipe;
import net.minecraft.recipe.FireworkStarFadeRecipe;
import net.minecraft.recipe.FireworkStarRecipe;
import net.minecraft.recipe.LegacySmithingRecipe;
import net.minecraft.recipe.MapCloningRecipe;
import net.minecraft.recipe.MapExtendingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RepairItemRecipe;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.recipe.ShieldDecorationRecipe;
import net.minecraft.recipe.ShulkerBoxColoringRecipe;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.recipe.SmithingTransformRecipe;
import net.minecraft.recipe.SmithingTrimRecipe;
import net.minecraft.recipe.SmokingRecipe;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.recipe.SuspiciousStewRecipe;
import net.minecraft.recipe.TippedArrowRecipe;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * The recipe serializer controls the serialization and deserialization of
 * recipe content. The caller should handle the serialization of recipes' IDs.
 * 
 * <p>Even though they are referred to by the {@code type} field in recipe
 * JSON format, they are stored in a registry with key
 * {@code minecraft:root/minecraft:recipe_serializer}, and is hence named.
 * 
 * <p>If a recipe's serializer exists only on the server but not on the
 * client, the client will disconnect upon receiving the recipe; if a
 * recipe JSON intended for an absent recipe serializer is read, it is
 * skipped.
 */
public interface RecipeSerializer<T extends Recipe<?>> {
    public static final RecipeSerializer<ShapedRecipe> SHAPED = RecipeSerializer.register("crafting_shaped", new ShapedRecipe.Serializer());
    public static final RecipeSerializer<ShapelessRecipe> SHAPELESS = RecipeSerializer.register("crafting_shapeless", new ShapelessRecipe.Serializer());
    public static final RecipeSerializer<ArmorDyeRecipe> ARMOR_DYE = RecipeSerializer.register("crafting_special_armordye", new SpecialRecipeSerializer<ArmorDyeRecipe>(ArmorDyeRecipe::new));
    public static final RecipeSerializer<BookCloningRecipe> BOOK_CLONING = RecipeSerializer.register("crafting_special_bookcloning", new SpecialRecipeSerializer<BookCloningRecipe>(BookCloningRecipe::new));
    public static final RecipeSerializer<MapCloningRecipe> MAP_CLONING = RecipeSerializer.register("crafting_special_mapcloning", new SpecialRecipeSerializer<MapCloningRecipe>(MapCloningRecipe::new));
    public static final RecipeSerializer<MapExtendingRecipe> MAP_EXTENDING = RecipeSerializer.register("crafting_special_mapextending", new SpecialRecipeSerializer<MapExtendingRecipe>(MapExtendingRecipe::new));
    public static final RecipeSerializer<FireworkRocketRecipe> FIREWORK_ROCKET = RecipeSerializer.register("crafting_special_firework_rocket", new SpecialRecipeSerializer<FireworkRocketRecipe>(FireworkRocketRecipe::new));
    public static final RecipeSerializer<FireworkStarRecipe> FIREWORK_STAR = RecipeSerializer.register("crafting_special_firework_star", new SpecialRecipeSerializer<FireworkStarRecipe>(FireworkStarRecipe::new));
    public static final RecipeSerializer<FireworkStarFadeRecipe> FIREWORK_STAR_FADE = RecipeSerializer.register("crafting_special_firework_star_fade", new SpecialRecipeSerializer<FireworkStarFadeRecipe>(FireworkStarFadeRecipe::new));
    public static final RecipeSerializer<TippedArrowRecipe> TIPPED_ARROW = RecipeSerializer.register("crafting_special_tippedarrow", new SpecialRecipeSerializer<TippedArrowRecipe>(TippedArrowRecipe::new));
    public static final RecipeSerializer<BannerDuplicateRecipe> BANNER_DUPLICATE = RecipeSerializer.register("crafting_special_bannerduplicate", new SpecialRecipeSerializer<BannerDuplicateRecipe>(BannerDuplicateRecipe::new));
    public static final RecipeSerializer<ShieldDecorationRecipe> SHIELD_DECORATION = RecipeSerializer.register("crafting_special_shielddecoration", new SpecialRecipeSerializer<ShieldDecorationRecipe>(ShieldDecorationRecipe::new));
    public static final RecipeSerializer<ShulkerBoxColoringRecipe> SHULKER_BOX = RecipeSerializer.register("crafting_special_shulkerboxcoloring", new SpecialRecipeSerializer<ShulkerBoxColoringRecipe>(ShulkerBoxColoringRecipe::new));
    public static final RecipeSerializer<SuspiciousStewRecipe> SUSPICIOUS_STEW = RecipeSerializer.register("crafting_special_suspiciousstew", new SpecialRecipeSerializer<SuspiciousStewRecipe>(SuspiciousStewRecipe::new));
    public static final RecipeSerializer<RepairItemRecipe> REPAIR_ITEM = RecipeSerializer.register("crafting_special_repairitem", new SpecialRecipeSerializer<RepairItemRecipe>(RepairItemRecipe::new));
    public static final RecipeSerializer<SmeltingRecipe> SMELTING = RecipeSerializer.register("smelting", new CookingRecipeSerializer<SmeltingRecipe>(SmeltingRecipe::new, 200));
    public static final RecipeSerializer<BlastingRecipe> BLASTING = RecipeSerializer.register("blasting", new CookingRecipeSerializer<BlastingRecipe>(BlastingRecipe::new, 100));
    public static final RecipeSerializer<SmokingRecipe> SMOKING = RecipeSerializer.register("smoking", new CookingRecipeSerializer<SmokingRecipe>(SmokingRecipe::new, 100));
    public static final RecipeSerializer<CampfireCookingRecipe> CAMPFIRE_COOKING = RecipeSerializer.register("campfire_cooking", new CookingRecipeSerializer<CampfireCookingRecipe>(CampfireCookingRecipe::new, 100));
    public static final RecipeSerializer<StonecuttingRecipe> STONECUTTING = RecipeSerializer.register("stonecutting", new CuttingRecipe.Serializer<StonecuttingRecipe>(StonecuttingRecipe::new));
    public static final RecipeSerializer<LegacySmithingRecipe> SMITHING = RecipeSerializer.register("smithing", new LegacySmithingRecipe.Serializer());
    public static final RecipeSerializer<SmithingTransformRecipe> SMITHING_TRANSFORM = RecipeSerializer.register("smithing_transform", new SmithingTransformRecipe.Serializer());
    public static final RecipeSerializer<SmithingTrimRecipe> SMITHING_TRIM = RecipeSerializer.register("smithing_trim", new SmithingTrimRecipe.Serializer());
    public static final RecipeSerializer<CraftingDecoratedPotRecipe> CRAFTING_DECORATED_POT = RecipeSerializer.register("crafting_decorated_pot", new SpecialRecipeSerializer<CraftingDecoratedPotRecipe>(CraftingDecoratedPotRecipe::new));

    /**
     * Reads a recipe from a JSON object.
     * 
     * @implNote If this throws any exception besides {@link com.google.gson.JsonParseException}
     * and {@link IllegalArgumentException}, it will terminate and affect loading
     * of all recipes from data packs beyond the current recipe.
     * 
     * @throws com.google.gson.JsonParseException if the recipe JSON is incorrect
     * @return the read recipe
     * 
     * @param json the recipe JSON
     * @param id the recipe's ID
     */
    public T read(Identifier var1, JsonObject var2);

    /**
     * Reads a recipe from a packet byte buf, usually on the client.
     * 
     * <p>This can throw whatever exception the packet byte buf throws. This may be
     * called in the netty event loop than the client game engine thread.
     * 
     * @return the read recipe
     * 
     * @param buf the recipe buf
     * @param id the recipe's ID
     */
    public T read(Identifier var1, PacketByteBuf var2);

    /**
     * Writes a recipe to a packet byte buf, usually on the server.
     * 
     * <p>The recipe's ID is already written into the buf when this is called.
     * 
     * <p>This can throw whatever exception the packet byte buf throws. This may be
     * called in the netty event loop than the server game engine thread.
     * 
     * @param buf the recipe buf
     * @param recipe the recipe
     */
    public void write(PacketByteBuf var1, T var2);

    public static <S extends RecipeSerializer<T>, T extends Recipe<?>> S register(String id, S serializer) {
        return (S)Registry.register(Registries.RECIPE_SERIALIZER, id, serializer);
    }
}


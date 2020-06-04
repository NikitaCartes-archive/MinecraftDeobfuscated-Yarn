/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.recipe;

import com.google.gson.JsonObject;
import net.minecraft.class_5357;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.ArmorDyeRecipe;
import net.minecraft.recipe.BannerDuplicateRecipe;
import net.minecraft.recipe.BlastingRecipe;
import net.minecraft.recipe.BookCloningRecipe;
import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.recipe.CookingRecipeSerializer;
import net.minecraft.recipe.CuttingRecipe;
import net.minecraft.recipe.FireworkRocketRecipe;
import net.minecraft.recipe.FireworkStarFadeRecipe;
import net.minecraft.recipe.FireworkStarRecipe;
import net.minecraft.recipe.MapCloningRecipe;
import net.minecraft.recipe.MapExtendingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RepairItemRecipe;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.recipe.ShieldDecorationRecipe;
import net.minecraft.recipe.ShulkerBoxColoringRecipe;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.recipe.SmokingRecipe;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.recipe.SuspiciousStewRecipe;
import net.minecraft.recipe.TippedArrowRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public interface RecipeSerializer<T extends Recipe<?>> {
    public static final RecipeSerializer<ShapedRecipe> SHAPED = RecipeSerializer.register("crafting_shaped", new ShapedRecipe.Serializer());
    public static final RecipeSerializer<ShapelessRecipe> SHAPELESS = RecipeSerializer.register("crafting_shapeless", new ShapelessRecipe.Serializer());
    public static final SpecialRecipeSerializer<ArmorDyeRecipe> ARMOR_DYE = RecipeSerializer.register("crafting_special_armordye", new SpecialRecipeSerializer<ArmorDyeRecipe>(ArmorDyeRecipe::new));
    public static final SpecialRecipeSerializer<BookCloningRecipe> BOOK_CLONING = RecipeSerializer.register("crafting_special_bookcloning", new SpecialRecipeSerializer<BookCloningRecipe>(BookCloningRecipe::new));
    public static final SpecialRecipeSerializer<MapCloningRecipe> MAP_CLONING = RecipeSerializer.register("crafting_special_mapcloning", new SpecialRecipeSerializer<MapCloningRecipe>(MapCloningRecipe::new));
    public static final SpecialRecipeSerializer<MapExtendingRecipe> MAP_EXTENDING = RecipeSerializer.register("crafting_special_mapextending", new SpecialRecipeSerializer<MapExtendingRecipe>(MapExtendingRecipe::new));
    public static final SpecialRecipeSerializer<FireworkRocketRecipe> FIREWORK_ROCKET = RecipeSerializer.register("crafting_special_firework_rocket", new SpecialRecipeSerializer<FireworkRocketRecipe>(FireworkRocketRecipe::new));
    public static final SpecialRecipeSerializer<FireworkStarRecipe> FIREWORK_STAR = RecipeSerializer.register("crafting_special_firework_star", new SpecialRecipeSerializer<FireworkStarRecipe>(FireworkStarRecipe::new));
    public static final SpecialRecipeSerializer<FireworkStarFadeRecipe> FIREWORK_STAR_FADE = RecipeSerializer.register("crafting_special_firework_star_fade", new SpecialRecipeSerializer<FireworkStarFadeRecipe>(FireworkStarFadeRecipe::new));
    public static final SpecialRecipeSerializer<TippedArrowRecipe> TIPPED_ARROW = RecipeSerializer.register("crafting_special_tippedarrow", new SpecialRecipeSerializer<TippedArrowRecipe>(TippedArrowRecipe::new));
    public static final SpecialRecipeSerializer<BannerDuplicateRecipe> BANNER_DUPLICATE = RecipeSerializer.register("crafting_special_bannerduplicate", new SpecialRecipeSerializer<BannerDuplicateRecipe>(BannerDuplicateRecipe::new));
    public static final SpecialRecipeSerializer<ShieldDecorationRecipe> SHIELD_DECORATION = RecipeSerializer.register("crafting_special_shielddecoration", new SpecialRecipeSerializer<ShieldDecorationRecipe>(ShieldDecorationRecipe::new));
    public static final SpecialRecipeSerializer<ShulkerBoxColoringRecipe> SHULKER_BOX = RecipeSerializer.register("crafting_special_shulkerboxcoloring", new SpecialRecipeSerializer<ShulkerBoxColoringRecipe>(ShulkerBoxColoringRecipe::new));
    public static final SpecialRecipeSerializer<SuspiciousStewRecipe> SUSPICIOUS_STEW = RecipeSerializer.register("crafting_special_suspiciousstew", new SpecialRecipeSerializer<SuspiciousStewRecipe>(SuspiciousStewRecipe::new));
    public static final SpecialRecipeSerializer<RepairItemRecipe> REPAIR_ITEM = RecipeSerializer.register("crafting_special_repairitem", new SpecialRecipeSerializer<RepairItemRecipe>(RepairItemRecipe::new));
    public static final CookingRecipeSerializer<SmeltingRecipe> SMELTING = RecipeSerializer.register("smelting", new CookingRecipeSerializer<SmeltingRecipe>(SmeltingRecipe::new, 200));
    public static final CookingRecipeSerializer<BlastingRecipe> BLASTING = RecipeSerializer.register("blasting", new CookingRecipeSerializer<BlastingRecipe>(BlastingRecipe::new, 100));
    public static final CookingRecipeSerializer<SmokingRecipe> SMOKING = RecipeSerializer.register("smoking", new CookingRecipeSerializer<SmokingRecipe>(SmokingRecipe::new, 100));
    public static final CookingRecipeSerializer<CampfireCookingRecipe> CAMPFIRE_COOKING = RecipeSerializer.register("campfire_cooking", new CookingRecipeSerializer<CampfireCookingRecipe>(CampfireCookingRecipe::new, 100));
    public static final RecipeSerializer<StonecuttingRecipe> STONECUTTING = RecipeSerializer.register("stonecutting", new CuttingRecipe.Serializer<StonecuttingRecipe>(StonecuttingRecipe::new));
    public static final RecipeSerializer<class_5357> SMITHING = RecipeSerializer.register("smithing", new class_5357.class_5358());

    public T read(Identifier var1, JsonObject var2);

    public T read(Identifier var1, PacketByteBuf var2);

    public void write(PacketByteBuf var1, T var2);

    public static <S extends RecipeSerializer<T>, T extends Recipe<?>> S register(String id, S serializer) {
        return (S)Registry.register(Registry.RECIPE_SERIALIZER, id, serializer);
    }
}


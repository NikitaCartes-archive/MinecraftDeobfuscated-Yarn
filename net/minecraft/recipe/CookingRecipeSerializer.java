/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Objects;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.book.CookingRecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class CookingRecipeSerializer<T extends AbstractCookingRecipe>
implements RecipeSerializer<T> {
    private final int cookingTime;
    private final RecipeFactory<T> recipeFactory;

    public CookingRecipeSerializer(RecipeFactory<T> recipeFactory, int cookingTime) {
        this.cookingTime = cookingTime;
        this.recipeFactory = recipeFactory;
    }

    @Override
    public T read(Identifier identifier, JsonObject jsonObject) {
        String string = JsonHelper.getString(jsonObject, "group", "");
        CookingRecipeCategory cookingRecipeCategory = Objects.requireNonNullElse(CookingRecipeCategory.CODEC.byId(JsonHelper.getString(jsonObject, "category", null)), CookingRecipeCategory.MISC);
        JsonElement jsonElement = JsonHelper.hasArray(jsonObject, "ingredient") ? JsonHelper.getArray(jsonObject, "ingredient") : JsonHelper.getObject(jsonObject, "ingredient");
        Ingredient ingredient = Ingredient.fromJson(jsonElement);
        String string2 = JsonHelper.getString(jsonObject, "result");
        Identifier identifier2 = new Identifier(string2);
        ItemStack itemStack = new ItemStack((ItemConvertible)Registries.ITEM.getOrEmpty(identifier2).orElseThrow(() -> new IllegalStateException("Item: " + string2 + " does not exist")));
        float f = JsonHelper.getFloat(jsonObject, "experience", 0.0f);
        int i = JsonHelper.getInt(jsonObject, "cookingtime", this.cookingTime);
        return this.recipeFactory.create(identifier, string, cookingRecipeCategory, ingredient, itemStack, f, i);
    }

    @Override
    public T read(Identifier identifier, PacketByteBuf packetByteBuf) {
        String string = packetByteBuf.readString();
        CookingRecipeCategory cookingRecipeCategory = packetByteBuf.readEnumConstant(CookingRecipeCategory.class);
        Ingredient ingredient = Ingredient.fromPacket(packetByteBuf);
        ItemStack itemStack = packetByteBuf.readItemStack();
        float f = packetByteBuf.readFloat();
        int i = packetByteBuf.readVarInt();
        return this.recipeFactory.create(identifier, string, cookingRecipeCategory, ingredient, itemStack, f, i);
    }

    @Override
    public void write(PacketByteBuf packetByteBuf, T abstractCookingRecipe) {
        packetByteBuf.writeString(((AbstractCookingRecipe)abstractCookingRecipe).group);
        packetByteBuf.writeEnumConstant(((AbstractCookingRecipe)abstractCookingRecipe).getCategory());
        ((AbstractCookingRecipe)abstractCookingRecipe).input.write(packetByteBuf);
        packetByteBuf.writeItemStack(((AbstractCookingRecipe)abstractCookingRecipe).output);
        packetByteBuf.writeFloat(((AbstractCookingRecipe)abstractCookingRecipe).experience);
        packetByteBuf.writeVarInt(((AbstractCookingRecipe)abstractCookingRecipe).cookTime);
    }

    @Override
    public /* synthetic */ Recipe read(Identifier id, PacketByteBuf buf) {
        return this.read(id, buf);
    }

    @Override
    public /* synthetic */ Recipe read(Identifier id, JsonObject json) {
        return this.read(id, json);
    }

    static interface RecipeFactory<T extends AbstractCookingRecipe> {
        public T create(Identifier var1, String var2, CookingRecipeCategory var3, Ingredient var4, ItemStack var5, float var6, int var7);
    }
}


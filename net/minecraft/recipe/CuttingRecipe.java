/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.recipe;

import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;

public abstract class CuttingRecipe
implements Recipe<Inventory> {
    protected final Ingredient input;
    protected final ItemStack output;
    private final RecipeType<?> type;
    private final RecipeSerializer<?> serializer;
    protected final Identifier id;
    protected final String group;

    public CuttingRecipe(RecipeType<?> recipeType, RecipeSerializer<?> recipeSerializer, Identifier identifier, String string, Ingredient ingredient, ItemStack itemStack) {
        this.type = recipeType;
        this.serializer = recipeSerializer;
        this.id = identifier;
        this.group = string;
        this.input = ingredient;
        this.output = itemStack;
    }

    @Override
    public RecipeType<?> getType() {
        return this.type;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return this.serializer;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public String getGroup() {
        return this.group;
    }

    @Override
    public ItemStack getOutput() {
        return this.output;
    }

    @Override
    public DefaultedList<Ingredient> getPreviewInputs() {
        DefaultedList<Ingredient> defaultedList = DefaultedList.of();
        defaultedList.add(this.input);
        return defaultedList;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean fits(int i, int j) {
        return true;
    }

    @Override
    public ItemStack craft(Inventory inventory) {
        return this.output.copy();
    }

    public static class Serializer<T extends CuttingRecipe>
    implements RecipeSerializer<T> {
        final RecipeFactory<T> recipeFactory;

        protected Serializer(RecipeFactory<T> recipeFactory) {
            this.recipeFactory = recipeFactory;
        }

        public T method_17881(Identifier identifier, JsonObject jsonObject) {
            String string = JsonHelper.getString(jsonObject, "group", "");
            Ingredient ingredient = JsonHelper.hasArray(jsonObject, "ingredient") ? Ingredient.fromJson(JsonHelper.getArray(jsonObject, "ingredient")) : Ingredient.fromJson(JsonHelper.getObject(jsonObject, "ingredient"));
            String string2 = JsonHelper.getString(jsonObject, "result");
            int i = JsonHelper.getInt(jsonObject, "count");
            ItemStack itemStack = new ItemStack(Registry.ITEM.get(new Identifier(string2)), i);
            return this.recipeFactory.create(identifier, string, ingredient, itemStack);
        }

        public T method_17882(Identifier identifier, PacketByteBuf packetByteBuf) {
            String string = packetByteBuf.readString(Short.MAX_VALUE);
            Ingredient ingredient = Ingredient.fromPacket(packetByteBuf);
            ItemStack itemStack = packetByteBuf.readItemStack();
            return this.recipeFactory.create(identifier, string, ingredient, itemStack);
        }

        public void method_17880(PacketByteBuf packetByteBuf, T cuttingRecipe) {
            packetByteBuf.writeString(((CuttingRecipe)cuttingRecipe).group);
            ((CuttingRecipe)cuttingRecipe).input.write(packetByteBuf);
            packetByteBuf.writeItemStack(((CuttingRecipe)cuttingRecipe).output);
        }

        @Override
        public /* synthetic */ Recipe read(Identifier identifier, PacketByteBuf packetByteBuf) {
            return this.method_17882(identifier, packetByteBuf);
        }

        @Override
        public /* synthetic */ Recipe read(Identifier identifier, JsonObject jsonObject) {
            return this.method_17881(identifier, jsonObject);
        }

        static interface RecipeFactory<T extends CuttingRecipe> {
            public T create(Identifier var1, String var2, Ingredient var3, ItemStack var4);
        }
    }
}


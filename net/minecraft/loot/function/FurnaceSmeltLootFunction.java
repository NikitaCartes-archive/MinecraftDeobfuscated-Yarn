/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.Optional;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FurnaceSmeltLootFunction
extends ConditionalLootFunction {
    private static final Logger LOGGER = LogManager.getLogger();

    private FurnaceSmeltLootFunction(LootCondition[] conditions) {
        super(conditions);
    }

    @Override
    public LootFunctionType getType() {
        return LootFunctionTypes.FURNACE_SMELT;
    }

    @Override
    public ItemStack process(ItemStack stack, LootContext context) {
        ItemStack itemStack;
        if (stack.isEmpty()) {
            return stack;
        }
        Optional<SmeltingRecipe> optional = context.getWorld().getRecipeManager().getFirstMatch(RecipeType.SMELTING, new SimpleInventory(stack), context.getWorld());
        if (optional.isPresent() && !(itemStack = optional.get().getOutput()).isEmpty()) {
            ItemStack itemStack2 = itemStack.copy();
            itemStack2.setCount(stack.getCount());
            return itemStack2;
        }
        LOGGER.warn("Couldn't smelt {} because there is no smelting recipe", (Object)stack);
        return stack;
    }

    public static ConditionalLootFunction.Builder<?> builder() {
        return FurnaceSmeltLootFunction.builder(FurnaceSmeltLootFunction::new);
    }

    public static class Serializer
    extends ConditionalLootFunction.Serializer<FurnaceSmeltLootFunction> {
        @Override
        public FurnaceSmeltLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
            return new FurnaceSmeltLootFunction(lootConditions);
        }

        @Override
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject json, JsonDeserializationContext context, LootCondition[] conditions) {
            return this.fromJson(json, context, conditions);
        }
    }
}


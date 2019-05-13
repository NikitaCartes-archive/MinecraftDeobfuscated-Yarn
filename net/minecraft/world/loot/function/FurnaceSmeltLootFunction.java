/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.loot.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.Optional;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.function.ConditionalLootFunction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FurnaceSmeltLootFunction
extends ConditionalLootFunction {
    private static final Logger LOGGER = LogManager.getLogger();

    private FurnaceSmeltLootFunction(LootCondition[] lootConditions) {
        super(lootConditions);
    }

    @Override
    public ItemStack process(ItemStack itemStack, LootContext lootContext) {
        ItemStack itemStack2;
        if (itemStack.isEmpty()) {
            return itemStack;
        }
        Optional<SmeltingRecipe> optional = lootContext.getWorld().getRecipeManager().getFirstMatch(RecipeType.SMELTING, new BasicInventory(itemStack), lootContext.getWorld());
        if (optional.isPresent() && !(itemStack2 = optional.get().getOutput()).isEmpty()) {
            ItemStack itemStack3 = itemStack2.copy();
            itemStack3.setAmount(itemStack.getAmount());
            return itemStack3;
        }
        LOGGER.warn("Couldn't smelt {} because there is no smelting recipe", (Object)itemStack);
        return itemStack;
    }

    public static ConditionalLootFunction.Builder<?> builder() {
        return FurnaceSmeltLootFunction.builder(FurnaceSmeltLootFunction::new);
    }

    public static class Factory
    extends ConditionalLootFunction.Factory<FurnaceSmeltLootFunction> {
        protected Factory() {
            super(new Identifier("furnace_smelt"), FurnaceSmeltLootFunction.class);
        }

        public FurnaceSmeltLootFunction method_726(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
            return new FurnaceSmeltLootFunction(lootConditions);
        }

        @Override
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
            return this.method_726(jsonObject, jsonDeserializationContext, lootConditions);
        }
    }
}


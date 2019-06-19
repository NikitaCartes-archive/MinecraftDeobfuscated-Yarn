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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FurnaceSmeltLootFunction extends ConditionalLootFunction {
	private static final Logger LOGGER = LogManager.getLogger();

	private FurnaceSmeltLootFunction(LootCondition[] lootConditions) {
		super(lootConditions);
	}

	@Override
	public ItemStack process(ItemStack itemStack, LootContext lootContext) {
		if (itemStack.isEmpty()) {
			return itemStack;
		} else {
			Optional<SmeltingRecipe> optional = lootContext.getWorld()
				.getRecipeManager()
				.getFirstMatch(RecipeType.SMELTING, new BasicInventory(itemStack), lootContext.getWorld());
			if (optional.isPresent()) {
				ItemStack itemStack2 = ((SmeltingRecipe)optional.get()).getOutput();
				if (!itemStack2.isEmpty()) {
					ItemStack itemStack3 = itemStack2.copy();
					itemStack3.setCount(itemStack.getCount());
					return itemStack3;
				}
			}

			LOGGER.warn("Couldn't smelt {} because there is no smelting recipe", itemStack);
			return itemStack;
		}
	}

	public static ConditionalLootFunction.Builder<?> builder() {
		return builder(FurnaceSmeltLootFunction::new);
	}

	public static class Factory extends ConditionalLootFunction.Factory<FurnaceSmeltLootFunction> {
		protected Factory() {
			super(new Identifier("furnace_smelt"), FurnaceSmeltLootFunction.class);
		}

		public FurnaceSmeltLootFunction method_726(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
			return new FurnaceSmeltLootFunction(lootConditions);
		}
	}
}

package net.minecraft.world.loot.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.smelting.SmeltingRecipe;
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
			Recipe recipe = method_725(lootContext, itemStack);
			if (recipe != null) {
				ItemStack itemStack2 = recipe.getOutput();
				if (!itemStack2.isEmpty()) {
					ItemStack itemStack3 = itemStack2.copy();
					itemStack3.setAmount(itemStack.getAmount());
					return itemStack3;
				}
			}

			LOGGER.warn("Couldn't smelt {} because there is no smelting recipe", itemStack);
			return itemStack;
		}
	}

	@Nullable
	public static Recipe method_725(LootContext lootContext, ItemStack itemStack) {
		for (Recipe recipe : lootContext.getWorld().getRecipeManager().values()) {
			if (recipe instanceof SmeltingRecipe && recipe.getPreviewInputs().get(0).matches(itemStack)) {
				return recipe;
			}
		}

		return null;
	}

	public static ConditionalLootFunction.Builder<?> method_724() {
		return create(FurnaceSmeltLootFunction::new);
	}

	public static class Factory extends ConditionalLootFunction.Factory<FurnaceSmeltLootFunction> {
		protected Factory() {
			super(new Identifier("furnace_smelt"), FurnaceSmeltLootFunction.class);
		}

		public FurnaceSmeltLootFunction deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
			return new FurnaceSmeltLootFunction(lootConditions);
		}
	}
}

package net.minecraft.loot.function;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import org.slf4j.Logger;

public class FurnaceSmeltLootFunction extends ConditionalLootFunction {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final MapCodec<FurnaceSmeltLootFunction> CODEC = RecordCodecBuilder.mapCodec(
		instance -> addConditionsField(instance).apply(instance, FurnaceSmeltLootFunction::new)
	);

	private FurnaceSmeltLootFunction(List<LootCondition> conditions) {
		super(conditions);
	}

	@Override
	public LootFunctionType<FurnaceSmeltLootFunction> getType() {
		return LootFunctionTypes.FURNACE_SMELT;
	}

	@Override
	public ItemStack process(ItemStack stack, LootContext context) {
		if (stack.isEmpty()) {
			return stack;
		} else {
			Optional<RecipeEntry<SmeltingRecipe>> optional = context.getWorld()
				.getRecipeManager()
				.getFirstMatch(RecipeType.SMELTING, new SingleStackRecipeInput(stack), context.getWorld());
			if (optional.isPresent()) {
				ItemStack itemStack = ((SmeltingRecipe)((RecipeEntry)optional.get()).value()).getResult(context.getWorld().getRegistryManager());
				if (!itemStack.isEmpty()) {
					return itemStack.copyWithCount(stack.getCount());
				}
			}

			LOGGER.warn("Couldn't smelt {} because there is no smelting recipe", stack);
			return stack;
		}
	}

	public static ConditionalLootFunction.Builder<?> builder() {
		return builder(FurnaceSmeltLootFunction::new);
	}
}

package net.minecraft.item;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Optional;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.slf4j.Logger;

public class KnowledgeBookItem extends Item {
	private static final String RECIPES_KEY = "Recipes";
	private static final Logger LOGGER = LogUtils.getLogger();

	public KnowledgeBookItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		NbtCompound nbtCompound = itemStack.getNbt();
		if (!user.getAbilities().creativeMode) {
			user.setStackInHand(hand, ItemStack.EMPTY);
		}

		if (nbtCompound != null && nbtCompound.contains("Recipes", NbtElement.LIST_TYPE)) {
			if (!world.isClient) {
				NbtList nbtList = nbtCompound.getList("Recipes", NbtElement.STRING_TYPE);
				List<Recipe<?>> list = Lists.<Recipe<?>>newArrayList();
				RecipeManager recipeManager = world.getServer().getRecipeManager();

				for (int i = 0; i < nbtList.size(); i++) {
					String string = nbtList.getString(i);
					Optional<? extends Recipe<?>> optional = recipeManager.get(new Identifier(string));
					if (!optional.isPresent()) {
						LOGGER.error("Invalid recipe: {}", string);
						return TypedActionResult.fail(itemStack);
					}

					list.add((Recipe)optional.get());
				}

				user.unlockRecipes(list);
				user.incrementStat(Stats.USED.getOrCreateStat(this));
			}

			return TypedActionResult.success(itemStack, world.isClient());
		} else {
			LOGGER.error("Tag not valid: {}", nbtCompound);
			return TypedActionResult.fail(itemStack);
		}
	}
}

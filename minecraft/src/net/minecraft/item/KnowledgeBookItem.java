package net.minecraft.item;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class KnowledgeBookItem extends Item {
	private static final Logger LOGGER = LogManager.getLogger();

	public KnowledgeBookItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		CompoundTag compoundTag = itemStack.getTag();
		if (!playerEntity.abilities.creativeMode) {
			playerEntity.setStackInHand(hand, ItemStack.EMPTY);
		}

		if (compoundTag != null && compoundTag.containsKey("Recipes", 9)) {
			if (!world.isClient) {
				ListTag listTag = compoundTag.getList("Recipes", 8);
				List<Recipe<?>> list = Lists.<Recipe<?>>newArrayList();
				RecipeManager recipeManager = world.getServer().getRecipeManager();

				for (int i = 0; i < listTag.size(); i++) {
					String string = listTag.getString(i);
					Optional<? extends Recipe<?>> optional = recipeManager.get(new Identifier(string));
					if (!optional.isPresent()) {
						LOGGER.error("Invalid recipe: {}", string);
						return new TypedActionResult<>(ActionResult.FAIL, itemStack);
					}

					list.add(optional.get());
				}

				playerEntity.unlockRecipes(list);
				playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
			}

			return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
		} else {
			LOGGER.error("Tag not valid: {}", compoundTag);
			return new TypedActionResult<>(ActionResult.FAIL, itemStack);
		}
	}
}

package net.minecraft.item;

import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.slf4j.Logger;

public class KnowledgeBookItem extends Item {
	private static final Logger LOGGER = LogUtils.getLogger();

	public KnowledgeBookItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		List<Identifier> list = itemStack.getOrDefault(DataComponentTypes.RECIPES, List.of());
		itemStack.decrementUnlessCreative(1, user);
		if (list.isEmpty()) {
			return TypedActionResult.fail(itemStack);
		} else {
			if (!world.isClient) {
				RecipeManager recipeManager = world.getServer().getRecipeManager();
				List<RecipeEntry<?>> list2 = new ArrayList(list.size());

				for (Identifier identifier : list) {
					Optional<RecipeEntry<?>> optional = recipeManager.get(identifier);
					if (!optional.isPresent()) {
						LOGGER.error("Invalid recipe: {}", identifier);
						return TypedActionResult.fail(itemStack);
					}

					list2.add((RecipeEntry)optional.get());
				}

				user.unlockRecipes(list2);
				user.incrementStat(Stats.USED.getOrCreateStat(this));
			}

			return TypedActionResult.success(itemStack, world.isClient());
		}
	}
}

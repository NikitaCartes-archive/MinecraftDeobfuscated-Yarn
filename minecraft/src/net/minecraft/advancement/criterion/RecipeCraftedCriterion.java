package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;

public class RecipeCraftedCriterion extends AbstractCriterion<RecipeCraftedCriterion.Conditions> {
	@Override
	public Codec<RecipeCraftedCriterion.Conditions> getConditionsCodec() {
		return RecipeCraftedCriterion.Conditions.CODEC;
	}

	public void trigger(ServerPlayerEntity player, RegistryKey<Recipe<?>> recipeKey, List<ItemStack> ingredients) {
		this.trigger(player, conditions -> conditions.matches(recipeKey, ingredients));
	}

	public static record Conditions(Optional<LootContextPredicate> player, RegistryKey<Recipe<?>> recipeId, List<ItemPredicate> ingredients)
		implements AbstractCriterion.Conditions {
		public static final Codec<RecipeCraftedCriterion.Conditions> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(RecipeCraftedCriterion.Conditions::player),
						RegistryKey.createCodec(RegistryKeys.RECIPE).fieldOf("recipe_id").forGetter(RecipeCraftedCriterion.Conditions::recipeId),
						ItemPredicate.CODEC.listOf().optionalFieldOf("ingredients", List.of()).forGetter(RecipeCraftedCriterion.Conditions::ingredients)
					)
					.apply(instance, RecipeCraftedCriterion.Conditions::new)
		);

		public static AdvancementCriterion<RecipeCraftedCriterion.Conditions> create(RegistryKey<Recipe<?>> recipeKey, List<ItemPredicate.Builder> ingredients) {
			return Criteria.RECIPE_CRAFTED
				.create(new RecipeCraftedCriterion.Conditions(Optional.empty(), recipeKey, ingredients.stream().map(ItemPredicate.Builder::build).toList()));
		}

		public static AdvancementCriterion<RecipeCraftedCriterion.Conditions> create(RegistryKey<Recipe<?>> recipeKey) {
			return Criteria.RECIPE_CRAFTED.create(new RecipeCraftedCriterion.Conditions(Optional.empty(), recipeKey, List.of()));
		}

		public static AdvancementCriterion<RecipeCraftedCriterion.Conditions> createCrafterRecipeCrafted(RegistryKey<Recipe<?>> recipeKey) {
			return Criteria.CRAFTER_RECIPE_CRAFTED.create(new RecipeCraftedCriterion.Conditions(Optional.empty(), recipeKey, List.of()));
		}

		boolean matches(RegistryKey<Recipe<?>> recipeKey, List<ItemStack> ingredients) {
			if (recipeKey != this.recipeId) {
				return false;
			} else {
				List<ItemStack> list = new ArrayList(ingredients);

				for (ItemPredicate itemPredicate : this.ingredients) {
					boolean bl = false;
					Iterator<ItemStack> iterator = list.iterator();

					while (iterator.hasNext()) {
						if (itemPredicate.test((ItemStack)iterator.next())) {
							iterator.remove();
							bl = true;
							break;
						}
					}

					if (!bl) {
						return false;
					}
				}

				return true;
			}
		}
	}
}

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
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class RecipeCraftedCriterion extends AbstractCriterion<RecipeCraftedCriterion.Conditions> {
	@Override
	public Codec<RecipeCraftedCriterion.Conditions> getConditionsCodec() {
		return RecipeCraftedCriterion.Conditions.CODEC;
	}

	public void trigger(ServerPlayerEntity player, Identifier recipeId, List<ItemStack> ingredients) {
		this.trigger(player, conditions -> conditions.matches(recipeId, ingredients));
	}

	public static record Conditions(Optional<LootContextPredicate> player, Identifier recipeId, List<ItemPredicate> ingredients)
		implements AbstractCriterion.Conditions {
		public static final Codec<RecipeCraftedCriterion.Conditions> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(RecipeCraftedCriterion.Conditions::player),
						Identifier.CODEC.fieldOf("recipe_id").forGetter(RecipeCraftedCriterion.Conditions::recipeId),
						ItemPredicate.CODEC.listOf().optionalFieldOf("ingredients", List.of()).forGetter(RecipeCraftedCriterion.Conditions::ingredients)
					)
					.apply(instance, RecipeCraftedCriterion.Conditions::new)
		);

		public static AdvancementCriterion<RecipeCraftedCriterion.Conditions> create(Identifier recipeId, List<ItemPredicate.Builder> ingredients) {
			return Criteria.RECIPE_CRAFTED
				.create(new RecipeCraftedCriterion.Conditions(Optional.empty(), recipeId, ingredients.stream().map(ItemPredicate.Builder::build).toList()));
		}

		public static AdvancementCriterion<RecipeCraftedCriterion.Conditions> create(Identifier recipeId) {
			return Criteria.RECIPE_CRAFTED.create(new RecipeCraftedCriterion.Conditions(Optional.empty(), recipeId, List.of()));
		}

		public static AdvancementCriterion<RecipeCraftedCriterion.Conditions> createCrafterRecipeCrafted(Identifier recipeId) {
			return Criteria.CRAFTER_RECIPE_CRAFTED.create(new RecipeCraftedCriterion.Conditions(Optional.empty(), recipeId, List.of()));
		}

		boolean matches(Identifier recipeId, List<ItemStack> ingredients) {
			if (!recipeId.equals(this.recipeId)) {
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

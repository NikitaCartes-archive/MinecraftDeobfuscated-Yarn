package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class RecipeCraftedCriterion extends AbstractCriterion<RecipeCraftedCriterion.Conditions> {
	protected RecipeCraftedCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, Optional<LootContextPredicate> optional, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "recipe_id"));
		List<ItemPredicate> list = ItemPredicate.deserializeAll(jsonObject.get("ingredients"));
		return new RecipeCraftedCriterion.Conditions(optional, identifier, list);
	}

	public void trigger(ServerPlayerEntity player, Identifier recipeId, List<ItemStack> ingredients) {
		this.trigger(player, conditions -> conditions.matches(recipeId, ingredients));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final Identifier recipeId;
		private final List<ItemPredicate> ingredients;

		public Conditions(Optional<LootContextPredicate> playerPredicate, Identifier recipeId, List<ItemPredicate> ingredients) {
			super(playerPredicate);
			this.recipeId = recipeId;
			this.ingredients = ingredients;
		}

		public static AdvancementCriterion<RecipeCraftedCriterion.Conditions> create(Identifier recipeId, List<ItemPredicate.Builder> ingredients) {
			return Criteria.RECIPE_CRAFTED
				.create(new RecipeCraftedCriterion.Conditions(Optional.empty(), recipeId, ingredients.stream().map(ItemPredicate.Builder::build).toList()));
		}

		public static AdvancementCriterion<RecipeCraftedCriterion.Conditions> create(Identifier recipeId) {
			return Criteria.RECIPE_CRAFTED.create(new RecipeCraftedCriterion.Conditions(Optional.empty(), recipeId, List.of()));
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

		@Override
		public JsonObject toJson() {
			JsonObject jsonObject = super.toJson();
			jsonObject.addProperty("recipe_id", this.recipeId.toString());
			if (!this.ingredients.isEmpty()) {
				jsonObject.add("ingredients", ItemPredicate.toJson(this.ingredients));
			}

			return jsonObject;
		}
	}
}

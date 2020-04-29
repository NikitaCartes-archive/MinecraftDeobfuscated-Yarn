package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class EnchantedItemCriterion extends AbstractCriterion<EnchantedItemCriterion.Conditions> {
	private static final Identifier ID = new Identifier("enchanted_item");

	@Override
	public Identifier getId() {
		return ID;
	}

	public EnchantedItemCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, EntityPredicate.Extended extended, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		ItemPredicate itemPredicate = ItemPredicate.fromJson(jsonObject.get("item"));
		NumberRange.IntRange intRange = NumberRange.IntRange.fromJson(jsonObject.get("levels"));
		return new EnchantedItemCriterion.Conditions(extended, itemPredicate, intRange);
	}

	public void trigger(ServerPlayerEntity player, ItemStack stack, int levels) {
		this.test(player, conditions -> conditions.matches(stack, levels));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final ItemPredicate item;
		private final NumberRange.IntRange levels;

		public Conditions(EntityPredicate.Extended player, ItemPredicate item, NumberRange.IntRange levels) {
			super(EnchantedItemCriterion.ID, player);
			this.item = item;
			this.levels = levels;
		}

		public static EnchantedItemCriterion.Conditions any() {
			return new EnchantedItemCriterion.Conditions(EntityPredicate.Extended.EMPTY, ItemPredicate.ANY, NumberRange.IntRange.ANY);
		}

		public boolean matches(ItemStack stack, int levels) {
			return !this.item.test(stack) ? false : this.levels.test(levels);
		}

		@Override
		public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
			JsonObject jsonObject = super.toJson(predicateSerializer);
			jsonObject.add("item", this.item.toJson());
			jsonObject.add("levels", this.levels.toJson());
			return jsonObject;
		}
	}
}

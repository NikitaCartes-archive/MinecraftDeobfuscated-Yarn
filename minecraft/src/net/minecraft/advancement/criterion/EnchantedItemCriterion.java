package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import java.util.Optional;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class EnchantedItemCriterion extends AbstractCriterion<EnchantedItemCriterion.Conditions> {
	static final Identifier ID = new Identifier("enchanted_item");

	@Override
	public Identifier getId() {
		return ID;
	}

	public EnchantedItemCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, Optional<LootContextPredicate> optional, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		Optional<ItemPredicate> optional2 = ItemPredicate.fromJson(jsonObject.get("item"));
		NumberRange.IntRange intRange = NumberRange.IntRange.fromJson(jsonObject.get("levels"));
		return new EnchantedItemCriterion.Conditions(optional, optional2, intRange);
	}

	public void trigger(ServerPlayerEntity player, ItemStack stack, int levels) {
		this.trigger(player, conditions -> conditions.matches(stack, levels));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final Optional<ItemPredicate> item;
		private final NumberRange.IntRange levels;

		public Conditions(Optional<LootContextPredicate> playerPredicate, Optional<ItemPredicate> item, NumberRange.IntRange levels) {
			super(EnchantedItemCriterion.ID, playerPredicate);
			this.item = item;
			this.levels = levels;
		}

		public static EnchantedItemCriterion.Conditions any() {
			return new EnchantedItemCriterion.Conditions(Optional.empty(), Optional.empty(), NumberRange.IntRange.ANY);
		}

		public boolean matches(ItemStack stack, int levels) {
			return this.item.isPresent() && !((ItemPredicate)this.item.get()).test(stack) ? false : this.levels.test(levels);
		}

		@Override
		public JsonObject toJson() {
			JsonObject jsonObject = super.toJson();
			this.item.ifPresent(itemPredicate -> jsonObject.add("item", itemPredicate.toJson()));
			jsonObject.add("levels", this.levels.toJson());
			return jsonObject;
		}
	}
}

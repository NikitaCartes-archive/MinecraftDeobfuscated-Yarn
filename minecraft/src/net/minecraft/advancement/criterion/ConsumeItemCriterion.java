package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

public class ConsumeItemCriterion extends AbstractCriterion<ConsumeItemCriterion.Conditions> {
	public ConsumeItemCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, Optional<LootContextPredicate> optional, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		return new ConsumeItemCriterion.Conditions(optional, ItemPredicate.fromJson(jsonObject.get("item")));
	}

	public void trigger(ServerPlayerEntity player, ItemStack stack) {
		this.trigger(player, conditions -> conditions.matches(stack));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final Optional<ItemPredicate> item;

		public Conditions(Optional<LootContextPredicate> playerPredicate, Optional<ItemPredicate> item) {
			super(playerPredicate);
			this.item = item;
		}

		public static AdvancementCriterion<ConsumeItemCriterion.Conditions> any() {
			return Criteria.CONSUME_ITEM.create(new ConsumeItemCriterion.Conditions(Optional.empty(), Optional.empty()));
		}

		public static AdvancementCriterion<ConsumeItemCriterion.Conditions> item(ItemConvertible item) {
			return predicate(ItemPredicate.Builder.create().items(item.asItem()));
		}

		public static AdvancementCriterion<ConsumeItemCriterion.Conditions> predicate(ItemPredicate.Builder builder) {
			return Criteria.CONSUME_ITEM.create(new ConsumeItemCriterion.Conditions(Optional.empty(), Optional.of(builder.build())));
		}

		public boolean matches(ItemStack stack) {
			return this.item.isEmpty() || ((ItemPredicate)this.item.get()).test(stack);
		}

		@Override
		public JsonObject toJson() {
			JsonObject jsonObject = super.toJson();
			this.item.ifPresent(itemPredicate -> jsonObject.add("item", itemPredicate.toJson()));
			return jsonObject;
		}
	}
}

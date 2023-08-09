package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import java.util.Optional;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ConsumeItemCriterion extends AbstractCriterion<ConsumeItemCriterion.Conditions> {
	static final Identifier ID = new Identifier("consume_item");

	@Override
	public Identifier getId() {
		return ID;
	}

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
			super(ConsumeItemCriterion.ID, playerPredicate);
			this.item = item;
		}

		public static ConsumeItemCriterion.Conditions any() {
			return new ConsumeItemCriterion.Conditions(Optional.empty(), Optional.empty());
		}

		public static ConsumeItemCriterion.Conditions predicate(ItemPredicate predicate) {
			return new ConsumeItemCriterion.Conditions(Optional.empty(), Optional.of(predicate));
		}

		public static ConsumeItemCriterion.Conditions item(ItemConvertible item) {
			return new ConsumeItemCriterion.Conditions(Optional.empty(), ItemPredicate.Builder.create().items(item.asItem()).build());
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

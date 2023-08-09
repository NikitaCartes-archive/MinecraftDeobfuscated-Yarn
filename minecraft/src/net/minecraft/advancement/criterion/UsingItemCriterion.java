package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import java.util.Optional;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class UsingItemCriterion extends AbstractCriterion<UsingItemCriterion.Conditions> {
	static final Identifier ID = new Identifier("using_item");

	@Override
	public Identifier getId() {
		return ID;
	}

	public UsingItemCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, Optional<LootContextPredicate> optional, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		Optional<ItemPredicate> optional2 = ItemPredicate.fromJson(jsonObject.get("item"));
		return new UsingItemCriterion.Conditions(optional, optional2);
	}

	public void trigger(ServerPlayerEntity player, ItemStack stack) {
		this.trigger(player, conditions -> conditions.test(stack));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final Optional<ItemPredicate> item;

		public Conditions(Optional<LootContextPredicate> playerPredicate, Optional<ItemPredicate> item) {
			super(UsingItemCriterion.ID, playerPredicate);
			this.item = item;
		}

		public static UsingItemCriterion.Conditions create(EntityPredicate.Builder player, ItemPredicate.Builder item) {
			return new UsingItemCriterion.Conditions(EntityPredicate.contextPredicateFromEntityPredicate(player), item.build());
		}

		public boolean test(ItemStack stack) {
			return !this.item.isPresent() || ((ItemPredicate)this.item.get()).test(stack);
		}

		@Override
		public JsonObject toJson() {
			JsonObject jsonObject = super.toJson();
			this.item.ifPresent(itemPredicate -> jsonObject.add("item", itemPredicate.toJson()));
			return jsonObject;
		}
	}
}

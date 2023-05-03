package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class UsedTotemCriterion extends AbstractCriterion<UsedTotemCriterion.Conditions> {
	static final Identifier ID = new Identifier("used_totem");

	@Override
	public Identifier getId() {
		return ID;
	}

	public UsedTotemCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, LootContextPredicate lootContextPredicate, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		ItemPredicate itemPredicate = ItemPredicate.fromJson(jsonObject.get("item"));
		return new UsedTotemCriterion.Conditions(lootContextPredicate, itemPredicate);
	}

	public void trigger(ServerPlayerEntity player, ItemStack stack) {
		this.trigger(player, conditions -> conditions.matches(stack));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final ItemPredicate item;

		public Conditions(LootContextPredicate player, ItemPredicate item) {
			super(UsedTotemCriterion.ID, player);
			this.item = item;
		}

		public static UsedTotemCriterion.Conditions create(ItemPredicate itemPredicate) {
			return new UsedTotemCriterion.Conditions(LootContextPredicate.EMPTY, itemPredicate);
		}

		public static UsedTotemCriterion.Conditions create(ItemConvertible item) {
			return new UsedTotemCriterion.Conditions(LootContextPredicate.EMPTY, ItemPredicate.Builder.create().items(item).build());
		}

		public boolean matches(ItemStack stack) {
			return this.item.test(stack);
		}

		@Override
		public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
			JsonObject jsonObject = super.toJson(predicateSerializer);
			jsonObject.add("item", this.item.toJson());
			return jsonObject;
		}
	}
}

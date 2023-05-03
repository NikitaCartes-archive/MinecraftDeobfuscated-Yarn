package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ItemDurabilityChangedCriterion extends AbstractCriterion<ItemDurabilityChangedCriterion.Conditions> {
	static final Identifier ID = new Identifier("item_durability_changed");

	@Override
	public Identifier getId() {
		return ID;
	}

	public ItemDurabilityChangedCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, LootContextPredicate lootContextPredicate, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		ItemPredicate itemPredicate = ItemPredicate.fromJson(jsonObject.get("item"));
		NumberRange.IntRange intRange = NumberRange.IntRange.fromJson(jsonObject.get("durability"));
		NumberRange.IntRange intRange2 = NumberRange.IntRange.fromJson(jsonObject.get("delta"));
		return new ItemDurabilityChangedCriterion.Conditions(lootContextPredicate, itemPredicate, intRange, intRange2);
	}

	public void trigger(ServerPlayerEntity player, ItemStack stack, int durability) {
		this.trigger(player, conditions -> conditions.matches(stack, durability));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final ItemPredicate item;
		private final NumberRange.IntRange durability;
		private final NumberRange.IntRange delta;

		public Conditions(LootContextPredicate player, ItemPredicate item, NumberRange.IntRange durability, NumberRange.IntRange delta) {
			super(ItemDurabilityChangedCriterion.ID, player);
			this.item = item;
			this.durability = durability;
			this.delta = delta;
		}

		public static ItemDurabilityChangedCriterion.Conditions create(ItemPredicate item, NumberRange.IntRange durability) {
			return create(LootContextPredicate.EMPTY, item, durability);
		}

		public static ItemDurabilityChangedCriterion.Conditions create(LootContextPredicate player, ItemPredicate item, NumberRange.IntRange durability) {
			return new ItemDurabilityChangedCriterion.Conditions(player, item, durability, NumberRange.IntRange.ANY);
		}

		public boolean matches(ItemStack stack, int durability) {
			if (!this.item.test(stack)) {
				return false;
			} else {
				return !this.durability.test(stack.getMaxDamage() - durability) ? false : this.delta.test(stack.getDamage() - durability);
			}
		}

		@Override
		public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
			JsonObject jsonObject = super.toJson(predicateSerializer);
			jsonObject.add("item", this.item.toJson());
			jsonObject.add("durability", this.durability.toJson());
			jsonObject.add("delta", this.delta.toJson());
			return jsonObject;
		}
	}
}

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

public class ItemDurabilityChangedCriterion extends AbstractCriterion<ItemDurabilityChangedCriterion.Conditions> {
	static final Identifier ID = new Identifier("item_durability_changed");

	@Override
	public Identifier getId() {
		return ID;
	}

	public ItemDurabilityChangedCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, Optional<LootContextPredicate> optional, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		Optional<ItemPredicate> optional2 = ItemPredicate.fromJson(jsonObject.get("item"));
		NumberRange.IntRange intRange = NumberRange.IntRange.fromJson(jsonObject.get("durability"));
		NumberRange.IntRange intRange2 = NumberRange.IntRange.fromJson(jsonObject.get("delta"));
		return new ItemDurabilityChangedCriterion.Conditions(optional, optional2, intRange, intRange2);
	}

	public void trigger(ServerPlayerEntity player, ItemStack stack, int durability) {
		this.trigger(player, conditions -> conditions.matches(stack, durability));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final Optional<ItemPredicate> item;
		private final NumberRange.IntRange durability;
		private final NumberRange.IntRange delta;

		public Conditions(Optional<LootContextPredicate> playerPredicate, Optional<ItemPredicate> item, NumberRange.IntRange durability, NumberRange.IntRange delta) {
			super(ItemDurabilityChangedCriterion.ID, playerPredicate);
			this.item = item;
			this.durability = durability;
			this.delta = delta;
		}

		public static ItemDurabilityChangedCriterion.Conditions create(Optional<ItemPredicate> item, NumberRange.IntRange durability) {
			return create(Optional.empty(), item, durability);
		}

		public static ItemDurabilityChangedCriterion.Conditions create(
			Optional<LootContextPredicate> playerPredicate, Optional<ItemPredicate> item, NumberRange.IntRange durability
		) {
			return new ItemDurabilityChangedCriterion.Conditions(playerPredicate, item, durability, NumberRange.IntRange.ANY);
		}

		public boolean matches(ItemStack stack, int durability) {
			if (this.item.isPresent() && !((ItemPredicate)this.item.get()).test(stack)) {
				return false;
			} else {
				return !this.durability.test(stack.getMaxDamage() - durability) ? false : this.delta.test(stack.getDamage() - durability);
			}
		}

		@Override
		public JsonObject toJson() {
			JsonObject jsonObject = super.toJson();
			this.item.ifPresent(itemPredicate -> jsonObject.add("item", itemPredicate.toJson()));
			jsonObject.add("durability", this.durability.toJson());
			jsonObject.add("delta", this.delta.toJson());
			return jsonObject;
		}
	}
}

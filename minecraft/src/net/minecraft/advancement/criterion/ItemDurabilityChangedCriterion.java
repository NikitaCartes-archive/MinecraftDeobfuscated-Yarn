package net.minecraft.advancement.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ItemDurabilityChangedCriterion extends AbstractCriterion<ItemDurabilityChangedCriterion.Conditions> {
	private static final Identifier ID = new Identifier("item_durability_changed");

	@Override
	public Identifier getId() {
		return ID;
	}

	public ItemDurabilityChangedCriterion.Conditions method_8962(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		ItemPredicate itemPredicate = ItemPredicate.deserialize(jsonObject.get("item"));
		NumberRange.IntRange intRange = NumberRange.IntRange.fromJson(jsonObject.get("durability"));
		NumberRange.IntRange intRange2 = NumberRange.IntRange.fromJson(jsonObject.get("delta"));
		return new ItemDurabilityChangedCriterion.Conditions(itemPredicate, intRange, intRange2);
	}

	public void handle(ServerPlayerEntity serverPlayerEntity, ItemStack itemStack, int i) {
		this.test(serverPlayerEntity.getAdvancementManager(), conditions -> conditions.matches(itemStack, i));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final ItemPredicate item;
		private final NumberRange.IntRange durability;
		private final NumberRange.IntRange delta;

		public Conditions(ItemPredicate itemPredicate, NumberRange.IntRange intRange, NumberRange.IntRange intRange2) {
			super(ItemDurabilityChangedCriterion.ID);
			this.item = itemPredicate;
			this.durability = intRange;
			this.delta = intRange2;
		}

		public static ItemDurabilityChangedCriterion.Conditions create(ItemPredicate itemPredicate, NumberRange.IntRange intRange) {
			return new ItemDurabilityChangedCriterion.Conditions(itemPredicate, intRange, NumberRange.IntRange.ANY);
		}

		public boolean matches(ItemStack itemStack, int i) {
			if (!this.item.test(itemStack)) {
				return false;
			} else {
				return !this.durability.test(itemStack.getMaxDamage() - i) ? false : this.delta.test(itemStack.getDamage() - i);
			}
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("item", this.item.serialize());
			jsonObject.add("durability", this.durability.toJson());
			jsonObject.add("delta", this.delta.toJson());
			return jsonObject;
		}
	}
}

package net.minecraft.advancement.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ConsumeItemCriterion extends AbstractCriterion<ConsumeItemCriterion.Conditions> {
	private static final Identifier ID = new Identifier("consume_item");

	@Override
	public Identifier getId() {
		return ID;
	}

	public ConsumeItemCriterion.Conditions method_8820(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		return new ConsumeItemCriterion.Conditions(ItemPredicate.fromJson(jsonObject.get("item")));
	}

	public void trigger(ServerPlayerEntity player, ItemStack stack) {
		this.test(player.getAdvancementTracker(), conditions -> conditions.matches(stack));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final ItemPredicate item;

		public Conditions(ItemPredicate item) {
			super(ConsumeItemCriterion.ID);
			this.item = item;
		}

		public static ConsumeItemCriterion.Conditions any() {
			return new ConsumeItemCriterion.Conditions(ItemPredicate.ANY);
		}

		public static ConsumeItemCriterion.Conditions item(ItemConvertible item) {
			return new ConsumeItemCriterion.Conditions(
				new ItemPredicate(
					null,
					item.asItem(),
					NumberRange.IntRange.ANY,
					NumberRange.IntRange.ANY,
					EnchantmentPredicate.ARRAY_OF_ANY,
					EnchantmentPredicate.ARRAY_OF_ANY,
					null,
					NbtPredicate.ANY
				)
			);
		}

		public boolean matches(ItemStack stack) {
			return this.item.test(stack);
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("item", this.item.toJson());
			return jsonObject;
		}
	}
}

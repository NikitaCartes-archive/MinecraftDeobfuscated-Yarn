package net.minecraft.advancement.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class FilledBucketCriterion extends AbstractCriterion<FilledBucketCriterion.Conditions> {
	private static final Identifier ID = new Identifier("filled_bucket");

	@Override
	public Identifier getId() {
		return ID;
	}

	public FilledBucketCriterion.Conditions method_8931(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		ItemPredicate itemPredicate = ItemPredicate.deserialize(jsonObject.get("item"));
		return new FilledBucketCriterion.Conditions(itemPredicate);
	}

	public void trigger(ServerPlayerEntity serverPlayerEntity, ItemStack itemStack) {
		this.test(serverPlayerEntity.getAdvancementManager(), conditions -> conditions.matches(itemStack));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final ItemPredicate item;

		public Conditions(ItemPredicate itemPredicate) {
			super(FilledBucketCriterion.ID);
			this.item = itemPredicate;
		}

		public static FilledBucketCriterion.Conditions create(ItemPredicate itemPredicate) {
			return new FilledBucketCriterion.Conditions(itemPredicate);
		}

		public boolean matches(ItemStack itemStack) {
			return this.item.test(itemStack);
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("item", this.item.serialize());
			return jsonObject;
		}
	}
}

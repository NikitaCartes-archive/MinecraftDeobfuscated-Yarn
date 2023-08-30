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

public class ShotCrossbowCriterion extends AbstractCriterion<ShotCrossbowCriterion.Conditions> {
	public ShotCrossbowCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, Optional<LootContextPredicate> optional, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		Optional<ItemPredicate> optional2 = ItemPredicate.fromJson(jsonObject.get("item"));
		return new ShotCrossbowCriterion.Conditions(optional, optional2);
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

		public static AdvancementCriterion<ShotCrossbowCriterion.Conditions> create(Optional<ItemPredicate> item) {
			return Criteria.SHOT_CROSSBOW.create(new ShotCrossbowCriterion.Conditions(Optional.empty(), item));
		}

		public static AdvancementCriterion<ShotCrossbowCriterion.Conditions> create(ItemConvertible item) {
			return Criteria.SHOT_CROSSBOW
				.create(new ShotCrossbowCriterion.Conditions(Optional.empty(), Optional.of(ItemPredicate.Builder.create().items(item).build())));
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

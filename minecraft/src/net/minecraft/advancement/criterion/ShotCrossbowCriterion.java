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

public class ShotCrossbowCriterion extends AbstractCriterion<ShotCrossbowCriterion.Conditions> {
	static final Identifier ID = new Identifier("shot_crossbow");

	@Override
	public Identifier getId() {
		return ID;
	}

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
			super(ShotCrossbowCriterion.ID, playerPredicate);
			this.item = item;
		}

		public static ShotCrossbowCriterion.Conditions create(Optional<ItemPredicate> item) {
			return new ShotCrossbowCriterion.Conditions(Optional.empty(), item);
		}

		public static ShotCrossbowCriterion.Conditions create(ItemConvertible item) {
			return new ShotCrossbowCriterion.Conditions(Optional.empty(), ItemPredicate.Builder.create().items(item).build());
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

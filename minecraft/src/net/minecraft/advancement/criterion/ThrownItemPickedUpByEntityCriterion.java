package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

public class ThrownItemPickedUpByEntityCriterion extends AbstractCriterion<ThrownItemPickedUpByEntityCriterion.Conditions> {
	protected ThrownItemPickedUpByEntityCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, Optional<LootContextPredicate> optional, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		Optional<ItemPredicate> optional2 = ItemPredicate.fromJson(jsonObject.get("item"));
		Optional<LootContextPredicate> optional3 = EntityPredicate.contextPredicateFromJson(jsonObject, "entity", advancementEntityPredicateDeserializer);
		return new ThrownItemPickedUpByEntityCriterion.Conditions(optional, optional2, optional3);
	}

	public void trigger(ServerPlayerEntity player, ItemStack stack, @Nullable Entity entity) {
		LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext(player, entity);
		this.trigger(player, conditions -> conditions.test(player, stack, lootContext));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final Optional<ItemPredicate> item;
		private final Optional<LootContextPredicate> entity;

		public Conditions(Optional<LootContextPredicate> optional, Optional<ItemPredicate> playerPredicate, Optional<LootContextPredicate> item) {
			super(optional);
			this.item = playerPredicate;
			this.entity = item;
		}

		public static AdvancementCriterion<ThrownItemPickedUpByEntityCriterion.Conditions> createThrownItemPickedUpByEntity(
			LootContextPredicate player, Optional<ItemPredicate> item, Optional<LootContextPredicate> entity
		) {
			return Criteria.THROWN_ITEM_PICKED_UP_BY_ENTITY.create(new ThrownItemPickedUpByEntityCriterion.Conditions(Optional.of(player), item, entity));
		}

		public static AdvancementCriterion<ThrownItemPickedUpByEntityCriterion.Conditions> createThrownItemPickedUpByPlayer(
			Optional<LootContextPredicate> playerPredicate, Optional<ItemPredicate> item, Optional<LootContextPredicate> entity
		) {
			return Criteria.THROWN_ITEM_PICKED_UP_BY_PLAYER.create(new ThrownItemPickedUpByEntityCriterion.Conditions(playerPredicate, item, entity));
		}

		public boolean test(ServerPlayerEntity player, ItemStack stack, LootContext entity) {
			return this.item.isPresent() && !((ItemPredicate)this.item.get()).test(stack)
				? false
				: !this.entity.isPresent() || ((LootContextPredicate)this.entity.get()).test(entity);
		}

		@Override
		public JsonObject toJson() {
			JsonObject jsonObject = super.toJson();
			this.item.ifPresent(itemPredicate -> jsonObject.add("item", itemPredicate.toJson()));
			this.entity.ifPresent(lootContextPredicate -> jsonObject.add("entity", lootContextPredicate.toJson()));
			return jsonObject;
		}
	}
}

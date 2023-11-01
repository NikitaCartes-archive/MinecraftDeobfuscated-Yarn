package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerInteractedWithEntityCriterion extends AbstractCriterion<PlayerInteractedWithEntityCriterion.Conditions> {
	protected PlayerInteractedWithEntityCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, Optional<LootContextPredicate> optional, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		Optional<ItemPredicate> optional2 = ItemPredicate.fromJson(jsonObject.get("item"));
		Optional<LootContextPredicate> optional3 = EntityPredicate.contextPredicateFromJson(jsonObject, "entity", advancementEntityPredicateDeserializer);
		return new PlayerInteractedWithEntityCriterion.Conditions(optional, optional2, optional3);
	}

	public void trigger(ServerPlayerEntity player, ItemStack stack, Entity entity) {
		LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext(player, entity);
		this.trigger(player, conditions -> conditions.test(stack, lootContext));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final Optional<ItemPredicate> item;
		private final Optional<LootContextPredicate> entity;

		public Conditions(Optional<LootContextPredicate> playerPredicate, Optional<ItemPredicate> item, Optional<LootContextPredicate> entity) {
			super(playerPredicate);
			this.item = item;
			this.entity = entity;
		}

		public static AdvancementCriterion<PlayerInteractedWithEntityCriterion.Conditions> create(
			Optional<LootContextPredicate> playerPredicate, ItemPredicate.Builder item, Optional<LootContextPredicate> entity
		) {
			return Criteria.PLAYER_INTERACTED_WITH_ENTITY.create(new PlayerInteractedWithEntityCriterion.Conditions(playerPredicate, Optional.of(item.build()), entity));
		}

		public static AdvancementCriterion<PlayerInteractedWithEntityCriterion.Conditions> create(ItemPredicate.Builder item, Optional<LootContextPredicate> entity) {
			return create(Optional.empty(), item, entity);
		}

		public boolean test(ItemStack stack, LootContext entity) {
			return this.item.isPresent() && !((ItemPredicate)this.item.get()).test(stack)
				? false
				: this.entity.isEmpty() || ((LootContextPredicate)this.entity.get()).test(entity);
		}

		@Override
		public JsonObject toJson() {
			JsonObject jsonObject = super.toJson();
			this.item.ifPresent(item -> jsonObject.add("item", item.toJson()));
			this.entity.ifPresent(entity -> jsonObject.add("entity", entity.toJson()));
			return jsonObject;
		}
	}
}

package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class PlayerInteractedWithEntityCriterion extends AbstractCriterion<PlayerInteractedWithEntityCriterion.Conditions> {
	static final Identifier ID = new Identifier("player_interacted_with_entity");

	@Override
	public Identifier getId() {
		return ID;
	}

	protected PlayerInteractedWithEntityCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, LootContextPredicate lootContextPredicate, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		ItemPredicate itemPredicate = ItemPredicate.fromJson(jsonObject.get("item"));
		LootContextPredicate lootContextPredicate2 = EntityPredicate.contextPredicateFromJson(jsonObject, "entity", advancementEntityPredicateDeserializer);
		return new PlayerInteractedWithEntityCriterion.Conditions(lootContextPredicate, itemPredicate, lootContextPredicate2);
	}

	public void trigger(ServerPlayerEntity player, ItemStack stack, Entity entity) {
		LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext(player, entity);
		this.trigger(player, conditions -> conditions.test(stack, lootContext));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final ItemPredicate item;
		private final LootContextPredicate entity;

		public Conditions(LootContextPredicate player, ItemPredicate item, LootContextPredicate entity) {
			super(PlayerInteractedWithEntityCriterion.ID, player);
			this.item = item;
			this.entity = entity;
		}

		public static PlayerInteractedWithEntityCriterion.Conditions create(
			LootContextPredicate player, ItemPredicate.Builder itemBuilder, LootContextPredicate entity
		) {
			return new PlayerInteractedWithEntityCriterion.Conditions(player, itemBuilder.build(), entity);
		}

		public static PlayerInteractedWithEntityCriterion.Conditions create(ItemPredicate.Builder itemBuilder, LootContextPredicate entity) {
			return create(LootContextPredicate.EMPTY, itemBuilder, entity);
		}

		public boolean test(ItemStack stack, LootContext context) {
			return !this.item.test(stack) ? false : this.entity.test(context);
		}

		@Override
		public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
			JsonObject jsonObject = super.toJson(predicateSerializer);
			jsonObject.add("item", this.item.toJson());
			jsonObject.add("entity", this.entity.toJson(predicateSerializer));
			return jsonObject;
		}
	}
}

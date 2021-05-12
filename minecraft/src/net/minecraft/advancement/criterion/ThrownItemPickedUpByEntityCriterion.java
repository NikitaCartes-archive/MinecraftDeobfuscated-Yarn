package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ThrownItemPickedUpByEntityCriterion extends AbstractCriterion<ThrownItemPickedUpByEntityCriterion.Conditions> {
	static final Identifier ID = new Identifier("thrown_item_picked_up_by_entity");

	@Override
	public Identifier getId() {
		return ID;
	}

	protected ThrownItemPickedUpByEntityCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, EntityPredicate.Extended extended, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		ItemPredicate itemPredicate = ItemPredicate.fromJson(jsonObject.get("item"));
		EntityPredicate.Extended extended2 = EntityPredicate.Extended.getInJson(jsonObject, "entity", advancementEntityPredicateDeserializer);
		return new ThrownItemPickedUpByEntityCriterion.Conditions(extended, itemPredicate, extended2);
	}

	public void trigger(ServerPlayerEntity player, ItemStack stack, Entity entity) {
		LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext(player, entity);
		this.test(player, conditions -> conditions.test(player, stack, lootContext));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final ItemPredicate item;
		private final EntityPredicate.Extended entity;

		public Conditions(EntityPredicate.Extended player, ItemPredicate item, EntityPredicate.Extended entity) {
			super(ThrownItemPickedUpByEntityCriterion.ID, player);
			this.item = item;
			this.entity = entity;
		}

		public static ThrownItemPickedUpByEntityCriterion.Conditions create(
			EntityPredicate.Extended player, ItemPredicate.Builder item, EntityPredicate.Extended entity
		) {
			return new ThrownItemPickedUpByEntityCriterion.Conditions(player, item.build(), entity);
		}

		public boolean test(ServerPlayerEntity player, ItemStack stack, LootContext entityContext) {
			return !this.item.test(stack) ? false : this.entity.test(entityContext);
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

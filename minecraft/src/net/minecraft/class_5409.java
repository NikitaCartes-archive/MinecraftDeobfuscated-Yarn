package net.minecraft;

import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class class_5409 extends AbstractCriterion<class_5409.class_5410> {
	private static final Identifier field_25699 = new Identifier("player_interacted_with_entity");

	@Override
	public Identifier getId() {
		return field_25699;
	}

	protected class_5409.class_5410 conditionsFromJson(
		JsonObject jsonObject, EntityPredicate.Extended extended, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		ItemPredicate itemPredicate = ItemPredicate.fromJson(jsonObject.get("item"));
		EntityPredicate.Extended extended2 = EntityPredicate.Extended.getInJson(jsonObject, "entity", advancementEntityPredicateDeserializer);
		return new class_5409.class_5410(extended, itemPredicate, extended2);
	}

	public void method_30097(ServerPlayerEntity serverPlayerEntity, ItemStack itemStack, Entity entity) {
		LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext(serverPlayerEntity, entity);
		this.test(serverPlayerEntity, arg -> arg.method_30100(itemStack, lootContext));
	}

	public static class class_5410 extends AbstractCriterionConditions {
		private final ItemPredicate field_25700;
		private final EntityPredicate.Extended field_25701;

		public class_5410(EntityPredicate.Extended extended, ItemPredicate itemPredicate, EntityPredicate.Extended extended2) {
			super(class_5409.field_25699, extended);
			this.field_25700 = itemPredicate;
			this.field_25701 = extended2;
		}

		public static class_5409.class_5410 method_30099(EntityPredicate.Extended extended, ItemPredicate.Builder builder, EntityPredicate.Extended extended2) {
			return new class_5409.class_5410(extended, builder.build(), extended2);
		}

		public boolean method_30100(ItemStack itemStack, LootContext lootContext) {
			return !this.field_25700.test(itemStack) ? false : this.field_25701.test(lootContext);
		}

		@Override
		public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
			JsonObject jsonObject = super.toJson(predicateSerializer);
			jsonObject.add("item", this.field_25700.toJson());
			jsonObject.add("entity", this.field_25701.toJson(predicateSerializer));
			return jsonObject;
		}
	}
}

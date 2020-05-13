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

public class class_5279 extends AbstractCriterion<class_5279.class_5280> {
	private static final Identifier field_24492 = new Identifier("thrown_item_picked_up_by_entity");

	@Override
	public Identifier getId() {
		return field_24492;
	}

	protected class_5279.class_5280 conditionsFromJson(
		JsonObject jsonObject, EntityPredicate.Extended extended, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		ItemPredicate itemPredicate = ItemPredicate.fromJson(jsonObject.get("item"));
		EntityPredicate.Extended extended2 = EntityPredicate.Extended.getInJson(jsonObject, "entity", advancementEntityPredicateDeserializer);
		return new class_5279.class_5280(extended, itemPredicate, extended2);
	}

	public void method_27975(ServerPlayerEntity serverPlayerEntity, ItemStack itemStack, Entity entity) {
		LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext(serverPlayerEntity, entity);
		this.test(serverPlayerEntity, arg -> arg.method_27979(serverPlayerEntity, itemStack, lootContext));
	}

	public static class class_5280 extends AbstractCriterionConditions {
		private final ItemPredicate field_24493;
		private final EntityPredicate.Extended field_24494;

		public class_5280(EntityPredicate.Extended extended, ItemPredicate itemPredicate, EntityPredicate.Extended extended2) {
			super(class_5279.field_24492, extended);
			this.field_24493 = itemPredicate;
			this.field_24494 = extended2;
		}

		public static class_5279.class_5280 method_27978(EntityPredicate.Extended extended, ItemPredicate.Builder builder, EntityPredicate.Extended extended2) {
			return new class_5279.class_5280(extended, builder.build(), extended2);
		}

		public boolean method_27979(ServerPlayerEntity serverPlayerEntity, ItemStack itemStack, LootContext lootContext) {
			return !this.field_24493.test(itemStack) ? false : this.field_24494.test(lootContext);
		}

		@Override
		public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
			JsonObject jsonObject = super.toJson(predicateSerializer);
			jsonObject.add("item", this.field_24493.toJson());
			jsonObject.add("entity", this.field_24494.toJson(predicateSerializer));
			return jsonObject;
		}
	}
}

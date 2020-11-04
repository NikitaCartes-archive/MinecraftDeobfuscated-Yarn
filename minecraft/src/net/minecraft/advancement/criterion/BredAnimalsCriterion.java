package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class BredAnimalsCriterion extends AbstractCriterion<BredAnimalsCriterion.Conditions> {
	private static final Identifier ID = new Identifier("bred_animals");

	@Override
	public Identifier getId() {
		return ID;
	}

	public BredAnimalsCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, EntityPredicate.Extended extended, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		EntityPredicate.Extended extended2 = EntityPredicate.Extended.getInJson(jsonObject, "parent", advancementEntityPredicateDeserializer);
		EntityPredicate.Extended extended3 = EntityPredicate.Extended.getInJson(jsonObject, "partner", advancementEntityPredicateDeserializer);
		EntityPredicate.Extended extended4 = EntityPredicate.Extended.getInJson(jsonObject, "child", advancementEntityPredicateDeserializer);
		return new BredAnimalsCriterion.Conditions(extended, extended2, extended3, extended4);
	}

	public void trigger(ServerPlayerEntity serverPlayerEntity, AnimalEntity animalEntity, AnimalEntity animalEntity2, @Nullable PassiveEntity passiveEntity) {
		LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext(serverPlayerEntity, animalEntity);
		LootContext lootContext2 = EntityPredicate.createAdvancementEntityLootContext(serverPlayerEntity, animalEntity2);
		LootContext lootContext3 = passiveEntity != null ? EntityPredicate.createAdvancementEntityLootContext(serverPlayerEntity, passiveEntity) : null;
		this.test(serverPlayerEntity, conditions -> conditions.matches(lootContext, lootContext2, lootContext3));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final EntityPredicate.Extended parent;
		private final EntityPredicate.Extended partner;
		private final EntityPredicate.Extended child;

		public Conditions(
			EntityPredicate.Extended extended, EntityPredicate.Extended extended2, EntityPredicate.Extended extended3, EntityPredicate.Extended extended4
		) {
			super(BredAnimalsCriterion.ID, extended);
			this.parent = extended2;
			this.partner = extended3;
			this.child = extended4;
		}

		public static BredAnimalsCriterion.Conditions any() {
			return new BredAnimalsCriterion.Conditions(
				EntityPredicate.Extended.EMPTY, EntityPredicate.Extended.EMPTY, EntityPredicate.Extended.EMPTY, EntityPredicate.Extended.EMPTY
			);
		}

		public static BredAnimalsCriterion.Conditions create(EntityPredicate.Builder builder) {
			return new BredAnimalsCriterion.Conditions(
				EntityPredicate.Extended.EMPTY, EntityPredicate.Extended.EMPTY, EntityPredicate.Extended.EMPTY, EntityPredicate.Extended.ofLegacy(builder.build())
			);
		}

		public static BredAnimalsCriterion.Conditions method_29918(
			EntityPredicate entityPredicate, EntityPredicate entityPredicate2, EntityPredicate entityPredicate3
		) {
			return new BredAnimalsCriterion.Conditions(
				EntityPredicate.Extended.EMPTY,
				EntityPredicate.Extended.ofLegacy(entityPredicate),
				EntityPredicate.Extended.ofLegacy(entityPredicate2),
				EntityPredicate.Extended.ofLegacy(entityPredicate3)
			);
		}

		public boolean matches(LootContext lootContext, LootContext lootContext2, @Nullable LootContext lootContext3) {
			return this.child == EntityPredicate.Extended.EMPTY || lootContext3 != null && this.child.test(lootContext3)
				? this.parent.test(lootContext) && this.partner.test(lootContext2) || this.parent.test(lootContext2) && this.partner.test(lootContext)
				: false;
		}

		@Override
		public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
			JsonObject jsonObject = super.toJson(predicateSerializer);
			jsonObject.add("parent", this.parent.toJson(predicateSerializer));
			jsonObject.add("partner", this.partner.toJson(predicateSerializer));
			jsonObject.add("child", this.child.toJson(predicateSerializer));
			return jsonObject;
		}
	}
}

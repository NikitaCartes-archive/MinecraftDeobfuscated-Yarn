package net.minecraft.advancement.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.class_4558;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class TameAnimalCriterion extends class_4558<TameAnimalCriterion.Conditions> {
	private static final Identifier ID = new Identifier("tame_animal");

	@Override
	public Identifier getId() {
		return ID;
	}

	public TameAnimalCriterion.Conditions method_9133(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		EntityPredicate entityPredicate = EntityPredicate.deserialize(jsonObject.get("entity"));
		return new TameAnimalCriterion.Conditions(entityPredicate);
	}

	public void handle(ServerPlayerEntity serverPlayerEntity, AnimalEntity animalEntity) {
		this.method_22510(serverPlayerEntity.getAdvancementManager(), conditions -> conditions.matches(serverPlayerEntity, animalEntity));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final EntityPredicate entity;

		public Conditions(EntityPredicate entityPredicate) {
			super(TameAnimalCriterion.ID);
			this.entity = entityPredicate;
		}

		public static TameAnimalCriterion.Conditions any() {
			return new TameAnimalCriterion.Conditions(EntityPredicate.ANY);
		}

		public static TameAnimalCriterion.Conditions create(EntityPredicate entityPredicate) {
			return new TameAnimalCriterion.Conditions(entityPredicate);
		}

		public boolean matches(ServerPlayerEntity serverPlayerEntity, AnimalEntity animalEntity) {
			return this.entity.test(serverPlayerEntity, animalEntity);
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("entity", this.entity.serialize());
			return jsonObject;
		}
	}
}

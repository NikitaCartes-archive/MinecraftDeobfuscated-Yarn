package net.minecraft.advancement.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class SummonedEntityCriterion extends AbstractCriterion<SummonedEntityCriterion.Conditions> {
	private static final Identifier ID = new Identifier("summoned_entity");

	@Override
	public Identifier getId() {
		return ID;
	}

	public SummonedEntityCriterion.Conditions method_9123(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		EntityPredicate entityPredicate = EntityPredicate.fromJson(jsonObject.get("entity"));
		return new SummonedEntityCriterion.Conditions(entityPredicate);
	}

	public void handle(ServerPlayerEntity serverPlayerEntity, Entity entity) {
		this.test(serverPlayerEntity.getAdvancementManager(), conditions -> conditions.matches(serverPlayerEntity, entity));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final EntityPredicate entity;

		public Conditions(EntityPredicate entityPredicate) {
			super(SummonedEntityCriterion.ID);
			this.entity = entityPredicate;
		}

		public static SummonedEntityCriterion.Conditions create(EntityPredicate.Builder builder) {
			return new SummonedEntityCriterion.Conditions(builder.build());
		}

		public boolean matches(ServerPlayerEntity serverPlayerEntity, Entity entity) {
			return this.entity.test(serverPlayerEntity, entity);
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("entity", this.entity.serialize());
			return jsonObject;
		}
	}
}

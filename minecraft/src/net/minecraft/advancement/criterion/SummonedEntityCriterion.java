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

	public SummonedEntityCriterion.Conditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		EntityPredicate entityPredicate = EntityPredicate.fromJson(jsonObject.get("entity"));
		return new SummonedEntityCriterion.Conditions(entityPredicate);
	}

	public void trigger(ServerPlayerEntity player, Entity entity) {
		this.test(player.getAdvancementTracker(), conditions -> conditions.matches(player, entity));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final EntityPredicate entity;

		public Conditions(EntityPredicate entity) {
			super(SummonedEntityCriterion.ID);
			this.entity = entity;
		}

		public static SummonedEntityCriterion.Conditions create(EntityPredicate.Builder builder) {
			return new SummonedEntityCriterion.Conditions(builder.build());
		}

		public boolean matches(ServerPlayerEntity player, Entity entity) {
			return this.entity.test(player, entity);
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("entity", this.entity.toJson());
			return jsonObject;
		}
	}
}

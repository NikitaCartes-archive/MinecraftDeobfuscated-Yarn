package net.minecraft.advancement.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.LivingEntity;
import net.minecraft.predicate.entity.EntityEffectPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class EffectsChangedCriterion extends AbstractCriterion<EffectsChangedCriterion.Conditions> {
	private static final Identifier ID = new Identifier("effects_changed");

	@Override
	public Identifier getId() {
		return ID;
	}

	public EffectsChangedCriterion.Conditions method_8862(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		EntityEffectPredicate entityEffectPredicate = EntityEffectPredicate.deserialize(jsonObject.get("effects"));
		return new EffectsChangedCriterion.Conditions(entityEffectPredicate);
	}

	public void handle(ServerPlayerEntity serverPlayerEntity) {
		this.test(serverPlayerEntity.getAdvancementManager(), conditions -> conditions.matches(serverPlayerEntity));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final EntityEffectPredicate effects;

		public Conditions(EntityEffectPredicate entityEffectPredicate) {
			super(EffectsChangedCriterion.ID);
			this.effects = entityEffectPredicate;
		}

		public static EffectsChangedCriterion.Conditions create(EntityEffectPredicate entityEffectPredicate) {
			return new EffectsChangedCriterion.Conditions(entityEffectPredicate);
		}

		public boolean matches(ServerPlayerEntity serverPlayerEntity) {
			return this.effects.test((LivingEntity)serverPlayerEntity);
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("effects", this.effects.serialize());
			return jsonObject;
		}
	}
}

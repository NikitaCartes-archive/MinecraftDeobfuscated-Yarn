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

	public void trigger(ServerPlayerEntity player) {
		this.test(player.getAdvancementTracker(), conditions -> conditions.matches(player));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final EntityEffectPredicate effects;

		public Conditions(EntityEffectPredicate effects) {
			super(EffectsChangedCriterion.ID);
			this.effects = effects;
		}

		public static EffectsChangedCriterion.Conditions create(EntityEffectPredicate effects) {
			return new EffectsChangedCriterion.Conditions(effects);
		}

		public boolean matches(ServerPlayerEntity player) {
			return this.effects.test((LivingEntity)player);
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("effects", this.effects.serialize());
			return jsonObject;
		}
	}
}

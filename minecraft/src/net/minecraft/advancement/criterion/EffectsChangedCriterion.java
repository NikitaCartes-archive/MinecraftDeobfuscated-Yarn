package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import net.minecraft.entity.LivingEntity;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityEffectPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class EffectsChangedCriterion extends AbstractCriterion<EffectsChangedCriterion.Conditions> {
	private static final Identifier ID = new Identifier("effects_changed");

	@Override
	public Identifier getId() {
		return ID;
	}

	public EffectsChangedCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, EntityPredicate.Extended extended, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		EntityEffectPredicate entityEffectPredicate = EntityEffectPredicate.fromJson(jsonObject.get("effects"));
		return new EffectsChangedCriterion.Conditions(extended, entityEffectPredicate);
	}

	public void trigger(ServerPlayerEntity player) {
		this.test(player, conditions -> conditions.matches(player));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final EntityEffectPredicate effects;

		public Conditions(EntityPredicate.Extended player, EntityEffectPredicate effects) {
			super(EffectsChangedCriterion.ID, player);
			this.effects = effects;
		}

		public static EffectsChangedCriterion.Conditions create(EntityEffectPredicate effects) {
			return new EffectsChangedCriterion.Conditions(EntityPredicate.Extended.EMPTY, effects);
		}

		public boolean matches(ServerPlayerEntity player) {
			return this.effects.test((LivingEntity)player);
		}

		@Override
		public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
			JsonObject jsonObject = super.toJson(predicateSerializer);
			jsonObject.add("effects", this.effects.toJson());
			return jsonObject;
		}
	}
}

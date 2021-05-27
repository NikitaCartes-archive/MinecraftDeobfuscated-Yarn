package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityEffectPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class EffectsChangedCriterion extends AbstractCriterion<EffectsChangedCriterion.Conditions> {
	static final Identifier ID = new Identifier("effects_changed");

	@Override
	public Identifier getId() {
		return ID;
	}

	public EffectsChangedCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, EntityPredicate.Extended extended, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		EntityEffectPredicate entityEffectPredicate = EntityEffectPredicate.fromJson(jsonObject.get("effects"));
		EntityPredicate.Extended extended2 = EntityPredicate.Extended.getInJson(jsonObject, "source", advancementEntityPredicateDeserializer);
		return new EffectsChangedCriterion.Conditions(extended, entityEffectPredicate, extended2);
	}

	public void trigger(ServerPlayerEntity serverPlayerEntity, @Nullable Entity entity) {
		LootContext lootContext = entity != null ? EntityPredicate.createAdvancementEntityLootContext(serverPlayerEntity, entity) : null;
		this.test(serverPlayerEntity, conditions -> conditions.matches(serverPlayerEntity, lootContext));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final EntityEffectPredicate effects;
		private final EntityPredicate.Extended field_33909;

		public Conditions(EntityPredicate.Extended player, EntityEffectPredicate effects, EntityPredicate.Extended extended) {
			super(EffectsChangedCriterion.ID, player);
			this.effects = effects;
			this.field_33909 = extended;
		}

		public static EffectsChangedCriterion.Conditions create(EntityEffectPredicate effects) {
			return new EffectsChangedCriterion.Conditions(EntityPredicate.Extended.EMPTY, effects, EntityPredicate.Extended.EMPTY);
		}

		public static EffectsChangedCriterion.Conditions method_37224(EntityPredicate entityPredicate) {
			return new EffectsChangedCriterion.Conditions(
				EntityPredicate.Extended.EMPTY, EntityEffectPredicate.EMPTY, EntityPredicate.Extended.ofLegacy(entityPredicate)
			);
		}

		public boolean matches(ServerPlayerEntity player, @Nullable LootContext lootContext) {
			return !this.effects.test((LivingEntity)player)
				? false
				: this.field_33909 == EntityPredicate.Extended.EMPTY || lootContext != null && this.field_33909.test(lootContext);
		}

		@Override
		public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
			JsonObject jsonObject = super.toJson(predicateSerializer);
			jsonObject.add("effects", this.effects.toJson());
			jsonObject.add("source", this.field_33909.toJson(predicateSerializer));
			return jsonObject;
		}
	}
}

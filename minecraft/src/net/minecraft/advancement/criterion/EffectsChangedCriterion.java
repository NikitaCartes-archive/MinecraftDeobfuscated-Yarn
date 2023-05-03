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
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class EffectsChangedCriterion extends AbstractCriterion<EffectsChangedCriterion.Conditions> {
	static final Identifier ID = new Identifier("effects_changed");

	@Override
	public Identifier getId() {
		return ID;
	}

	public EffectsChangedCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, LootContextPredicate lootContextPredicate, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		EntityEffectPredicate entityEffectPredicate = EntityEffectPredicate.fromJson(jsonObject.get("effects"));
		LootContextPredicate lootContextPredicate2 = EntityPredicate.contextPredicateFromJson(jsonObject, "source", advancementEntityPredicateDeserializer);
		return new EffectsChangedCriterion.Conditions(lootContextPredicate, entityEffectPredicate, lootContextPredicate2);
	}

	public void trigger(ServerPlayerEntity player, @Nullable Entity source) {
		LootContext lootContext = source != null ? EntityPredicate.createAdvancementEntityLootContext(player, source) : null;
		this.trigger(player, conditions -> conditions.matches(player, lootContext));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final EntityEffectPredicate effects;
		private final LootContextPredicate source;

		public Conditions(LootContextPredicate player, EntityEffectPredicate effects, LootContextPredicate source) {
			super(EffectsChangedCriterion.ID, player);
			this.effects = effects;
			this.source = source;
		}

		public static EffectsChangedCriterion.Conditions create(EntityEffectPredicate effects) {
			return new EffectsChangedCriterion.Conditions(LootContextPredicate.EMPTY, effects, LootContextPredicate.EMPTY);
		}

		public static EffectsChangedCriterion.Conditions create(EntityPredicate source) {
			return new EffectsChangedCriterion.Conditions(LootContextPredicate.EMPTY, EntityEffectPredicate.EMPTY, EntityPredicate.asLootContextPredicate(source));
		}

		public boolean matches(ServerPlayerEntity player, @Nullable LootContext context) {
			return !this.effects.test((LivingEntity)player) ? false : this.source == LootContextPredicate.EMPTY || context != null && this.source.test(context);
		}

		@Override
		public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
			JsonObject jsonObject = super.toJson(predicateSerializer);
			jsonObject.add("effects", this.effects.toJson());
			jsonObject.add("source", this.source.toJson(predicateSerializer));
			return jsonObject;
		}
	}
}

package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.EntityEffectPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

public class EffectsChangedCriterion extends AbstractCriterion<EffectsChangedCriterion.Conditions> {
	public EffectsChangedCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, Optional<LootContextPredicate> optional, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		Optional<EntityEffectPredicate> optional2 = EntityEffectPredicate.fromJson(jsonObject.get("effects"));
		Optional<LootContextPredicate> optional3 = EntityPredicate.contextPredicateFromJson(jsonObject, "source", advancementEntityPredicateDeserializer);
		return new EffectsChangedCriterion.Conditions(optional, optional2, optional3);
	}

	public void trigger(ServerPlayerEntity player, @Nullable Entity source) {
		LootContext lootContext = source != null ? EntityPredicate.createAdvancementEntityLootContext(player, source) : null;
		this.trigger(player, conditions -> conditions.matches(player, lootContext));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final Optional<EntityEffectPredicate> effects;
		private final Optional<LootContextPredicate> source;

		public Conditions(Optional<LootContextPredicate> playerPredicate, Optional<EntityEffectPredicate> effects, Optional<LootContextPredicate> source) {
			super(playerPredicate);
			this.effects = effects;
			this.source = source;
		}

		public static AdvancementCriterion<EffectsChangedCriterion.Conditions> create(EntityEffectPredicate.Builder effects) {
			return Criteria.EFFECTS_CHANGED.create(new EffectsChangedCriterion.Conditions(Optional.empty(), effects.build(), Optional.empty()));
		}

		public static AdvancementCriterion<EffectsChangedCriterion.Conditions> create(EntityPredicate.Builder builder) {
			return Criteria.EFFECTS_CHANGED
				.create(new EffectsChangedCriterion.Conditions(Optional.empty(), Optional.empty(), Optional.of(EntityPredicate.asLootContextPredicate(builder.build()))));
		}

		public boolean matches(ServerPlayerEntity player, @Nullable LootContext context) {
			return this.effects.isPresent() && !((EntityEffectPredicate)this.effects.get()).test((LivingEntity)player)
				? false
				: !this.source.isPresent() || context != null && ((LootContextPredicate)this.source.get()).test(context);
		}

		@Override
		public JsonObject toJson() {
			JsonObject jsonObject = super.toJson();
			this.effects.ifPresent(entityEffectPredicate -> jsonObject.add("effects", entityEffectPredicate.toJson()));
			this.source.ifPresent(lootContextPredicate -> jsonObject.add("source", lootContextPredicate.toJson()));
			return jsonObject;
		}
	}
}

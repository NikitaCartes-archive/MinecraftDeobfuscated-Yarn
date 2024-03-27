package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.entity.LootContextPredicateValidator;
import net.minecraft.server.network.ServerPlayerEntity;

public class TameAnimalCriterion extends AbstractCriterion<TameAnimalCriterion.Conditions> {
	@Override
	public Codec<TameAnimalCriterion.Conditions> getConditionsCodec() {
		return TameAnimalCriterion.Conditions.CODEC;
	}

	public void trigger(ServerPlayerEntity player, AnimalEntity entity) {
		LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext(player, entity);
		this.trigger(player, conditions -> conditions.matches(lootContext));
	}

	public static record Conditions(Optional<LootContextPredicate> player, Optional<LootContextPredicate> entity) implements AbstractCriterion.Conditions {
		public static final Codec<TameAnimalCriterion.Conditions> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(TameAnimalCriterion.Conditions::player),
						EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("entity").forGetter(TameAnimalCriterion.Conditions::entity)
					)
					.apply(instance, TameAnimalCriterion.Conditions::new)
		);

		public static AdvancementCriterion<TameAnimalCriterion.Conditions> any() {
			return Criteria.TAME_ANIMAL.create(new TameAnimalCriterion.Conditions(Optional.empty(), Optional.empty()));
		}

		public static AdvancementCriterion<TameAnimalCriterion.Conditions> create(EntityPredicate.Builder entity) {
			return Criteria.TAME_ANIMAL
				.create(new TameAnimalCriterion.Conditions(Optional.empty(), Optional.of(EntityPredicate.contextPredicateFromEntityPredicate(entity))));
		}

		public boolean matches(LootContext entity) {
			return this.entity.isEmpty() || ((LootContextPredicate)this.entity.get()).test(entity);
		}

		@Override
		public void validate(LootContextPredicateValidator validator) {
			AbstractCriterion.Conditions.super.validate(validator);
			validator.validateEntityPredicate(this.entity, ".entity");
		}
	}
}

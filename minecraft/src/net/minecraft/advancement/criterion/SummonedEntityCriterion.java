package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.entity.Entity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.entity.LootContextPredicateValidator;
import net.minecraft.server.network.ServerPlayerEntity;

public class SummonedEntityCriterion extends AbstractCriterion<SummonedEntityCriterion.Conditions> {
	@Override
	public Codec<SummonedEntityCriterion.Conditions> getConditionsCodec() {
		return SummonedEntityCriterion.Conditions.CODEC;
	}

	public void trigger(ServerPlayerEntity player, Entity entity) {
		LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext(player, entity);
		this.trigger(player, conditions -> conditions.matches(lootContext));
	}

	public static record Conditions(Optional<LootContextPredicate> player, Optional<LootContextPredicate> entity) implements AbstractCriterion.Conditions {
		public static final Codec<SummonedEntityCriterion.Conditions> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(SummonedEntityCriterion.Conditions::player),
						EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("entity").forGetter(SummonedEntityCriterion.Conditions::entity)
					)
					.apply(instance, SummonedEntityCriterion.Conditions::new)
		);

		public static AdvancementCriterion<SummonedEntityCriterion.Conditions> create(EntityPredicate.Builder summonedEntityPredicateBuilder) {
			return Criteria.SUMMONED_ENTITY
				.create(
					new SummonedEntityCriterion.Conditions(Optional.empty(), Optional.of(EntityPredicate.contextPredicateFromEntityPredicate(summonedEntityPredicateBuilder)))
				);
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

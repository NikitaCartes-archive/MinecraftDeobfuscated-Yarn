package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

public class StartedRidingCriterion extends AbstractCriterion<StartedRidingCriterion.Conditions> {
	@Override
	public Codec<StartedRidingCriterion.Conditions> getConditionsCodec() {
		return StartedRidingCriterion.Conditions.CODEC;
	}

	public void trigger(ServerPlayerEntity player) {
		this.trigger(player, conditions -> true);
	}

	public static record Conditions(Optional<LootContextPredicate> player) implements AbstractCriterion.Conditions {
		public static final Codec<StartedRidingCriterion.Conditions> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(StartedRidingCriterion.Conditions::player))
					.apply(instance, StartedRidingCriterion.Conditions::new)
		);

		public static AdvancementCriterion<StartedRidingCriterion.Conditions> create(EntityPredicate.Builder player) {
			return Criteria.STARTED_RIDING.create(new StartedRidingCriterion.Conditions(Optional.of(EntityPredicate.contextPredicateFromEntityPredicate(player))));
		}
	}
}

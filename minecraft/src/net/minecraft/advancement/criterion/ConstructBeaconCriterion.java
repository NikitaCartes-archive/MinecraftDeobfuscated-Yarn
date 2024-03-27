package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

public class ConstructBeaconCriterion extends AbstractCriterion<ConstructBeaconCriterion.Conditions> {
	@Override
	public Codec<ConstructBeaconCriterion.Conditions> getConditionsCodec() {
		return ConstructBeaconCriterion.Conditions.CODEC;
	}

	public void trigger(ServerPlayerEntity player, int level) {
		this.trigger(player, conditions -> conditions.matches(level));
	}

	public static record Conditions(Optional<LootContextPredicate> player, NumberRange.IntRange level) implements AbstractCriterion.Conditions {
		public static final Codec<ConstructBeaconCriterion.Conditions> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(ConstructBeaconCriterion.Conditions::player),
						NumberRange.IntRange.CODEC.optionalFieldOf("level", NumberRange.IntRange.ANY).forGetter(ConstructBeaconCriterion.Conditions::level)
					)
					.apply(instance, ConstructBeaconCriterion.Conditions::new)
		);

		public static AdvancementCriterion<ConstructBeaconCriterion.Conditions> create() {
			return Criteria.CONSTRUCT_BEACON.create(new ConstructBeaconCriterion.Conditions(Optional.empty(), NumberRange.IntRange.ANY));
		}

		public static AdvancementCriterion<ConstructBeaconCriterion.Conditions> level(NumberRange.IntRange level) {
			return Criteria.CONSTRUCT_BEACON.create(new ConstructBeaconCriterion.Conditions(Optional.empty(), level));
		}

		public boolean matches(int level) {
			return this.level.test(level);
		}
	}
}

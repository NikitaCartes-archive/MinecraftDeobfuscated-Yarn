package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.DistancePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class LevitationCriterion extends AbstractCriterion<LevitationCriterion.Conditions> {
	@Override
	public Codec<LevitationCriterion.Conditions> getConditionsCodec() {
		return LevitationCriterion.Conditions.CODEC;
	}

	public void trigger(ServerPlayerEntity player, Vec3d startPos, int duration) {
		this.trigger(player, conditions -> conditions.matches(player, startPos, duration));
	}

	public static record Conditions(Optional<LootContextPredicate> player, Optional<DistancePredicate> distance, NumberRange.IntRange duration)
		implements AbstractCriterion.Conditions {
		public static final Codec<LevitationCriterion.Conditions> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(LevitationCriterion.Conditions::player),
						DistancePredicate.CODEC.optionalFieldOf("distance").forGetter(LevitationCriterion.Conditions::distance),
						NumberRange.IntRange.CODEC.optionalFieldOf("duration", NumberRange.IntRange.ANY).forGetter(LevitationCriterion.Conditions::duration)
					)
					.apply(instance, LevitationCriterion.Conditions::new)
		);

		public static AdvancementCriterion<LevitationCriterion.Conditions> create(DistancePredicate distance) {
			return Criteria.LEVITATION.create(new LevitationCriterion.Conditions(Optional.empty(), Optional.of(distance), NumberRange.IntRange.ANY));
		}

		public boolean matches(ServerPlayerEntity player, Vec3d distance, int duration) {
			return this.distance.isPresent()
					&& !((DistancePredicate)this.distance.get()).test(distance.x, distance.y, distance.z, player.getX(), player.getY(), player.getZ())
				? false
				: this.duration.test(duration);
		}
	}
}

package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.predicate.entity.DistancePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class TravelCriterion extends AbstractCriterion<TravelCriterion.Conditions> {
	@Override
	public Codec<TravelCriterion.Conditions> getConditionsCodec() {
		return TravelCriterion.Conditions.CODEC;
	}

	public void trigger(ServerPlayerEntity player, Vec3d startPos) {
		Vec3d vec3d = player.getPos();
		this.trigger(player, conditions -> conditions.matches(player.getServerWorld(), startPos, vec3d));
	}

	public static record Conditions(Optional<LootContextPredicate> player, Optional<LocationPredicate> startPosition, Optional<DistancePredicate> distance)
		implements AbstractCriterion.Conditions {
		public static final Codec<TravelCriterion.Conditions> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(TravelCriterion.Conditions::player),
						LocationPredicate.CODEC.optionalFieldOf("start_position").forGetter(TravelCriterion.Conditions::startPosition),
						DistancePredicate.CODEC.optionalFieldOf("distance").forGetter(TravelCriterion.Conditions::distance)
					)
					.apply(instance, TravelCriterion.Conditions::new)
		);

		public static AdvancementCriterion<TravelCriterion.Conditions> fallFromHeight(
			EntityPredicate.Builder entity, DistancePredicate distance, LocationPredicate.Builder startPos
		) {
			return Criteria.FALL_FROM_HEIGHT
				.create(
					new TravelCriterion.Conditions(
						Optional.of(EntityPredicate.contextPredicateFromEntityPredicate(entity)), Optional.of(startPos.build()), Optional.of(distance)
					)
				);
		}

		public static AdvancementCriterion<TravelCriterion.Conditions> rideEntityInLava(EntityPredicate.Builder entity, DistancePredicate distance) {
			return Criteria.RIDE_ENTITY_IN_LAVA
				.create(new TravelCriterion.Conditions(Optional.of(EntityPredicate.contextPredicateFromEntityPredicate(entity)), Optional.empty(), Optional.of(distance)));
		}

		public static AdvancementCriterion<TravelCriterion.Conditions> netherTravel(DistancePredicate distance) {
			return Criteria.NETHER_TRAVEL.create(new TravelCriterion.Conditions(Optional.empty(), Optional.empty(), Optional.of(distance)));
		}

		public boolean matches(ServerWorld world, Vec3d pos, Vec3d endPos) {
			return this.startPosition.isPresent() && !((LocationPredicate)this.startPosition.get()).test(world, pos.x, pos.y, pos.z)
				? false
				: !this.distance.isPresent() || ((DistancePredicate)this.distance.get()).test(pos.x, pos.y, pos.z, endPos.x, endPos.y, endPos.z);
		}
	}
}

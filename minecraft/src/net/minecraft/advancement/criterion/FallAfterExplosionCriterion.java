package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.entity.Entity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.DistancePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.entity.LootContextPredicateValidator;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.Vec3d;

public class FallAfterExplosionCriterion extends AbstractCriterion<FallAfterExplosionCriterion.Conditions> {
	@Override
	public Codec<FallAfterExplosionCriterion.Conditions> getConditionsCodec() {
		return FallAfterExplosionCriterion.Conditions.CODEC;
	}

	public void trigger(ServerPlayerEntity player, Vec3d startPosition, @Nullable Entity cause) {
		Vec3d vec3d = player.getPos();
		LootContext lootContext = cause != null ? EntityPredicate.createAdvancementEntityLootContext(player, cause) : null;
		this.trigger(player, conditions -> conditions.matches(player.getServerWorld(), startPosition, vec3d, lootContext));
	}

	public static record Conditions(
		Optional<LootContextPredicate> player, Optional<LocationPredicate> startPosition, Optional<DistancePredicate> distance, Optional<LootContextPredicate> cause
	) implements AbstractCriterion.Conditions {
		public static final Codec<FallAfterExplosionCriterion.Conditions> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Codecs.createStrictOptionalFieldCodec(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC, "player").forGetter(FallAfterExplosionCriterion.Conditions::player),
						Codecs.createStrictOptionalFieldCodec(LocationPredicate.CODEC, "start_position").forGetter(FallAfterExplosionCriterion.Conditions::startPosition),
						Codecs.createStrictOptionalFieldCodec(DistancePredicate.CODEC, "distance").forGetter(FallAfterExplosionCriterion.Conditions::distance),
						Codecs.createStrictOptionalFieldCodec(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC, "cause").forGetter(FallAfterExplosionCriterion.Conditions::cause)
					)
					.apply(instance, FallAfterExplosionCriterion.Conditions::new)
		);

		public static AdvancementCriterion<FallAfterExplosionCriterion.Conditions> create(DistancePredicate distance, EntityPredicate.Builder cause) {
			return Criteria.FALL_AFTER_EXPLOSION
				.create(
					new FallAfterExplosionCriterion.Conditions(
						Optional.empty(), Optional.empty(), Optional.of(distance), Optional.of(EntityPredicate.contextPredicateFromEntityPredicate(cause))
					)
				);
		}

		@Override
		public void validate(LootContextPredicateValidator validator) {
			AbstractCriterion.Conditions.super.validate(validator);
			validator.validateEntityPredicate(this.cause(), ".cause");
		}

		public boolean matches(ServerWorld world, Vec3d startPosition, Vec3d endPosition, @Nullable LootContext cause) {
			if (this.startPosition.isPresent() && !((LocationPredicate)this.startPosition.get()).test(world, startPosition.x, startPosition.y, startPosition.z)) {
				return false;
			} else {
				return this.distance.isPresent()
						&& !((DistancePredicate)this.distance.get()).test(startPosition.x, startPosition.y, startPosition.z, endPosition.x, endPosition.y, endPosition.z)
					? false
					: !this.cause.isPresent() || cause != null && ((LootContextPredicate)this.cause.get()).test(cause);
			}
		}
	}
}

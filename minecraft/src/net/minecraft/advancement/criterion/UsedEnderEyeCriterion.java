package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class UsedEnderEyeCriterion extends AbstractCriterion<UsedEnderEyeCriterion.Conditions> {
	@Override
	public Codec<UsedEnderEyeCriterion.Conditions> getConditionsCodec() {
		return UsedEnderEyeCriterion.Conditions.CODEC;
	}

	public void trigger(ServerPlayerEntity player, BlockPos strongholdPos) {
		double d = player.getX() - (double)strongholdPos.getX();
		double e = player.getZ() - (double)strongholdPos.getZ();
		double f = d * d + e * e;
		this.trigger(player, conditions -> conditions.matches(f));
	}

	public static record Conditions(Optional<LootContextPredicate> player, NumberRange.DoubleRange distance) implements AbstractCriterion.Conditions {
		public static final Codec<UsedEnderEyeCriterion.Conditions> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(UsedEnderEyeCriterion.Conditions::player),
						NumberRange.DoubleRange.CODEC.optionalFieldOf("distance", NumberRange.DoubleRange.ANY).forGetter(UsedEnderEyeCriterion.Conditions::distance)
					)
					.apply(instance, UsedEnderEyeCriterion.Conditions::new)
		);

		public boolean matches(double distance) {
			return this.distance.testSqrt(distance);
		}
	}
}

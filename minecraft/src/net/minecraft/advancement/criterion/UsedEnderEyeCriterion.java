package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import java.util.Optional;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class UsedEnderEyeCriterion extends AbstractCriterion<UsedEnderEyeCriterion.Conditions> {
	static final Identifier ID = new Identifier("used_ender_eye");

	@Override
	public Identifier getId() {
		return ID;
	}

	public UsedEnderEyeCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, Optional<LootContextPredicate> optional, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		NumberRange.FloatRange floatRange = NumberRange.FloatRange.fromJson(jsonObject.get("distance"));
		return new UsedEnderEyeCriterion.Conditions(optional, floatRange);
	}

	public void trigger(ServerPlayerEntity player, BlockPos strongholdPos) {
		double d = player.getX() - (double)strongholdPos.getX();
		double e = player.getZ() - (double)strongholdPos.getZ();
		double f = d * d + e * e;
		this.trigger(player, conditions -> conditions.matches(f));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final NumberRange.FloatRange distance;

		public Conditions(Optional<LootContextPredicate> playerPredicate, NumberRange.FloatRange distance) {
			super(UsedEnderEyeCriterion.ID, playerPredicate);
			this.distance = distance;
		}

		public boolean matches(double distance) {
			return this.distance.testSqrt(distance);
		}
	}
}

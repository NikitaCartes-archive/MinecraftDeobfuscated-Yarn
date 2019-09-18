package net.minecraft.advancement.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.predicate.NumberRange;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class UsedEnderEyeCriterion extends AbstractCriterion<UsedEnderEyeCriterion.Conditions> {
	private static final Identifier id = new Identifier("used_ender_eye");

	@Override
	public Identifier getId() {
		return id;
	}

	public UsedEnderEyeCriterion.Conditions method_9156(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		NumberRange.FloatRange floatRange = NumberRange.FloatRange.fromJson(jsonObject.get("distance"));
		return new UsedEnderEyeCriterion.Conditions(floatRange);
	}

	public void handle(ServerPlayerEntity serverPlayerEntity, BlockPos blockPos) {
		double d = serverPlayerEntity.x - (double)blockPos.getX();
		double e = serverPlayerEntity.z - (double)blockPos.getZ();
		double f = d * d + e * e;
		this.test(serverPlayerEntity.getAdvancementManager(), conditions -> conditions.matches(f));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final NumberRange.FloatRange distance;

		public Conditions(NumberRange.FloatRange floatRange) {
			super(UsedEnderEyeCriterion.id);
			this.distance = floatRange;
		}

		public boolean matches(double d) {
			return this.distance.testSqrt(d);
		}
	}
}

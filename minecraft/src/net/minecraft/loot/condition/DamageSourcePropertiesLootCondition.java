package net.minecraft.loot.condition;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.util.JsonSerializer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class DamageSourcePropertiesLootCondition implements LootCondition {
	private final DamageSourcePredicate predicate;

	private DamageSourcePropertiesLootCondition(DamageSourcePredicate predicate) {
		this.predicate = predicate;
	}

	@Override
	public LootConditionType getType() {
		return LootConditionTypes.DAMAGE_SOURCE_PROPERTIES;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return ImmutableSet.of(LootContextParameters.POSITION, LootContextParameters.DAMAGE_SOURCE);
	}

	public boolean test(LootContext lootContext) {
		DamageSource damageSource = lootContext.get(LootContextParameters.DAMAGE_SOURCE);
		BlockPos blockPos = lootContext.get(LootContextParameters.POSITION);
		return blockPos != null && damageSource != null && this.predicate.test(lootContext.getWorld(), Vec3d.of(blockPos), damageSource);
	}

	public static LootCondition.Builder builder(DamageSourcePredicate.Builder builder) {
		return () -> new DamageSourcePropertiesLootCondition(builder.build());
	}

	public static class Serializer implements JsonSerializer<DamageSourcePropertiesLootCondition> {
		public void toJson(
			JsonObject jsonObject, DamageSourcePropertiesLootCondition damageSourcePropertiesLootCondition, JsonSerializationContext jsonSerializationContext
		) {
			jsonObject.add("predicate", damageSourcePropertiesLootCondition.predicate.toJson());
		}

		public DamageSourcePropertiesLootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			DamageSourcePredicate damageSourcePredicate = DamageSourcePredicate.fromJson(jsonObject.get("predicate"));
			return new DamageSourcePropertiesLootCondition(damageSourcePredicate);
		}
	}
}

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
import net.minecraft.util.math.Vec3d;

public class DamageSourcePropertiesLootCondition implements LootCondition {
	private final DamageSourcePredicate predicate;

	private DamageSourcePropertiesLootCondition(DamageSourcePredicate predicate) {
		this.predicate = predicate;
	}

	@Override
	public LootConditionType getType() {
		return LootConditionTypes.field_25246;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return ImmutableSet.of(LootContextParameters.field_24424, LootContextParameters.field_1231);
	}

	public boolean method_834(LootContext lootContext) {
		DamageSource damageSource = lootContext.get(LootContextParameters.field_1231);
		Vec3d vec3d = lootContext.get(LootContextParameters.field_24424);
		return vec3d != null && damageSource != null && this.predicate.test(lootContext.getWorld(), vec3d, damageSource);
	}

	public static LootCondition.Builder builder(DamageSourcePredicate.Builder builder) {
		return () -> new DamageSourcePropertiesLootCondition(builder.build());
	}

	public static class Serializer implements JsonSerializer<DamageSourcePropertiesLootCondition> {
		public void method_838(
			JsonObject jsonObject, DamageSourcePropertiesLootCondition damageSourcePropertiesLootCondition, JsonSerializationContext jsonSerializationContext
		) {
			jsonObject.add("predicate", damageSourcePropertiesLootCondition.predicate.toJson());
		}

		public DamageSourcePropertiesLootCondition method_839(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			DamageSourcePredicate damageSourcePredicate = DamageSourcePredicate.fromJson(jsonObject.get("predicate"));
			return new DamageSourcePropertiesLootCondition(damageSourcePredicate);
		}
	}
}

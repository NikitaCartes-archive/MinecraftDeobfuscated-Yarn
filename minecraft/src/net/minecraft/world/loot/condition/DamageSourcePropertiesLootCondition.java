package net.minecraft.world.loot.condition;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.LootContextParameter;
import net.minecraft.world.loot.context.LootContextParameters;

public class DamageSourcePropertiesLootCondition implements LootCondition {
	private final DamageSourcePredicate predicate;

	private DamageSourcePropertiesLootCondition(DamageSourcePredicate damageSourcePredicate) {
		this.predicate = damageSourcePredicate;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return ImmutableSet.of(LootContextParameters.field_1232, LootContextParameters.field_1231);
	}

	public boolean method_834(LootContext lootContext) {
		DamageSource damageSource = lootContext.get(LootContextParameters.field_1231);
		BlockPos blockPos = lootContext.get(LootContextParameters.field_1232);
		return blockPos != null && damageSource != null && this.predicate.test(lootContext.getWorld(), new Vec3d(blockPos), damageSource);
	}

	public static LootCondition.Builder builder(DamageSourcePredicate.Builder builder) {
		return () -> new DamageSourcePropertiesLootCondition(builder.build());
	}

	public static class Factory extends LootCondition.Factory<DamageSourcePropertiesLootCondition> {
		protected Factory() {
			super(new Identifier("damage_source_properties"), DamageSourcePropertiesLootCondition.class);
		}

		public void method_838(
			JsonObject jsonObject, DamageSourcePropertiesLootCondition damageSourcePropertiesLootCondition, JsonSerializationContext jsonSerializationContext
		) {
			jsonObject.add("predicate", damageSourcePropertiesLootCondition.predicate.serialize());
		}

		public DamageSourcePropertiesLootCondition method_839(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			DamageSourcePredicate damageSourcePredicate = DamageSourcePredicate.deserialize(jsonObject.get("predicate"));
			return new DamageSourcePropertiesLootCondition(damageSourcePredicate);
		}
	}
}

package net.minecraft.world.loot.condition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.LootContextParameters;

public class LocationCheckLootCondition implements LootCondition {
	private final LocationPredicate predicate;

	private LocationCheckLootCondition(LocationPredicate locationPredicate) {
		this.predicate = locationPredicate;
	}

	public boolean method_881(LootContext lootContext) {
		BlockPos blockPos = lootContext.get(LootContextParameters.field_1232);
		return blockPos != null && this.predicate.test(lootContext.getWorld(), (float)blockPos.getX(), (float)blockPos.getY(), (float)blockPos.getZ());
	}

	public static LootCondition.Builder builder(LocationPredicate.Builder builder) {
		return () -> new LocationCheckLootCondition(builder.build());
	}

	public static class Factory extends LootCondition.Factory<LocationCheckLootCondition> {
		public Factory() {
			super(new Identifier("location_check"), LocationCheckLootCondition.class);
		}

		public void method_886(JsonObject jsonObject, LocationCheckLootCondition locationCheckLootCondition, JsonSerializationContext jsonSerializationContext) {
			jsonObject.add("predicate", locationCheckLootCondition.predicate.serialize());
		}

		public LocationCheckLootCondition method_885(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			LocationPredicate locationPredicate = LocationPredicate.deserialize(jsonObject.get("predicate"));
			return new LocationCheckLootCondition(locationPredicate);
		}
	}
}

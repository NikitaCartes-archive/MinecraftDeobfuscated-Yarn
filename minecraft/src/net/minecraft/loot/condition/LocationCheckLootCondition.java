package net.minecraft.loot.condition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.loot.condition.LootCondition;

public class LocationCheckLootCondition implements LootCondition {
	private final LocationPredicate predicate;

	private LocationCheckLootCondition(LocationPredicate predicate) {
		this.predicate = predicate;
	}

	public boolean test(LootContext lootContext) {
		BlockPos blockPos = lootContext.get(LootContextParameters.POSITION);
		return blockPos != null && this.predicate.test(lootContext.getWorld(), (float)blockPos.getX(), (float)blockPos.getY(), (float)blockPos.getZ());
	}

	public static LootCondition.Builder builder(LocationPredicate.Builder predicateBuilder) {
		return () -> new LocationCheckLootCondition(predicateBuilder.build());
	}

	public static class Factory extends LootCondition.Factory<LocationCheckLootCondition> {
		public Factory() {
			super(new Identifier("location_check"), LocationCheckLootCondition.class);
		}

		public void toJson(JsonObject jsonObject, LocationCheckLootCondition locationCheckLootCondition, JsonSerializationContext jsonSerializationContext) {
			jsonObject.add("predicate", locationCheckLootCondition.predicate.toJson());
		}

		public LocationCheckLootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			LocationPredicate locationPredicate = LocationPredicate.fromJson(jsonObject.get("predicate"));
			return new LocationCheckLootCondition(locationPredicate);
		}
	}
}

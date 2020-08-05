package net.minecraft.loot.condition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.JsonSerializer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class LocationCheckLootCondition implements LootCondition {
	private final LocationPredicate predicate;
	private final BlockPos offset;

	private LocationCheckLootCondition(LocationPredicate predicate, BlockPos offset) {
		this.predicate = predicate;
		this.offset = offset;
	}

	@Override
	public LootConditionType getType() {
		return LootConditionTypes.LOCATION_CHECK;
	}

	public boolean test(LootContext lootContext) {
		Vec3d vec3d = lootContext.get(LootContextParameters.ORIGIN);
		return vec3d != null
			&& this.predicate
				.test(
					lootContext.getWorld(), vec3d.getX() + (double)this.offset.getX(), vec3d.getY() + (double)this.offset.getY(), vec3d.getZ() + (double)this.offset.getZ()
				);
	}

	public static LootCondition.Builder builder(LocationPredicate.Builder predicateBuilder) {
		return () -> new LocationCheckLootCondition(predicateBuilder.build(), BlockPos.ORIGIN);
	}

	public static LootCondition.Builder method_30151(LocationPredicate.Builder builder, BlockPos blockPos) {
		return () -> new LocationCheckLootCondition(builder.build(), blockPos);
	}

	public static class Serializer implements JsonSerializer<LocationCheckLootCondition> {
		public void toJson(JsonObject jsonObject, LocationCheckLootCondition locationCheckLootCondition, JsonSerializationContext jsonSerializationContext) {
			jsonObject.add("predicate", locationCheckLootCondition.predicate.toJson());
			if (locationCheckLootCondition.offset.getX() != 0) {
				jsonObject.addProperty("offsetX", locationCheckLootCondition.offset.getX());
			}

			if (locationCheckLootCondition.offset.getY() != 0) {
				jsonObject.addProperty("offsetY", locationCheckLootCondition.offset.getY());
			}

			if (locationCheckLootCondition.offset.getZ() != 0) {
				jsonObject.addProperty("offsetZ", locationCheckLootCondition.offset.getZ());
			}
		}

		public LocationCheckLootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			LocationPredicate locationPredicate = LocationPredicate.fromJson(jsonObject.get("predicate"));
			int i = JsonHelper.getInt(jsonObject, "offsetX", 0);
			int j = JsonHelper.getInt(jsonObject, "offsetY", 0);
			int k = JsonHelper.getInt(jsonObject, "offsetZ", 0);
			return new LocationCheckLootCondition(locationPredicate, new BlockPos(i, j, k));
		}
	}
}

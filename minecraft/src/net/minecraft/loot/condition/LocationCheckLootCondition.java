package net.minecraft.loot.condition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.BlockPos;

public class LocationCheckLootCondition implements LootCondition {
	private final LocationPredicate predicate;
	private final BlockPos offset;

	public LocationCheckLootCondition(LocationPredicate locationPredicate, BlockPos blockPos) {
		this.predicate = locationPredicate;
		this.offset = blockPos;
	}

	public boolean method_881(LootContext lootContext) {
		BlockPos blockPos = lootContext.get(LootContextParameters.POSITION);
		return blockPos != null
			&& this.predicate
				.test(
					lootContext.getWorld(),
					(float)(blockPos.getX() + this.offset.getX()),
					(float)(blockPos.getY() + this.offset.getY()),
					(float)(blockPos.getZ() + this.offset.getZ())
				);
	}

	public static LootCondition.Builder builder(LocationPredicate.Builder builder) {
		return () -> new LocationCheckLootCondition(builder.build(), BlockPos.ORIGIN);
	}

	public static class Factory extends LootCondition.Factory<LocationCheckLootCondition> {
		public Factory() {
			super(new Identifier("location_check"), LocationCheckLootCondition.class);
		}

		public void method_886(JsonObject jsonObject, LocationCheckLootCondition locationCheckLootCondition, JsonSerializationContext jsonSerializationContext) {
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

		public LocationCheckLootCondition method_885(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			LocationPredicate locationPredicate = LocationPredicate.fromJson(jsonObject.get("predicate"));
			int i = JsonHelper.getInt(jsonObject, "offsetX", 0);
			int j = JsonHelper.getInt(jsonObject, "offsetY", 0);
			int k = JsonHelper.getInt(jsonObject, "offsetZ", 0);
			return new LocationCheckLootCondition(locationPredicate, new BlockPos(i, j, k));
		}
	}
}

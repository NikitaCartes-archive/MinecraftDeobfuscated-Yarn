/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.condition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.condition.LootConditionTypes;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.JsonSerializer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class LocationCheckLootCondition
implements LootCondition {
    final LocationPredicate predicate;
    final BlockPos offset;

    LocationCheckLootCondition(LocationPredicate predicate, BlockPos offset) {
        this.predicate = predicate;
        this.offset = offset;
    }

    @Override
    public LootConditionType getType() {
        return LootConditionTypes.LOCATION_CHECK;
    }

    @Override
    public boolean test(LootContext lootContext) {
        Vec3d vec3d = lootContext.get(LootContextParameters.ORIGIN);
        return vec3d != null && this.predicate.test(lootContext.getWorld(), vec3d.getX() + (double)this.offset.getX(), vec3d.getY() + (double)this.offset.getY(), vec3d.getZ() + (double)this.offset.getZ());
    }

    public static LootCondition.Builder builder(LocationPredicate.Builder predicateBuilder) {
        return () -> new LocationCheckLootCondition(predicateBuilder.build(), BlockPos.ORIGIN);
    }

    public static LootCondition.Builder builder(LocationPredicate.Builder predicateBuilder, BlockPos pos) {
        return () -> new LocationCheckLootCondition(predicateBuilder.build(), pos);
    }

    @Override
    public /* synthetic */ boolean test(Object context) {
        return this.test((LootContext)context);
    }

    public static class Serializer
    implements JsonSerializer<LocationCheckLootCondition> {
        @Override
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

        @Override
        public LocationCheckLootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            LocationPredicate locationPredicate = LocationPredicate.fromJson(jsonObject.get("predicate"));
            int i = JsonHelper.getInt(jsonObject, "offsetX", 0);
            int j = JsonHelper.getInt(jsonObject, "offsetY", 0);
            int k = JsonHelper.getInt(jsonObject, "offsetZ", 0);
            return new LocationCheckLootCondition(locationPredicate, new BlockPos(i, j, k));
        }

        @Override
        public /* synthetic */ Object fromJson(JsonObject json, JsonDeserializationContext context) {
            return this.fromJson(json, context);
        }
    }
}


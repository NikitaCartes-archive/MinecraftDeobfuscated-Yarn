/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.loot.condition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.class_4570;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.LootContextParameters;

public class LocationCheckLootCondition
implements class_4570 {
    private final LocationPredicate predicate;
    private final BlockPos field_20765;

    public LocationCheckLootCondition(LocationPredicate locationPredicate, BlockPos blockPos) {
        this.predicate = locationPredicate;
        this.field_20765 = blockPos;
    }

    public boolean method_881(LootContext lootContext) {
        BlockPos blockPos = lootContext.get(LootContextParameters.POSITION);
        return blockPos != null && this.predicate.test(lootContext.getWorld(), blockPos.getX() + this.field_20765.getX(), blockPos.getY() + this.field_20765.getY(), blockPos.getZ() + this.field_20765.getZ());
    }

    public static class_4570.Builder builder(LocationPredicate.Builder builder) {
        return () -> new LocationCheckLootCondition(builder.build(), BlockPos.ORIGIN);
    }

    @Override
    public /* synthetic */ boolean test(Object object) {
        return this.method_881((LootContext)object);
    }

    public static class Factory
    extends class_4570.Factory<LocationCheckLootCondition> {
        public Factory() {
            super(new Identifier("location_check"), LocationCheckLootCondition.class);
        }

        public void method_886(JsonObject jsonObject, LocationCheckLootCondition locationCheckLootCondition, JsonSerializationContext jsonSerializationContext) {
            jsonObject.add("predicate", locationCheckLootCondition.predicate.serialize());
            if (locationCheckLootCondition.field_20765.getX() != 0) {
                jsonObject.addProperty("offsetX", locationCheckLootCondition.field_20765.getX());
            }
            if (locationCheckLootCondition.field_20765.getY() != 0) {
                jsonObject.addProperty("offsetY", locationCheckLootCondition.field_20765.getY());
            }
            if (locationCheckLootCondition.field_20765.getZ() != 0) {
                jsonObject.addProperty("offsetZ", locationCheckLootCondition.field_20765.getZ());
            }
        }

        public LocationCheckLootCondition method_885(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            LocationPredicate locationPredicate = LocationPredicate.deserialize(jsonObject.get("predicate"));
            int i = JsonHelper.getInt(jsonObject, "offsetX", 0);
            int j = JsonHelper.getInt(jsonObject, "offsetY", 0);
            int k = JsonHelper.getInt(jsonObject, "offsetZ", 0);
            return new LocationCheckLootCondition(locationPredicate, new BlockPos(i, j, k));
        }

        @Override
        public /* synthetic */ class_4570 fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return this.method_885(jsonObject, jsonDeserializationContext);
        }
    }
}


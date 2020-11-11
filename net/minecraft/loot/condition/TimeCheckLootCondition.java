/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.condition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.condition.LootConditionTypes;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.operator.BoundedIntUnaryOperator;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.JsonSerializer;
import org.jetbrains.annotations.Nullable;

public class TimeCheckLootCondition
implements LootCondition {
    @Nullable
    private final Long period;
    private final BoundedIntUnaryOperator value;

    private TimeCheckLootCondition(@Nullable Long period, BoundedIntUnaryOperator value) {
        this.period = period;
        this.value = value;
    }

    @Override
    public LootConditionType getType() {
        return LootConditionTypes.TIME_CHECK;
    }

    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return this.value.method_32386();
    }

    @Override
    public boolean test(LootContext lootContext) {
        ServerWorld serverWorld = lootContext.getWorld();
        long l = serverWorld.getTimeOfDay();
        if (this.period != null) {
            l %= this.period.longValue();
        }
        return this.value.method_32393(lootContext, (int)l);
    }

    @Override
    public /* synthetic */ boolean test(Object context) {
        return this.test((LootContext)context);
    }

    public static class Serializer
    implements JsonSerializer<TimeCheckLootCondition> {
        @Override
        public void toJson(JsonObject jsonObject, TimeCheckLootCondition timeCheckLootCondition, JsonSerializationContext jsonSerializationContext) {
            jsonObject.addProperty("period", timeCheckLootCondition.period);
            jsonObject.add("value", jsonSerializationContext.serialize(timeCheckLootCondition.value));
        }

        @Override
        public TimeCheckLootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            Long long_ = jsonObject.has("period") ? Long.valueOf(JsonHelper.getLong(jsonObject, "period")) : null;
            BoundedIntUnaryOperator boundedIntUnaryOperator = JsonHelper.deserialize(jsonObject, "value", jsonDeserializationContext, BoundedIntUnaryOperator.class);
            return new TimeCheckLootCondition(long_, boundedIntUnaryOperator);
        }

        @Override
        public /* synthetic */ Object fromJson(JsonObject json, JsonDeserializationContext context) {
            return this.fromJson(json, context);
        }
    }
}


/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.condition;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.condition.LootConditionTypes;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.util.JsonSerializer;
import net.minecraft.util.math.Vec3d;

public class DamageSourcePropertiesLootCondition
implements LootCondition {
    final DamageSourcePredicate predicate;

    DamageSourcePropertiesLootCondition(DamageSourcePredicate damageSourcePredicate) {
        this.predicate = damageSourcePredicate;
    }

    @Override
    public LootConditionType getType() {
        return LootConditionTypes.DAMAGE_SOURCE_PROPERTIES;
    }

    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return ImmutableSet.of(LootContextParameters.ORIGIN, LootContextParameters.DAMAGE_SOURCE);
    }

    @Override
    public boolean test(LootContext lootContext) {
        DamageSource damageSource = lootContext.get(LootContextParameters.DAMAGE_SOURCE);
        Vec3d vec3d = lootContext.get(LootContextParameters.ORIGIN);
        return vec3d != null && damageSource != null && this.predicate.test(lootContext.getWorld(), vec3d, damageSource);
    }

    public static LootCondition.Builder builder(DamageSourcePredicate.Builder builder) {
        return () -> new DamageSourcePropertiesLootCondition(builder.build());
    }

    @Override
    public /* synthetic */ boolean test(Object context) {
        return this.test((LootContext)context);
    }

    public static class Serializer
    implements JsonSerializer<DamageSourcePropertiesLootCondition> {
        @Override
        public void toJson(JsonObject jsonObject, DamageSourcePropertiesLootCondition damageSourcePropertiesLootCondition, JsonSerializationContext jsonSerializationContext) {
            jsonObject.add("predicate", damageSourcePropertiesLootCondition.predicate.toJson());
        }

        @Override
        public DamageSourcePropertiesLootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            DamageSourcePredicate damageSourcePredicate = DamageSourcePredicate.fromJson(jsonObject.get("predicate"));
            return new DamageSourcePropertiesLootCondition(damageSourcePredicate);
        }

        @Override
        public /* synthetic */ Object fromJson(JsonObject json, JsonDeserializationContext context) {
            return this.fromJson(json, context);
        }
    }
}


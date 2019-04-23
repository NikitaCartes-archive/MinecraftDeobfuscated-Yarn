/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.loot.condition;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.Identifier;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.LootContextParameter;
import net.minecraft.world.loot.context.LootContextParameters;

public class SurvivesExplosionLootCondition
implements LootCondition {
    private static final SurvivesExplosionLootCondition INSTANCE = new SurvivesExplosionLootCondition();

    private SurvivesExplosionLootCondition() {
    }

    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return ImmutableSet.of(LootContextParameters.EXPLOSION_RADIUS);
    }

    public boolean method_869(LootContext lootContext) {
        Float float_ = lootContext.get(LootContextParameters.EXPLOSION_RADIUS);
        if (float_ != null) {
            Random random = lootContext.getRandom();
            float f = 1.0f / float_.floatValue();
            return random.nextFloat() <= f;
        }
        return true;
    }

    public static LootCondition.Builder builder() {
        return () -> INSTANCE;
    }

    @Override
    public /* synthetic */ boolean test(Object object) {
        return this.method_869((LootContext)object);
    }

    public static class Factory
    extends LootCondition.Factory<SurvivesExplosionLootCondition> {
        protected Factory() {
            super(new Identifier("survives_explosion"), SurvivesExplosionLootCondition.class);
        }

        public void method_874(JsonObject jsonObject, SurvivesExplosionLootCondition survivesExplosionLootCondition, JsonSerializationContext jsonSerializationContext) {
        }

        public SurvivesExplosionLootCondition method_873(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return INSTANCE;
        }

        @Override
        public /* synthetic */ LootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return this.method_873(jsonObject, jsonDeserializationContext);
        }
    }
}


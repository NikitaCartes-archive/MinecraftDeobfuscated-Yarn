/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.loot.condition;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.class_4570;
import net.minecraft.util.Identifier;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.LootContextParameter;
import net.minecraft.world.loot.context.LootContextParameters;

public class KilledByPlayerLootCondition
implements class_4570 {
    private static final KilledByPlayerLootCondition INSTANCE = new KilledByPlayerLootCondition();

    private KilledByPlayerLootCondition() {
    }

    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return ImmutableSet.of(LootContextParameters.LAST_DAMAGE_PLAYER);
    }

    public boolean method_938(LootContext lootContext) {
        return lootContext.hasParameter(LootContextParameters.LAST_DAMAGE_PLAYER);
    }

    public static class_4570.Builder builder() {
        return () -> INSTANCE;
    }

    @Override
    public /* synthetic */ boolean test(Object object) {
        return this.method_938((LootContext)object);
    }

    public static class Factory
    extends class_4570.Factory<KilledByPlayerLootCondition> {
        protected Factory() {
            super(new Identifier("killed_by_player"), KilledByPlayerLootCondition.class);
        }

        public void method_942(JsonObject jsonObject, KilledByPlayerLootCondition killedByPlayerLootCondition, JsonSerializationContext jsonSerializationContext) {
        }

        public KilledByPlayerLootCondition method_943(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return INSTANCE;
        }

        @Override
        public /* synthetic */ class_4570 fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return this.method_943(jsonObject, jsonDeserializationContext);
        }
    }
}


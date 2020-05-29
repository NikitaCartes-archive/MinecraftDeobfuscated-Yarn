/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.condition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.condition.LootConditionTypes;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.JsonSerializable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReferenceLootCondition
implements LootCondition {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Identifier id;

    private ReferenceLootCondition(Identifier id) {
        this.id = id;
    }

    @Override
    public LootConditionType method_29325() {
        return LootConditionTypes.REFERENCE;
    }

    @Override
    public void validate(LootTableReporter reporter) {
        if (reporter.hasCondition(this.id)) {
            reporter.report("Condition " + this.id + " is recursively called");
            return;
        }
        LootCondition.super.validate(reporter);
        LootCondition lootCondition = reporter.getCondition(this.id);
        if (lootCondition == null) {
            reporter.report("Unknown condition table called " + this.id);
        } else {
            lootCondition.validate(reporter.withTable(".{" + this.id + "}", this.id));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean test(LootContext lootContext) {
        LootCondition lootCondition = lootContext.getCondition(this.id);
        if (lootContext.addCondition(lootCondition)) {
            try {
                boolean bl = lootCondition.test(lootContext);
                return bl;
            } finally {
                lootContext.removeCondition(lootCondition);
            }
        }
        LOGGER.warn("Detected infinite loop in loot tables");
        return false;
    }

    @Override
    public /* synthetic */ boolean test(Object context) {
        return this.test((LootContext)context);
    }

    public static class Factory
    implements JsonSerializable<ReferenceLootCondition> {
        @Override
        public void toJson(JsonObject jsonObject, ReferenceLootCondition referenceLootCondition, JsonSerializationContext jsonSerializationContext) {
            jsonObject.addProperty("name", referenceLootCondition.id.toString());
        }

        @Override
        public ReferenceLootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "name"));
            return new ReferenceLootCondition(identifier);
        }

        @Override
        public /* synthetic */ Object fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return this.fromJson(jsonObject, jsonDeserializationContext);
        }
    }
}


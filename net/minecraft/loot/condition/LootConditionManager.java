/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.condition;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.loot.LootGsons;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.condition.LootConditionTypes;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class LootConditionManager
extends JsonDataLoader {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = LootGsons.getConditionGsonBuilder().create();
    private Map<Identifier, LootCondition> conditions = ImmutableMap.of();

    public LootConditionManager() {
        super(GSON, "predicates");
    }

    @Nullable
    public LootCondition get(Identifier id) {
        return this.conditions.get(id);
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> map, ResourceManager resourceManager, Profiler profiler) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        map.forEach((identifier, jsonElement) -> {
            try {
                if (jsonElement.isJsonArray()) {
                    LootCondition[] lootConditions = GSON.fromJson((JsonElement)jsonElement, LootCondition[].class);
                    builder.put(identifier, new class_5334(lootConditions));
                } else {
                    LootCondition lootCondition = GSON.fromJson((JsonElement)jsonElement, LootCondition.class);
                    builder.put(identifier, lootCondition);
                }
            } catch (Exception exception) {
                LOGGER.error("Couldn't parse loot table {}", identifier, (Object)exception);
            }
        });
        ImmutableMap<Identifier, LootCondition> map2 = builder.build();
        LootTableReporter lootTableReporter = new LootTableReporter(LootContextTypes.GENERIC, map2::get, identifier -> null);
        map2.forEach((identifier, lootCondition) -> lootCondition.validate(lootTableReporter.withCondition("{" + identifier + "}", (Identifier)identifier)));
        lootTableReporter.getMessages().forEach((string, string2) -> LOGGER.warn("Found validation problem in " + string + ": " + string2));
        this.conditions = map2;
    }

    public Set<Identifier> getIds() {
        return Collections.unmodifiableSet(this.conditions.keySet());
    }

    static class class_5334
    implements LootCondition {
        private final LootCondition[] field_25202;
        private final Predicate<LootContext> field_25203;

        private class_5334(LootCondition[] lootConditions) {
            this.field_25202 = lootConditions;
            this.field_25203 = LootConditionTypes.joinAnd(lootConditions);
        }

        @Override
        public final boolean test(LootContext lootContext) {
            return this.field_25203.test(lootContext);
        }

        @Override
        public void validate(LootTableReporter reporter) {
            LootCondition.super.validate(reporter);
            for (int i = 0; i < this.field_25202.length; ++i) {
                this.field_25202[i].validate(reporter.makeChild(".term[" + i + "]"));
            }
        }

        @Override
        public LootConditionType method_29325() {
            throw new UnsupportedOperationException();
        }

        @Override
        public /* synthetic */ boolean test(Object object) {
            return this.test((LootContext)object);
        }
    }
}


/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.condition;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditions;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class AlternativeLootCondition
implements LootCondition {
    private final LootCondition[] terms;
    private final Predicate<LootContext> predicate;

    private AlternativeLootCondition(LootCondition[] terms) {
        this.terms = terms;
        this.predicate = LootConditions.joinOr(terms);
    }

    @Override
    public final boolean test(LootContext lootContext) {
        return this.predicate.test(lootContext);
    }

    @Override
    public void validate(LootTableReporter reporter) {
        LootCondition.super.validate(reporter);
        for (int i = 0; i < this.terms.length; ++i) {
            this.terms[i].validate(reporter.makeChild(".term[" + i + "]"));
        }
    }

    public static Builder builder(LootCondition.Builder ... terms) {
        return new Builder(terms);
    }

    @Override
    public /* synthetic */ boolean test(Object context) {
        return this.test((LootContext)context);
    }

    public static class Factory
    extends LootCondition.Factory<AlternativeLootCondition> {
        public Factory() {
            super(new Identifier("alternative"), AlternativeLootCondition.class);
        }

        @Override
        public void toJson(JsonObject jsonObject, AlternativeLootCondition alternativeLootCondition, JsonSerializationContext jsonSerializationContext) {
            jsonObject.add("terms", jsonSerializationContext.serialize(alternativeLootCondition.terms));
        }

        @Override
        public AlternativeLootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            LootCondition[] lootConditions = JsonHelper.deserialize(jsonObject, "terms", jsonDeserializationContext, LootCondition[].class);
            return new AlternativeLootCondition(lootConditions);
        }

        @Override
        public /* synthetic */ LootCondition fromJson(JsonObject json, JsonDeserializationContext context) {
            return this.fromJson(json, context);
        }
    }

    public static class Builder
    implements LootCondition.Builder {
        private final List<LootCondition> terms = Lists.newArrayList();

        public Builder(LootCondition.Builder ... terms) {
            for (LootCondition.Builder builder : terms) {
                this.terms.add(builder.build());
            }
        }

        @Override
        public Builder or(LootCondition.Builder condition) {
            this.terms.add(condition.build());
            return this;
        }

        @Override
        public LootCondition build() {
            return new AlternativeLootCondition(this.terms.toArray(new LootCondition[0]));
        }
    }
}


/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.provider.number;

import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderType;
import net.minecraft.loot.provider.number.LootNumberProviderTypes;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.JsonSerializer;
import net.minecraft.util.math.random.AbstractRandom;

public final class BinomialLootNumberProvider
implements LootNumberProvider {
    final LootNumberProvider n;
    final LootNumberProvider p;

    BinomialLootNumberProvider(LootNumberProvider n, LootNumberProvider p) {
        this.n = n;
        this.p = p;
    }

    @Override
    public LootNumberProviderType getType() {
        return LootNumberProviderTypes.BINOMIAL;
    }

    @Override
    public int nextInt(LootContext context) {
        int i = this.n.nextInt(context);
        float f = this.p.nextFloat(context);
        AbstractRandom abstractRandom = context.getRandom();
        int j = 0;
        for (int k = 0; k < i; ++k) {
            if (!(abstractRandom.nextFloat() < f)) continue;
            ++j;
        }
        return j;
    }

    @Override
    public float nextFloat(LootContext context) {
        return this.nextInt(context);
    }

    public static BinomialLootNumberProvider create(int n, float p) {
        return new BinomialLootNumberProvider(ConstantLootNumberProvider.create(n), ConstantLootNumberProvider.create(p));
    }

    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return Sets.union(this.n.getRequiredParameters(), this.p.getRequiredParameters());
    }

    public static class Serializer
    implements JsonSerializer<BinomialLootNumberProvider> {
        @Override
        public BinomialLootNumberProvider fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            LootNumberProvider lootNumberProvider = JsonHelper.deserialize(jsonObject, "n", jsonDeserializationContext, LootNumberProvider.class);
            LootNumberProvider lootNumberProvider2 = JsonHelper.deserialize(jsonObject, "p", jsonDeserializationContext, LootNumberProvider.class);
            return new BinomialLootNumberProvider(lootNumberProvider, lootNumberProvider2);
        }

        @Override
        public void toJson(JsonObject jsonObject, BinomialLootNumberProvider binomialLootNumberProvider, JsonSerializationContext jsonSerializationContext) {
            jsonObject.add("n", jsonSerializationContext.serialize(binomialLootNumberProvider.n));
            jsonObject.add("p", jsonSerializationContext.serialize(binomialLootNumberProvider.p));
        }

        @Override
        public /* synthetic */ Object fromJson(JsonObject json, JsonDeserializationContext context) {
            return this.fromJson(json, context);
        }
    }
}


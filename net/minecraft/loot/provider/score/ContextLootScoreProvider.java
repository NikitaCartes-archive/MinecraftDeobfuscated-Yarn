/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.provider.score;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.lang.reflect.Type;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.provider.score.LootScoreProvider;
import net.minecraft.loot.provider.score.LootScoreProviderType;
import net.minecraft.loot.provider.score.LootScoreProviderTypes;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.JsonSerializer;
import net.minecraft.util.JsonSerializing;
import org.jetbrains.annotations.Nullable;

public class ContextLootScoreProvider
implements LootScoreProvider {
    private final LootContext.EntityTarget target;

    private ContextLootScoreProvider(LootContext.EntityTarget target) {
        this.target = target;
    }

    public static LootScoreProvider create(LootContext.EntityTarget target) {
        return new ContextLootScoreProvider(target);
    }

    @Override
    public LootScoreProviderType getType() {
        return LootScoreProviderTypes.CONTEXT;
    }

    @Override
    @Nullable
    public String getName(LootContext context) {
        Entity entity = context.get(this.target.getParameter());
        return entity != null ? entity.getEntityName() : null;
    }

    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return ImmutableSet.of(this.target.getParameter());
    }

    public static class CustomSerializer
    implements JsonSerializing.CustomSerializer<ContextLootScoreProvider> {
        @Override
        public JsonElement toJson(ContextLootScoreProvider contextLootScoreProvider, JsonSerializationContext jsonSerializationContext) {
            return jsonSerializationContext.serialize((Object)contextLootScoreProvider.target);
        }

        @Override
        public ContextLootScoreProvider fromJson(JsonElement jsonElement, JsonDeserializationContext jsonDeserializationContext) {
            LootContext.EntityTarget entityTarget = (LootContext.EntityTarget)((Object)jsonDeserializationContext.deserialize(jsonElement, (Type)((Object)LootContext.EntityTarget.class)));
            return new ContextLootScoreProvider(entityTarget);
        }

        @Override
        public /* synthetic */ Object fromJson(JsonElement json, JsonDeserializationContext context) {
            return this.fromJson(json, context);
        }
    }

    public static class Serializer
    implements JsonSerializer<ContextLootScoreProvider> {
        @Override
        public void toJson(JsonObject jsonObject, ContextLootScoreProvider contextLootScoreProvider, JsonSerializationContext jsonSerializationContext) {
            jsonObject.addProperty("target", contextLootScoreProvider.target.name());
        }

        @Override
        public ContextLootScoreProvider fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            LootContext.EntityTarget entityTarget = JsonHelper.deserialize(jsonObject, "target", jsonDeserializationContext, LootContext.EntityTarget.class);
            return new ContextLootScoreProvider(entityTarget);
        }

        @Override
        public /* synthetic */ Object fromJson(JsonObject json, JsonDeserializationContext context) {
            return this.fromJson(json, context);
        }
    }
}


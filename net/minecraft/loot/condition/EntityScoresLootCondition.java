/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.condition;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.entity.Entity;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.condition.LootConditionTypes;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.operator.BoundedIntUnaryOperator;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.JsonSerializer;

public class EntityScoresLootCondition
implements LootCondition {
    private final Map<String, BoundedIntUnaryOperator> scores;
    private final LootContext.EntityTarget target;

    private EntityScoresLootCondition(Map<String, BoundedIntUnaryOperator> scores, LootContext.EntityTarget target) {
        this.scores = ImmutableMap.copyOf(scores);
        this.target = target;
    }

    @Override
    public LootConditionType getType() {
        return LootConditionTypes.ENTITY_SCORES;
    }

    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return Stream.concat(Stream.of(this.target.getParameter()), this.scores.values().stream().flatMap(boundedIntUnaryOperator -> boundedIntUnaryOperator.method_32386().stream())).collect(ImmutableSet.toImmutableSet());
    }

    @Override
    public boolean test(LootContext lootContext) {
        Entity entity = lootContext.get(this.target.getParameter());
        if (entity == null) {
            return false;
        }
        Scoreboard scoreboard = entity.world.getScoreboard();
        for (Map.Entry<String, BoundedIntUnaryOperator> entry : this.scores.entrySet()) {
            if (this.entityScoreIsInRange(lootContext, entity, scoreboard, entry.getKey(), entry.getValue())) continue;
            return false;
        }
        return true;
    }

    protected boolean entityScoreIsInRange(LootContext lootContext, Entity entity, Scoreboard scoreboard, String string, BoundedIntUnaryOperator boundedIntUnaryOperator) {
        ScoreboardObjective scoreboardObjective = scoreboard.getNullableObjective(string);
        if (scoreboardObjective == null) {
            return false;
        }
        String string2 = entity.getEntityName();
        if (!scoreboard.playerHasObjective(string2, scoreboardObjective)) {
            return false;
        }
        return boundedIntUnaryOperator.method_32393(lootContext, scoreboard.getPlayerScore(string2, scoreboardObjective).getScore());
    }

    @Override
    public /* synthetic */ boolean test(Object context) {
        return this.test((LootContext)context);
    }

    public static class Serializer
    implements JsonSerializer<EntityScoresLootCondition> {
        @Override
        public void toJson(JsonObject jsonObject, EntityScoresLootCondition entityScoresLootCondition, JsonSerializationContext jsonSerializationContext) {
            JsonObject jsonObject2 = new JsonObject();
            for (Map.Entry entry : entityScoresLootCondition.scores.entrySet()) {
                jsonObject2.add((String)entry.getKey(), jsonSerializationContext.serialize(entry.getValue()));
            }
            jsonObject.add("scores", jsonObject2);
            jsonObject.add("entity", jsonSerializationContext.serialize((Object)entityScoresLootCondition.target));
        }

        @Override
        public EntityScoresLootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            Set<Map.Entry<String, JsonElement>> set = JsonHelper.getObject(jsonObject, "scores").entrySet();
            LinkedHashMap<String, BoundedIntUnaryOperator> map = Maps.newLinkedHashMap();
            for (Map.Entry<String, JsonElement> entry : set) {
                map.put(entry.getKey(), JsonHelper.deserialize(entry.getValue(), "score", jsonDeserializationContext, BoundedIntUnaryOperator.class));
            }
            return new EntityScoresLootCondition(map, JsonHelper.deserialize(jsonObject, "entity", jsonDeserializationContext, LootContext.EntityTarget.class));
        }

        @Override
        public /* synthetic */ Object fromJson(JsonObject json, JsonDeserializationContext context) {
            return this.fromJson(json, context);
        }
    }
}


/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.condition;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.loot.condition.AlternativeLootCondition;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.condition.DamageSourcePropertiesLootCondition;
import net.minecraft.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.loot.condition.EntityScoresLootCondition;
import net.minecraft.loot.condition.InvertedLootCondition;
import net.minecraft.loot.condition.KilledByPlayerLootCondition;
import net.minecraft.loot.condition.LocationCheckLootCondition;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.MatchToolLootCondition;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.condition.RandomChanceWithLootingLootCondition;
import net.minecraft.loot.condition.ReferenceLootCondition;
import net.minecraft.loot.condition.SurvivesExplosionLootCondition;
import net.minecraft.loot.condition.TableBonusLootCondition;
import net.minecraft.loot.condition.TimeCheckLootCondition;
import net.minecraft.loot.condition.WeatherCheckLootCondition;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class LootConditions {
    private static final Map<Identifier, LootCondition.Factory<?>> byId = Maps.newHashMap();
    private static final Map<Class<? extends LootCondition>, LootCondition.Factory<?>> byClass = Maps.newHashMap();

    public static <T extends LootCondition> void register(LootCondition.Factory<? extends T> condition) {
        Identifier identifier = condition.getId();
        Class<T> class_ = condition.getConditionClass();
        if (byId.containsKey(identifier)) {
            throw new IllegalArgumentException("Can't re-register item condition name " + identifier);
        }
        if (byClass.containsKey(class_)) {
            throw new IllegalArgumentException("Can't re-register item condition class " + class_.getName());
        }
        byId.put(identifier, condition);
        byClass.put(class_, condition);
    }

    public static LootCondition.Factory<?> get(Identifier id) {
        LootCondition.Factory<?> factory = byId.get(id);
        if (factory == null) {
            throw new IllegalArgumentException("Unknown loot item condition '" + id + "'");
        }
        return factory;
    }

    public static <T extends LootCondition> LootCondition.Factory<T> getFactory(T condition) {
        LootCondition.Factory<?> factory = byClass.get(condition.getClass());
        if (factory == null) {
            throw new IllegalArgumentException("Unknown loot item condition " + condition);
        }
        return factory;
    }

    public static <T> Predicate<T> joinAnd(Predicate<T>[] predicates2) {
        switch (predicates2.length) {
            case 0: {
                return predicates -> true;
            }
            case 1: {
                return predicates2[0];
            }
            case 2: {
                return predicates2[0].and(predicates2[1]);
            }
        }
        return operand -> {
            for (Predicate predicate : predicates2) {
                if (predicate.test(operand)) continue;
                return false;
            }
            return true;
        };
    }

    public static <T> Predicate<T> joinOr(Predicate<T>[] predicates2) {
        switch (predicates2.length) {
            case 0: {
                return predicates -> false;
            }
            case 1: {
                return predicates2[0];
            }
            case 2: {
                return predicates2[0].or(predicates2[1]);
            }
        }
        return operand -> {
            for (Predicate predicate : predicates2) {
                if (!predicate.test(operand)) continue;
                return true;
            }
            return false;
        };
    }

    static {
        LootConditions.register(new InvertedLootCondition.Factory());
        LootConditions.register(new AlternativeLootCondition.Factory());
        LootConditions.register(new RandomChanceLootCondition.Factory());
        LootConditions.register(new RandomChanceWithLootingLootCondition.Factory());
        LootConditions.register(new EntityPropertiesLootCondition.Factory());
        LootConditions.register(new KilledByPlayerLootCondition.Factory());
        LootConditions.register(new EntityScoresLootCondition.Factory());
        LootConditions.register(new BlockStatePropertyLootCondition.Factory());
        LootConditions.register(new MatchToolLootCondition.Factory());
        LootConditions.register(new TableBonusLootCondition.Factory());
        LootConditions.register(new SurvivesExplosionLootCondition.Factory());
        LootConditions.register(new DamageSourcePropertiesLootCondition.Factory());
        LootConditions.register(new LocationCheckLootCondition.Factory());
        LootConditions.register(new WeatherCheckLootCondition.Factory());
        LootConditions.register(new ReferenceLootCondition.Factory());
        LootConditions.register(new TimeCheckLootCondition.Factory());
    }

    public static class Factory
    implements JsonDeserializer<LootCondition>,
    JsonSerializer<LootCondition> {
        @Override
        public LootCondition deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            LootCondition.Factory<?> factory;
            JsonObject jsonObject = JsonHelper.asObject(jsonElement, "condition");
            Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "condition"));
            try {
                factory = LootConditions.get(identifier);
            } catch (IllegalArgumentException illegalArgumentException) {
                throw new JsonSyntaxException("Unknown condition '" + identifier + "'");
            }
            return factory.fromJson(jsonObject, jsonDeserializationContext);
        }

        @Override
        public JsonElement serialize(LootCondition lootCondition, Type type, JsonSerializationContext jsonSerializationContext) {
            LootCondition.Factory<LootCondition> factory = LootConditions.getFactory(lootCondition);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("condition", factory.getId().toString());
            factory.toJson(jsonObject, lootCondition, jsonSerializationContext);
            return jsonObject;
        }

        @Override
        public /* synthetic */ JsonElement serialize(Object condition, Type unused, JsonSerializationContext context) {
            return this.serialize((LootCondition)condition, unused, context);
        }

        @Override
        public /* synthetic */ Object deserialize(JsonElement json, Type unused, JsonDeserializationContext context) throws JsonParseException {
            return this.deserialize(json, unused, context);
        }
    }
}


/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.loot.condition;

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
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.loot.condition.AlternativeLootCondition;
import net.minecraft.world.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.world.loot.condition.DamageSourcePropertiesLootCondition;
import net.minecraft.world.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.world.loot.condition.EntityScoresLootCondition;
import net.minecraft.world.loot.condition.InvertedLootCondition;
import net.minecraft.world.loot.condition.KilledByPlayerLootCondition;
import net.minecraft.world.loot.condition.LocationCheckLootCondition;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.condition.MatchToolLootCondition;
import net.minecraft.world.loot.condition.RandomChanceLootCondition;
import net.minecraft.world.loot.condition.RandomChanceWithLootingLootCondition;
import net.minecraft.world.loot.condition.SurvivesExplosionLootCondition;
import net.minecraft.world.loot.condition.TableBonusLootCondition;
import net.minecraft.world.loot.condition.WeatherCheckLootCondition;

public class LootConditions {
    private static final Map<Identifier, LootCondition.Factory<?>> byId = Maps.newHashMap();
    private static final Map<Class<? extends LootCondition>, LootCondition.Factory<?>> byClass = Maps.newHashMap();

    public static <T extends LootCondition> void register(LootCondition.Factory<? extends T> factory) {
        Identifier identifier = factory.getId();
        Class<T> class_ = factory.getConditionClass();
        if (byId.containsKey(identifier)) {
            throw new IllegalArgumentException("Can't re-register item condition name " + identifier);
        }
        if (byClass.containsKey(class_)) {
            throw new IllegalArgumentException("Can't re-register item condition class " + class_.getName());
        }
        byId.put(identifier, factory);
        byClass.put(class_, factory);
    }

    public static LootCondition.Factory<?> get(Identifier identifier) {
        LootCondition.Factory<?> factory = byId.get(identifier);
        if (factory == null) {
            throw new IllegalArgumentException("Unknown loot item condition '" + identifier + "'");
        }
        return factory;
    }

    public static <T extends LootCondition> LootCondition.Factory<T> getFactory(T lootCondition) {
        LootCondition.Factory<?> factory = byClass.get(lootCondition.getClass());
        if (factory == null) {
            throw new IllegalArgumentException("Unknown loot item condition " + lootCondition);
        }
        return factory;
    }

    public static <T> Predicate<T> joinAnd(Predicate<T>[] predicates) {
        switch (predicates.length) {
            case 0: {
                return object -> true;
            }
            case 1: {
                return predicates[0];
            }
            case 2: {
                return predicates[0].and(predicates[1]);
            }
        }
        return object -> {
            for (Predicate predicate : predicates) {
                if (predicate.test(object)) continue;
                return false;
            }
            return true;
        };
    }

    public static <T> Predicate<T> joinOr(Predicate<T>[] predicates) {
        switch (predicates.length) {
            case 0: {
                return object -> false;
            }
            case 1: {
                return predicates[0];
            }
            case 2: {
                return predicates[0].or(predicates[1]);
            }
        }
        return object -> {
            for (Predicate predicate : predicates) {
                if (!predicate.test(object)) continue;
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
    }

    public static class Factory
    implements JsonDeserializer<LootCondition>,
    JsonSerializer<LootCondition> {
        public LootCondition method_930(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
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

        public JsonElement method_931(LootCondition lootCondition, Type type, JsonSerializationContext jsonSerializationContext) {
            LootCondition.Factory<LootCondition> factory = LootConditions.getFactory(lootCondition);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("condition", factory.getId().toString());
            factory.toJson(jsonObject, lootCondition, jsonSerializationContext);
            return jsonObject;
        }

        @Override
        public /* synthetic */ JsonElement serialize(Object object, Type type, JsonSerializationContext jsonSerializationContext) {
            return this.method_931((LootCondition)object, type, jsonSerializationContext);
        }

        @Override
        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.method_930(jsonElement, type, jsonDeserializationContext);
        }
    }
}


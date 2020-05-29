/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.condition;

import java.util.function.Predicate;
import net.minecraft.class_5330;
import net.minecraft.loot.condition.AlternativeLootCondition;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.condition.DamageSourcePropertiesLootCondition;
import net.minecraft.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.loot.condition.EntityScoresLootCondition;
import net.minecraft.loot.condition.InvertedLootCondition;
import net.minecraft.loot.condition.KilledByPlayerLootCondition;
import net.minecraft.loot.condition.LocationCheckLootCondition;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.condition.MatchToolLootCondition;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.condition.RandomChanceWithLootingLootCondition;
import net.minecraft.loot.condition.ReferenceLootCondition;
import net.minecraft.loot.condition.SurvivesExplosionLootCondition;
import net.minecraft.loot.condition.TableBonusLootCondition;
import net.minecraft.loot.condition.TimeCheckLootCondition;
import net.minecraft.loot.condition.WeatherCheckLootCondition;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonSerializable;
import net.minecraft.util.registry.Registry;

public class LootConditionTypes {
    public static final LootConditionType INVERTED = LootConditionTypes.register("inverted", new InvertedLootCondition.Factory());
    public static final LootConditionType ALTERNATIVE = LootConditionTypes.register("alternative", new AlternativeLootCondition.Factory());
    public static final LootConditionType RANDOM_CHANCE = LootConditionTypes.register("random_chance", new RandomChanceLootCondition.Factory());
    public static final LootConditionType RANDOM_CHANCE_WITH_LOOTING = LootConditionTypes.register("random_chance_with_looting", new RandomChanceWithLootingLootCondition.Factory());
    public static final LootConditionType ENTITY_PROPERTIES = LootConditionTypes.register("entity_properties", new EntityPropertiesLootCondition.Factory());
    public static final LootConditionType KILLED_BY_PLAYER = LootConditionTypes.register("killed_by_player", new KilledByPlayerLootCondition.Factory());
    public static final LootConditionType ENTITY_SCORES = LootConditionTypes.register("entity_scores", new EntityScoresLootCondition.Factory());
    public static final LootConditionType BLOCK_STATE_PROPERTY = LootConditionTypes.register("block_state_property", new BlockStatePropertyLootCondition.Factory());
    public static final LootConditionType MATCH_TOOL = LootConditionTypes.register("match_tool", new MatchToolLootCondition.Factory());
    public static final LootConditionType TABLE_BONUS = LootConditionTypes.register("table_bonus", new TableBonusLootCondition.Factory());
    public static final LootConditionType SURVIVES_EXPLOSION = LootConditionTypes.register("survives_explosion", new SurvivesExplosionLootCondition.Factory());
    public static final LootConditionType DAMAGE_SOURCE_PROPERTIES = LootConditionTypes.register("damage_source_properties", new DamageSourcePropertiesLootCondition.Factory());
    public static final LootConditionType LOCATION_CHECK = LootConditionTypes.register("location_check", new LocationCheckLootCondition.Factory());
    public static final LootConditionType WEATHER_CHECK = LootConditionTypes.register("weather_check", new WeatherCheckLootCondition.Factory());
    public static final LootConditionType REFERENCE = LootConditionTypes.register("reference", new ReferenceLootCondition.Factory());
    public static final LootConditionType TIME_CHECK = LootConditionTypes.register("time_check", new TimeCheckLootCondition.Factory());

    private static LootConditionType register(String id, JsonSerializable<? extends LootCondition> jsonSerializable) {
        return Registry.register(Registry.LOOT_CONDITION_TYPE, new Identifier(id), new LootConditionType(jsonSerializable));
    }

    public static Object method_29326() {
        return class_5330.method_29306(Registry.LOOT_CONDITION_TYPE, "condition", "condition", LootCondition::method_29325).method_29307();
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
}


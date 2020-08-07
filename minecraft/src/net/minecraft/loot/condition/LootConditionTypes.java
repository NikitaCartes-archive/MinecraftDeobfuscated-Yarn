package net.minecraft.loot.condition;

import java.util.function.Predicate;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonSerializer;
import net.minecraft.util.JsonSerializing;
import net.minecraft.util.registry.Registry;

public class LootConditionTypes {
	public static final LootConditionType field_25235 = register("inverted", new InvertedLootCondition.Serializer());
	public static final LootConditionType field_25236 = register("alternative", new AlternativeLootCondition.Serializer());
	public static final LootConditionType field_25237 = register("random_chance", new RandomChanceLootCondition.Serializer());
	public static final LootConditionType field_25238 = register("random_chance_with_looting", new RandomChanceWithLootingLootCondition.Serializer());
	public static final LootConditionType field_25239 = register("entity_properties", new EntityPropertiesLootCondition.Serializer());
	public static final LootConditionType field_25240 = register("killed_by_player", new KilledByPlayerLootCondition.Serializer());
	public static final LootConditionType field_25241 = register("entity_scores", new EntityScoresLootCondition.Serializer());
	public static final LootConditionType field_25242 = register("block_state_property", new BlockStatePropertyLootCondition.Serializer());
	public static final LootConditionType field_25243 = register("match_tool", new MatchToolLootCondition.Serializer());
	public static final LootConditionType field_25244 = register("table_bonus", new TableBonusLootCondition.Serializer());
	public static final LootConditionType field_25245 = register("survives_explosion", new SurvivesExplosionLootCondition.Serializer());
	public static final LootConditionType field_25246 = register("damage_source_properties", new DamageSourcePropertiesLootCondition.Serializer());
	public static final LootConditionType field_25247 = register("location_check", new LocationCheckLootCondition.Serializer());
	public static final LootConditionType field_25248 = register("weather_check", new WeatherCheckLootCondition.Serializer());
	public static final LootConditionType field_25249 = register("reference", new ReferenceLootCondition.Serializer());
	public static final LootConditionType field_25250 = register("time_check", new TimeCheckLootCondition.Serializer());

	private static LootConditionType register(String id, JsonSerializer<? extends LootCondition> serializer) {
		return Registry.register(Registry.LOOT_CONDITION_TYPE, new Identifier(id), new LootConditionType(serializer));
	}

	public static Object createGsonSerializer() {
		return JsonSerializing.<LootCondition, LootConditionType>createTypeHandler(Registry.LOOT_CONDITION_TYPE, "condition", "condition", LootCondition::getType)
			.createGsonSerializer();
	}

	public static <T> Predicate<T> joinAnd(Predicate<T>[] predicates) {
		switch (predicates.length) {
			case 0:
				return predicatesx -> true;
			case 1:
				return predicates[0];
			case 2:
				return predicates[0].and(predicates[1]);
			default:
				return operand -> {
					for (Predicate<T> predicate : predicates) {
						if (!predicate.test(operand)) {
							return false;
						}
					}

					return true;
				};
		}
	}

	public static <T> Predicate<T> joinOr(Predicate<T>[] predicates) {
		switch (predicates.length) {
			case 0:
				return predicatesx -> false;
			case 1:
				return predicates[0];
			case 2:
				return predicates[0].or(predicates[1]);
			default:
				return operand -> {
					for (Predicate<T> predicate : predicates) {
						if (predicate.test(operand)) {
							return true;
						}
					}

					return false;
				};
		}
	}
}

package net.minecraft.loot.condition;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

public class LootConditionTypes {
	private static final Codec<LootCondition> BASE_CODEC = Registries.LOOT_CONDITION_TYPE
		.getCodec()
		.dispatch("condition", LootCondition::getType, LootConditionType::codec);
	public static final Codec<LootCondition> CODEC = Codecs.createLazy(() -> Codecs.either(BASE_CODEC, AllOfLootCondition.field_45858));
	public static final LootConditionType INVERTED = register("inverted", InvertedLootCondition.CODEC);
	public static final LootConditionType ANY_OF = register("any_of", AnyOfLootCondition.CODEC);
	public static final LootConditionType ALL_OF = register("all_of", AllOfLootCondition.CODEC);
	public static final LootConditionType RANDOM_CHANCE = register("random_chance", RandomChanceLootCondition.CODEC);
	public static final LootConditionType RANDOM_CHANCE_WITH_LOOTING = register("random_chance_with_looting", RandomChanceWithLootingLootCondition.CODEC);
	public static final LootConditionType ENTITY_PROPERTIES = register("entity_properties", EntityPropertiesLootCondition.CODEC);
	public static final LootConditionType KILLED_BY_PLAYER = register("killed_by_player", KilledByPlayerLootCondition.CODEC);
	public static final LootConditionType ENTITY_SCORES = register("entity_scores", EntityScoresLootCondition.CODEC);
	public static final LootConditionType BLOCK_STATE_PROPERTY = register("block_state_property", BlockStatePropertyLootCondition.CODEC);
	public static final LootConditionType MATCH_TOOL = register("match_tool", MatchToolLootCondition.CODEC);
	public static final LootConditionType TABLE_BONUS = register("table_bonus", TableBonusLootCondition.CODEC);
	public static final LootConditionType SURVIVES_EXPLOSION = register("survives_explosion", SurvivesExplosionLootCondition.CODEC);
	public static final LootConditionType DAMAGE_SOURCE_PROPERTIES = register("damage_source_properties", DamageSourcePropertiesLootCondition.CODEC);
	public static final LootConditionType LOCATION_CHECK = register("location_check", LocationCheckLootCondition.CODEC);
	public static final LootConditionType WEATHER_CHECK = register("weather_check", WeatherCheckLootCondition.CODEC);
	public static final LootConditionType REFERENCE = register("reference", ReferenceLootCondition.CODEC);
	public static final LootConditionType TIME_CHECK = register("time_check", TimeCheckLootCondition.CODEC);
	public static final LootConditionType VALUE_CHECK = register("value_check", ValueCheckLootCondition.CODEC);

	private static LootConditionType register(String id, Codec<? extends LootCondition> codec) {
		return Registry.register(Registries.LOOT_CONDITION_TYPE, new Identifier(id), new LootConditionType(codec));
	}

	/**
	 * Returns a predicate that returns true only if all its element predicates
	 * return true, as if applied by logical and.
	 */
	public static <T> Predicate<T> matchingAll(List<? extends Predicate<T>> predicates) {
		List<Predicate<T>> list = List.copyOf(predicates);

		return switch (list.size()) {
			case 0 -> predicatesx -> true;
			case 1 -> (Predicate)list.get(0);
			case 2 -> ((Predicate)list.get(0)).and((Predicate)list.get(1));
			default -> operand -> {
			for (Predicate<T> predicate : list) {
				if (!predicate.test(operand)) {
					return false;
				}
			}

			return true;
		};
		};
	}

	/**
	 * Returns a predicate that returns true if any its element predicates
	 * return true, as if applied by logical or.
	 */
	public static <T> Predicate<T> matchingAny(List<? extends Predicate<T>> list) {
		List<Predicate<T>> list2 = List.copyOf(list);

		return switch (list2.size()) {
			case 0 -> predicatesx -> false;
			case 1 -> (Predicate)list2.get(0);
			case 2 -> ((Predicate)list2.get(0)).or((Predicate)list2.get(1));
			default -> operand -> {
			for (Predicate<T> predicate : list2) {
				if (predicate.test(operand)) {
					return true;
				}
			}

			return false;
		};
		};
	}
}

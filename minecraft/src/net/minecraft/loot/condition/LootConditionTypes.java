package net.minecraft.loot.condition;

import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class LootConditionTypes {
	public static final LootConditionType INVERTED = register("inverted", InvertedLootCondition.CODEC);
	public static final LootConditionType ANY_OF = register("any_of", AnyOfLootCondition.CODEC);
	public static final LootConditionType ALL_OF = register("all_of", AllOfLootCondition.CODEC);
	public static final LootConditionType RANDOM_CHANCE = register("random_chance", RandomChanceLootCondition.CODEC);
	public static final LootConditionType RANDOM_CHANCE_WITH_ENCHANTED_BONUS = register(
		"random_chance_with_enchanted_bonus", RandomChanceWithEnchantedBonusLootCondition.CODEC
	);
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
	public static final LootConditionType ENCHANTMENT_ACTIVE_CHECK = register("enchantment_active_check", EnchantmentActiveCheckLootCondition.CODEC);

	private static LootConditionType register(String id, MapCodec<? extends LootCondition> codec) {
		return Registry.register(Registries.LOOT_CONDITION_TYPE, Identifier.ofVanilla(id), new LootConditionType(codec));
	}
}

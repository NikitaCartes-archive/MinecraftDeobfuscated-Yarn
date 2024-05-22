package net.minecraft.registry.tag;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public interface EnchantmentTags {
	TagKey<Enchantment> TOOLTIP_ORDER = of("tooltip_order");
	TagKey<Enchantment> ARMOR_EXCLUSIVE_SET = of("exclusive_set/armor");
	TagKey<Enchantment> BOOTS_EXCLUSIVE_SET = of("exclusive_set/boots");
	TagKey<Enchantment> BOW_EXCLUSIVE_SET = of("exclusive_set/bow");
	TagKey<Enchantment> CROSSBOW_EXCLUSIVE_SET = of("exclusive_set/crossbow");
	TagKey<Enchantment> DAMAGE_EXCLUSIVE_SET = of("exclusive_set/damage");
	TagKey<Enchantment> MINING_EXCLUSIVE_SET = of("exclusive_set/mining");
	TagKey<Enchantment> RIPTIDE_EXCLUSIVE_SET = of("exclusive_set/riptide");
	TagKey<Enchantment> TRADEABLE = of("tradeable");
	TagKey<Enchantment> DOUBLE_TRADE_PRICE = of("double_trade_price");
	TagKey<Enchantment> IN_ENCHANTING_TABLE = of("in_enchanting_table");
	TagKey<Enchantment> ON_MOB_SPAWN_EQUIPMENT = of("on_mob_spawn_equipment");
	TagKey<Enchantment> ON_TRADED_EQUIPMENT = of("on_traded_equipment");
	TagKey<Enchantment> ON_RANDOM_LOOT = of("on_random_loot");
	TagKey<Enchantment> CURSE = of("curse");
	TagKey<Enchantment> SMELTS_LOOT = of("smelts_loot");
	TagKey<Enchantment> PREVENTS_BEE_SPAWNS_WHEN_MINING = of("prevents_bee_spawns_when_mining");
	TagKey<Enchantment> PREVENTS_DECORATED_POT_SHATTERING = of("prevents_decorated_pot_shattering");
	TagKey<Enchantment> PREVENTS_ICE_MELTING = of("prevents_ice_melting");
	TagKey<Enchantment> PREVENTS_INFESTED_SPAWNS = of("prevents_infested_spawns");
	TagKey<Enchantment> TREASURE = of("treasure");
	TagKey<Enchantment> NON_TREASURE = of("non_treasure");
	TagKey<Enchantment> DESERT_COMMON_TRADE = of("trades/desert_common");
	TagKey<Enchantment> JUNGLE_COMMON_TRADE = of("trades/jungle_common");
	TagKey<Enchantment> PLAINS_COMMON_TRADE = of("trades/plains_common");
	TagKey<Enchantment> SAVANNA_COMMON_TRADE = of("trades/savanna_common");
	TagKey<Enchantment> SNOW_COMMON_TRADE = of("trades/snow_common");
	TagKey<Enchantment> SWAMP_COMMON_TRADE = of("trades/swamp_common");
	TagKey<Enchantment> TAIGA_COMMON_TRADE = of("trades/taiga_common");
	TagKey<Enchantment> DESERT_SPECIAL_TRADE = of("trades/desert_special");
	TagKey<Enchantment> JUNGLE_SPECIAL_TRADE = of("trades/jungle_special");
	TagKey<Enchantment> PLAINS_SPECIAL_TRADE = of("trades/plains_special");
	TagKey<Enchantment> SAVANNA_SPECIAL_TRADE = of("trades/savanna_special");
	TagKey<Enchantment> SNOW_SPECIAL_TRADE = of("trades/snow_special");
	TagKey<Enchantment> SWAMP_SPECIAL_TRADE = of("trades/swamp_special");
	TagKey<Enchantment> TAIGA_SPECIAL_TRADE = of("trades/taiga_special");

	private static TagKey<Enchantment> of(String id) {
		return TagKey.of(RegistryKeys.ENCHANTMENT, Identifier.ofVanilla(id));
	}
}

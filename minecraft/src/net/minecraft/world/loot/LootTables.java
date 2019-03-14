package net.minecraft.world.loot;

import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Set;
import net.minecraft.util.Identifier;

public class LootTables {
	private static final Set<Identifier> LOOT_TABLES = Sets.<Identifier>newHashSet();
	private static final Set<Identifier> LOOT_TABLES_READ_ONLY = Collections.unmodifiableSet(LOOT_TABLES);
	public static final Identifier EMPTY = new Identifier("empty");
	public static final Identifier CHEST_SPAWN_BONUS = registerLootTable("chests/spawn_bonus_chest");
	public static final Identifier CHEST_END_CITY_TREASURE = registerLootTable("chests/end_city_treasure");
	public static final Identifier CHEST_SIMPLE_DUNGEON = registerLootTable("chests/simple_dungeon");
	public static final Identifier CHEST_VILLAGE_BLACKSMITH = registerLootTable("chests/village/village_weaponsmith");
	public static final Identifier field_17107 = registerLootTable("chests/village/village_toolsmith");
	public static final Identifier field_17009 = registerLootTable("chests/village/village_armorer");
	public static final Identifier field_16751 = registerLootTable("chests/village/village_cartographer");
	public static final Identifier field_17010 = registerLootTable("chests/village/village_mason");
	public static final Identifier field_17011 = registerLootTable("chests/village/village_shepherd");
	public static final Identifier field_17012 = registerLootTable("chests/village/village_butcher");
	public static final Identifier field_17108 = registerLootTable("chests/village/village_fletcher");
	public static final Identifier field_18007 = registerLootTable("chests/village/village_fisher");
	public static final Identifier field_16750 = registerLootTable("chests/village/village_tannery");
	public static final Identifier field_17109 = registerLootTable("chests/village/village_temple");
	public static final Identifier field_16752 = registerLootTable("chests/village/village_desert_house");
	public static final Identifier field_16748 = registerLootTable("chests/village/village_plains_house");
	public static final Identifier field_16749 = registerLootTable("chests/village/village_taiga_house");
	public static final Identifier field_16754 = registerLootTable("chests/village/village_snowy_house");
	public static final Identifier field_16753 = registerLootTable("chests/village/village_savanna_house");
	public static final Identifier CHEST_ABANDONED_MINESHAFT = registerLootTable("chests/abandoned_mineshaft");
	public static final Identifier CHEST_NETHER_BRIDGE = registerLootTable("chests/nether_bridge");
	public static final Identifier CHEST_STRONGHOLD_LIBRARY = registerLootTable("chests/stronghold_library");
	public static final Identifier CHEST_STRONGHOLD_CROSSING = registerLootTable("chests/stronghold_crossing");
	public static final Identifier CHEST_STRONGHOLD_CORRIDOR = registerLootTable("chests/stronghold_corridor");
	public static final Identifier CHEST_DESERT_PYRAMID = registerLootTable("chests/desert_pyramid");
	public static final Identifier CHEST_JUNGLE_TEMPLE = registerLootTable("chests/jungle_temple");
	public static final Identifier DISPENSER_JUNGLE_TEMPLE = registerLootTable("chests/jungle_temple_dispenser");
	public static final Identifier CHEST_IGLOO = registerLootTable("chests/igloo_chest");
	public static final Identifier CHEST_WOODLAND_MANSION = registerLootTable("chests/woodland_mansion");
	public static final Identifier field_397 = registerLootTable("chests/underwater_ruin_small");
	public static final Identifier field_300 = registerLootTable("chests/underwater_ruin_big");
	public static final Identifier field_251 = registerLootTable("chests/buried_treasure");
	public static final Identifier field_841 = registerLootTable("chests/shipwreck_map");
	public static final Identifier field_880 = registerLootTable("chests/shipwreck_supply");
	public static final Identifier field_665 = registerLootTable("chests/shipwreck_treasure");
	public static final Identifier field_16593 = registerLootTable("chests/pillager_outpost");
	public static final Identifier ENTITY_SHEEP_WHITE = registerLootTable("entities/sheep/white");
	public static final Identifier ENTITY_SHEEP_ORANGE = registerLootTable("entities/sheep/orange");
	public static final Identifier ENTITY_SHEEP_MAGENTA = registerLootTable("entities/sheep/magenta");
	public static final Identifier ENTITY_SHEEP_LIGHT_BLUE = registerLootTable("entities/sheep/light_blue");
	public static final Identifier ENTITY_SHEEP_YELLOW = registerLootTable("entities/sheep/yellow");
	public static final Identifier ENTITY_SHEEP_LIME = registerLootTable("entities/sheep/lime");
	public static final Identifier ENTITY_SHEEP_PINK = registerLootTable("entities/sheep/pink");
	public static final Identifier ENTITY_SHEEP_GRAY = registerLootTable("entities/sheep/gray");
	public static final Identifier ENTITY_SHEEP_LIGHT_GRAY = registerLootTable("entities/sheep/light_gray");
	public static final Identifier ENTITY_SHEEP_CYAN = registerLootTable("entities/sheep/cyan");
	public static final Identifier ENTITY_SHEEP_PURPLE = registerLootTable("entities/sheep/purple");
	public static final Identifier ENTITY_SHEEP_BLUE = registerLootTable("entities/sheep/blue");
	public static final Identifier ENTITY_SHEEP_BROWN = registerLootTable("entities/sheep/brown");
	public static final Identifier ENTITY_SHEEP_GREEN = registerLootTable("entities/sheep/green");
	public static final Identifier ENTITY_SHEEP_RED = registerLootTable("entities/sheep/red");
	public static final Identifier ENTITY_SHEEP_BLACK = registerLootTable("entities/sheep/black");
	public static final Identifier GAMEPLAY_FISHING = registerLootTable("gameplay/fishing");
	public static final Identifier GAMEPLAY_FISHING_JUNK = registerLootTable("gameplay/fishing/junk");
	public static final Identifier GAMEPLAY_FISHING_TREASURE = registerLootTable("gameplay/fishing/treasure");
	public static final Identifier GAMEPLAY_FISHING_FISH = registerLootTable("gameplay/fishing/fish");
	public static final Identifier ENTITY_CAT_MORNING_GIFT = registerLootTable("gameplay/cat_morning_gift");

	private static Identifier registerLootTable(String string) {
		return registerLootTable(new Identifier(string));
	}

	private static Identifier registerLootTable(Identifier identifier) {
		if (LOOT_TABLES.add(identifier)) {
			return identifier;
		} else {
			throw new IllegalArgumentException(identifier + " is already a registered built-in loot table");
		}
	}

	public static Set<Identifier> getAll() {
		return LOOT_TABLES_READ_ONLY;
	}
}

package net.minecraft.loot;

import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Set;
import net.minecraft.util.Identifier;

public class LootTables {
	private static final Set<Identifier> LOOT_TABLES = Sets.<Identifier>newHashSet();
	private static final Set<Identifier> LOOT_TABLES_READ_ONLY = Collections.unmodifiableSet(LOOT_TABLES);
	public static final Identifier EMPTY = new Identifier("empty");
	public static final Identifier SPAWN_BONUS_CHEST_CHEST = register("chests/spawn_bonus_chest");
	public static final Identifier END_CITY_TREASURE_CHEST = register("chests/end_city_treasure");
	public static final Identifier SIMPLE_DUNGEON_CHEST = register("chests/simple_dungeon");
	public static final Identifier VILLAGE_VILLAGE_WEAPONSMITH_CHEST = register("chests/village/village_weaponsmith");
	public static final Identifier VILLAGE_VILLAGE_TOOLSMITH_CHEST = register("chests/village/village_toolsmith");
	public static final Identifier VILLAGE_VILLAGE_ARMORER_CHEST = register("chests/village/village_armorer");
	public static final Identifier VILLAGE_VILLAGE_CARTOGRAPHER_CHEST = register("chests/village/village_cartographer");
	public static final Identifier VILLAGE_VILLAGE_MASON_CHEST = register("chests/village/village_mason");
	public static final Identifier VILLAGE_VILLAGE_SHEPHERD_CHEST = register("chests/village/village_shepherd");
	public static final Identifier VILLAGE_VILLAGE_BUTCHER_CHEST = register("chests/village/village_butcher");
	public static final Identifier VILLAGE_VILLAGE_FLETCHER_CHEST = register("chests/village/village_fletcher");
	public static final Identifier VILLAGE_VILLAGE_FISHER_CHEST = register("chests/village/village_fisher");
	public static final Identifier VILLAGE_VILLAGE_TANNERY_CHEST = register("chests/village/village_tannery");
	public static final Identifier VILLAGE_VILLAGE_TEMPLE_CHEST = register("chests/village/village_temple");
	public static final Identifier VILLAGE_VILLAGE_DESERT_HOUSE_CHEST = register("chests/village/village_desert_house");
	public static final Identifier VILLAGE_VILLAGE_PLAINS_HOUSE_CHEST = register("chests/village/village_plains_house");
	public static final Identifier VILLAGE_VILLAGE_TAIGA_HOUSE_CHEST = register("chests/village/village_taiga_house");
	public static final Identifier VILLAGE_VILLAGE_SNOWY_HOUSE_CHEST = register("chests/village/village_snowy_house");
	public static final Identifier VILLAGE_VILLAGE_SAVANNA_HOUSE_CHEST = register("chests/village/village_savanna_house");
	public static final Identifier ABANDONED_MINESHAFT_CHEST = register("chests/abandoned_mineshaft");
	public static final Identifier NETHER_BRIDGE_CHEST = register("chests/nether_bridge");
	public static final Identifier STRONGHOLD_LIBRARY_CHEST = register("chests/stronghold_library");
	public static final Identifier STRONGHOLD_CROSSING_CHEST = register("chests/stronghold_crossing");
	public static final Identifier STRONGHOLD_CORRIDOR_CHEST = register("chests/stronghold_corridor");
	public static final Identifier DESERT_PYRAMID_CHEST = register("chests/desert_pyramid");
	public static final Identifier JUNGLE_TEMPLE_CHEST = register("chests/jungle_temple");
	public static final Identifier JUNGLE_TEMPLE_DISPENSER_CHEST = register("chests/jungle_temple_dispenser");
	public static final Identifier IGLOO_CHEST_CHEST = register("chests/igloo_chest");
	public static final Identifier WOODLAND_MANSION_CHEST = register("chests/woodland_mansion");
	public static final Identifier UNDERWATER_RUIN_SMALL_CHEST = register("chests/underwater_ruin_small");
	public static final Identifier UNDERWATER_RUIN_BIG_CHEST = register("chests/underwater_ruin_big");
	public static final Identifier BURIED_TREASURE_CHEST = register("chests/buried_treasure");
	public static final Identifier SHIPWRECK_MAP_CHEST = register("chests/shipwreck_map");
	public static final Identifier SHIPWRECK_SUPPLY_CHEST = register("chests/shipwreck_supply");
	public static final Identifier SHIPWRECK_TREASURE_CHEST = register("chests/shipwreck_treasure");
	public static final Identifier PILLAGER_OUTPOST_CHEST = register("chests/pillager_outpost");
	public static final Identifier SHEEP_WHITE_ENTITIE = register("entities/sheep/white");
	public static final Identifier SHEEP_ORANGE_ENTITIE = register("entities/sheep/orange");
	public static final Identifier SHEEP_MAGENTA_ENTITIE = register("entities/sheep/magenta");
	public static final Identifier SHEEP_LIGHT_BLUE_ENTITIE = register("entities/sheep/light_blue");
	public static final Identifier SHEEP_YELLOW_ENTITIE = register("entities/sheep/yellow");
	public static final Identifier SHEEP_LIME_ENTITIE = register("entities/sheep/lime");
	public static final Identifier SHEEP_PINK_ENTITIE = register("entities/sheep/pink");
	public static final Identifier SHEEP_GRAY_ENTITIE = register("entities/sheep/gray");
	public static final Identifier SHEEP_LIGHT_GRAY_ENTITIE = register("entities/sheep/light_gray");
	public static final Identifier SHEEP_CYAN_ENTITIE = register("entities/sheep/cyan");
	public static final Identifier SHEEP_PURPLE_ENTITIE = register("entities/sheep/purple");
	public static final Identifier SHEEP_BLUE_ENTITIE = register("entities/sheep/blue");
	public static final Identifier SHEEP_BROWN_ENTITIE = register("entities/sheep/brown");
	public static final Identifier SHEEP_GREEN_ENTITIE = register("entities/sheep/green");
	public static final Identifier SHEEP_RED_ENTITIE = register("entities/sheep/red");
	public static final Identifier SHEEP_BLACK_ENTITIE = register("entities/sheep/black");
	public static final Identifier FISHING_GAMEPLAY = register("gameplay/fishing");
	public static final Identifier FISHING_JUNK_GAMEPLAY = register("gameplay/fishing/junk");
	public static final Identifier FISHING_TREASURE_GAMEPLAY = register("gameplay/fishing/treasure");
	public static final Identifier FISHING_FISH_GAMEPLAY = register("gameplay/fishing/fish");
	public static final Identifier CAT_MORNING_GIFT_GAMEPLAY = register("gameplay/cat_morning_gift");
	public static final Identifier HERO_OF_THE_VILLAGE_ARMORER_GIFT_GAMEPLAY = register("gameplay/hero_of_the_village/armorer_gift");
	public static final Identifier HERO_OF_THE_VILLAGE_BUTCHER_GIFT_GAMEPLAY = register("gameplay/hero_of_the_village/butcher_gift");
	public static final Identifier HERO_OF_THE_VILLAGE_CARTOGRAPHER_GIFT_GAMEPLAY = register("gameplay/hero_of_the_village/cartographer_gift");
	public static final Identifier HERO_OF_THE_VILLAGE_CLERIC_GIFT_GAMEPLAY = register("gameplay/hero_of_the_village/cleric_gift");
	public static final Identifier HERO_OF_THE_VILLAGE_FARMER_GIFT_GAMEPLAY = register("gameplay/hero_of_the_village/farmer_gift");
	public static final Identifier HERO_OF_THE_VILLAGE_FISHERMAN_GIFT_GAMEPLAY = register("gameplay/hero_of_the_village/fisherman_gift");
	public static final Identifier HERO_OF_THE_VILLAGE_FLETCHER_GIFT_GAMEPLAY = register("gameplay/hero_of_the_village/fletcher_gift");
	public static final Identifier HERO_OF_THE_VILLAGE_LEATHERWORKER_GIFT_GAMEPLAY = register("gameplay/hero_of_the_village/leatherworker_gift");
	public static final Identifier HERO_OF_THE_VILLAGE_LIBRARIAN_GIFT_GAMEPLAY = register("gameplay/hero_of_the_village/librarian_gift");
	public static final Identifier HERO_OF_THE_VILLAGE_MASON_GIFT_GAMEPLAY = register("gameplay/hero_of_the_village/mason_gift");
	public static final Identifier HERO_OF_THE_VILLAGE_SHEPHERD_GIFT_GAMEPLAY = register("gameplay/hero_of_the_village/shepherd_gift");
	public static final Identifier HERO_OF_THE_VILLAGE_TOOLSMITH_GIFT_GAMEPLAY = register("gameplay/hero_of_the_village/toolsmith_gift");
	public static final Identifier HERO_OF_THE_VILLAGE_WEAPONSMITH_GIFT_GAMEPLAY = register("gameplay/hero_of_the_village/weaponsmith_gift");

	private static Identifier register(String string) {
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

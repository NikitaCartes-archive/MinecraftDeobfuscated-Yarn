package net.minecraft.world.loot;

import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Set;
import net.minecraft.util.Identifier;

public class LootTables {
	private static final Set<Identifier> LOOT_TABLES = Sets.<Identifier>newHashSet();
	private static final Set<Identifier> LOOT_TABLES_READ_ONLY = Collections.unmodifiableSet(LOOT_TABLES);
	public static final Identifier EMPTY = new Identifier("empty");
	public static final Identifier SPAWN_BONUS_CHEST = register("chests/spawn_bonus_chest");
	public static final Identifier field_274 = register("chests/end_city_treasure");
	public static final Identifier field_356 = register("chests/simple_dungeon");
	public static final Identifier VILLAGE_WEAPONSMITH_CHEST = register("chests/village/village_weaponsmith");
	public static final Identifier VILLAGE_TOOLSMITH_CHEST = register("chests/village/village_toolsmith");
	public static final Identifier VILLAGE_ARMORER_CHEST = register("chests/village/village_armorer");
	public static final Identifier VILLAGE_CARTOGRAPHER_CHEST = register("chests/village/village_cartographer");
	public static final Identifier VILLAGE_MASON_CHEST = register("chests/village/village_mason");
	public static final Identifier VILLAGE_SHEPARD_CHEST = register("chests/village/village_shepherd");
	public static final Identifier VILLAGE_BUTCHER_CHEST = register("chests/village/village_butcher");
	public static final Identifier VILLAGE_FLETCHER_CHEST = register("chests/village/village_fletcher");
	public static final Identifier VILLAGE_FISHER_CHEST = register("chests/village/village_fisher");
	public static final Identifier VILLAGE_TANNERY_CHEST = register("chests/village/village_tannery");
	public static final Identifier VILLAGE_TEMPLE_CHEST = register("chests/village/village_temple");
	public static final Identifier VILLAGE_DESERT_HOUSE_CHEST = register("chests/village/village_desert_house");
	public static final Identifier VILLAGE_PLAINS_CHEST = register("chests/village/village_plains_house");
	public static final Identifier VILLAGE_TAIGA_HOUSE_CHEST = register("chests/village/village_taiga_house");
	public static final Identifier VILLAGE_SNOWY_HOUSE_CHEST = register("chests/village/village_snowy_house");
	public static final Identifier VILLAGE_SAVANNA_HOUSE_CHEST = register("chests/village/village_savanna_house");
	public static final Identifier field_472 = register("chests/abandoned_mineshaft");
	public static final Identifier field_615 = register("chests/nether_bridge");
	public static final Identifier field_683 = register("chests/stronghold_library");
	public static final Identifier field_800 = register("chests/stronghold_crossing");
	public static final Identifier field_842 = register("chests/stronghold_corridor");
	public static final Identifier field_885 = register("chests/desert_pyramid");
	public static final Identifier field_803 = register("chests/jungle_temple");
	public static final Identifier field_751 = register("chests/jungle_temple_dispenser");
	public static final Identifier field_662 = register("chests/igloo_chest");
	public static final Identifier field_484 = register("chests/woodland_mansion");
	public static final Identifier field_397 = register("chests/underwater_ruin_small");
	public static final Identifier field_300 = register("chests/underwater_ruin_big");
	public static final Identifier field_251 = register("chests/buried_treasure");
	public static final Identifier field_841 = register("chests/shipwreck_map");
	public static final Identifier field_880 = register("chests/shipwreck_supply");
	public static final Identifier field_665 = register("chests/shipwreck_treasure");
	public static final Identifier field_16593 = register("chests/pillager_outpost");
	public static final Identifier WHITE_SHEEP_ENTITY = register("entities/sheep/white");
	public static final Identifier ORANGE_SHEEP_ENTITY = register("entities/sheep/orange");
	public static final Identifier MAGENTA_SHEEP_ENTITY = register("entities/sheep/magenta");
	public static final Identifier LIGHT_BLUE_SHEEP_ENTITY = register("entities/sheep/light_blue");
	public static final Identifier YELLOW_SHEEP_ENTITY = register("entities/sheep/yellow");
	public static final Identifier LIME_SHEEP_ENTITY = register("entities/sheep/lime");
	public static final Identifier PINK_SHEEP_ENTITY = register("entities/sheep/pink");
	public static final Identifier GRAY_SHEEP_ENTITY = register("entities/sheep/gray");
	public static final Identifier LIGHT_GRAY_SHEEP_ENTITY = register("entities/sheep/light_gray");
	public static final Identifier CYAN_SHEEP_ENTITY = register("entities/sheep/cyan");
	public static final Identifier PURPLE_SHEEP_ENTITY = register("entities/sheep/purple");
	public static final Identifier BLUE_SHEEP_ENTITY = register("entities/sheep/blue");
	public static final Identifier BROWN_SHEEP_ENTITY = register("entities/sheep/brown");
	public static final Identifier GREEN_SHEEP_ENTITY = register("entities/sheep/green");
	public static final Identifier RED_SHEEP_ENTITY = register("entities/sheep/red");
	public static final Identifier BLACK_SHEEP_ENTITY = register("entities/sheep/black");
	public static final Identifier field_353 = register("gameplay/fishing");
	public static final Identifier field_266 = register("gameplay/fishing/junk");
	public static final Identifier field_854 = register("gameplay/fishing/treasure");
	public static final Identifier field_795 = register("gameplay/fishing/fish");
	public static final Identifier field_16216 = register("gameplay/cat_morning_gift");
	public static final Identifier field_19062 = register("gameplay/hero_of_the_village/armorer_gift");
	public static final Identifier field_19063 = register("gameplay/hero_of_the_village/butcher_gift");
	public static final Identifier field_19064 = register("gameplay/hero_of_the_village/cartographer_gift");
	public static final Identifier field_19065 = register("gameplay/hero_of_the_village/cleric_gift");
	public static final Identifier field_19066 = register("gameplay/hero_of_the_village/farmer_gift");
	public static final Identifier field_19067 = register("gameplay/hero_of_the_village/fisherman_gift");
	public static final Identifier field_19068 = register("gameplay/hero_of_the_village/fletcher_gift");
	public static final Identifier field_19069 = register("gameplay/hero_of_the_village/leatherworker_gift");
	public static final Identifier field_19070 = register("gameplay/hero_of_the_village/librarian_gift");
	public static final Identifier field_19071 = register("gameplay/hero_of_the_village/mason_gift");
	public static final Identifier field_19072 = register("gameplay/hero_of_the_village/shepherd_gift");
	public static final Identifier field_19073 = register("gameplay/hero_of_the_village/toolsmith_gift");
	public static final Identifier field_19074 = register("gameplay/hero_of_the_village/weaponsmith_gift");

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

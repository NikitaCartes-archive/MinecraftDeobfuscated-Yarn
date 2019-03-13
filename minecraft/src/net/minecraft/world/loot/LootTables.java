package net.minecraft.world.loot;

import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Set;
import net.minecraft.util.Identifier;

public class LootTables {
	private static final Set<Identifier> LOOT_TABLES = Sets.<Identifier>newHashSet();
	private static final Set<Identifier> LOOT_TABLES_READ_ONLY = Collections.unmodifiableSet(LOOT_TABLES);
	public static final Identifier field_844 = new Identifier("empty");
	public static final Identifier field_850 = method_269("chests/spawn_bonus_chest");
	public static final Identifier field_274 = method_269("chests/end_city_treasure");
	public static final Identifier field_356 = method_269("chests/simple_dungeon");
	public static final Identifier field_434 = method_269("chests/village/village_weaponsmith");
	public static final Identifier field_17107 = method_269("chests/village/village_toolsmith");
	public static final Identifier field_17009 = method_269("chests/village/village_armorer");
	public static final Identifier field_16751 = method_269("chests/village/village_cartographer");
	public static final Identifier field_17010 = method_269("chests/village/village_mason");
	public static final Identifier field_17011 = method_269("chests/village/village_shepherd");
	public static final Identifier field_17012 = method_269("chests/village/village_butcher");
	public static final Identifier field_17108 = method_269("chests/village/village_fletcher");
	public static final Identifier field_18007 = method_269("chests/village/village_fisher");
	public static final Identifier field_16750 = method_269("chests/village/village_tannery");
	public static final Identifier field_17109 = method_269("chests/village/village_temple");
	public static final Identifier field_16752 = method_269("chests/village/village_desert_house");
	public static final Identifier field_16748 = method_269("chests/village/village_plains_house");
	public static final Identifier field_16749 = method_269("chests/village/village_taiga_house");
	public static final Identifier field_16754 = method_269("chests/village/village_snowy_house");
	public static final Identifier field_16753 = method_269("chests/village/village_savanna_house");
	public static final Identifier field_472 = method_269("chests/abandoned_mineshaft");
	public static final Identifier field_615 = method_269("chests/nether_bridge");
	public static final Identifier field_683 = method_269("chests/stronghold_library");
	public static final Identifier field_800 = method_269("chests/stronghold_crossing");
	public static final Identifier field_842 = method_269("chests/stronghold_corridor");
	public static final Identifier field_885 = method_269("chests/desert_pyramid");
	public static final Identifier field_803 = method_269("chests/jungle_temple");
	public static final Identifier field_751 = method_269("chests/jungle_temple_dispenser");
	public static final Identifier field_662 = method_269("chests/igloo_chest");
	public static final Identifier field_484 = method_269("chests/woodland_mansion");
	public static final Identifier field_397 = method_269("chests/underwater_ruin_small");
	public static final Identifier field_300 = method_269("chests/underwater_ruin_big");
	public static final Identifier field_251 = method_269("chests/buried_treasure");
	public static final Identifier field_841 = method_269("chests/shipwreck_map");
	public static final Identifier field_880 = method_269("chests/shipwreck_supply");
	public static final Identifier field_665 = method_269("chests/shipwreck_treasure");
	public static final Identifier field_16593 = method_269("chests/pillager_outpost");
	public static final Identifier field_869 = method_269("entities/sheep/white");
	public static final Identifier field_814 = method_269("entities/sheep/orange");
	public static final Identifier field_224 = method_269("entities/sheep/magenta");
	public static final Identifier field_461 = method_269("entities/sheep/light_blue");
	public static final Identifier field_385 = method_269("entities/sheep/yellow");
	public static final Identifier field_702 = method_269("entities/sheep/lime");
	public static final Identifier field_629 = method_269("entities/sheep/pink");
	public static final Identifier field_878 = method_269("entities/sheep/gray");
	public static final Identifier field_806 = method_269("entities/sheep/light_gray");
	public static final Identifier field_365 = method_269("entities/sheep/cyan");
	public static final Identifier field_285 = method_269("entities/sheep/purple");
	public static final Identifier field_394 = method_269("entities/sheep/blue");
	public static final Identifier field_489 = method_269("entities/sheep/brown");
	public static final Identifier field_607 = method_269("entities/sheep/green");
	public static final Identifier field_716 = method_269("entities/sheep/red");
	public static final Identifier field_778 = method_269("entities/sheep/black");
	public static final Identifier field_353 = method_269("gameplay/fishing");
	public static final Identifier field_266 = method_269("gameplay/fishing/junk");
	public static final Identifier field_854 = method_269("gameplay/fishing/treasure");
	public static final Identifier field_795 = method_269("gameplay/fishing/fish");
	public static final Identifier field_16216 = method_269("gameplay/cat_morning_gift");

	private static Identifier method_269(String string) {
		return method_271(new Identifier(string));
	}

	private static Identifier method_271(Identifier identifier) {
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

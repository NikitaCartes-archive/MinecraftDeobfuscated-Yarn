/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot;

import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Set;
import net.minecraft.util.Identifier;

public class LootTables {
    private static final Set<Identifier> LOOT_TABLES = Sets.newHashSet();
    private static final Set<Identifier> LOOT_TABLES_READ_ONLY = Collections.unmodifiableSet(LOOT_TABLES);
    public static final Identifier EMPTY = new Identifier("empty");
    public static final Identifier SPAWN_BONUS_CHEST = LootTables.register("chests/spawn_bonus_chest");
    public static final Identifier END_CITY_TREASURE_CHEST = LootTables.register("chests/end_city_treasure");
    public static final Identifier SIMPLE_DUNGEON_CHEST = LootTables.register("chests/simple_dungeon");
    public static final Identifier VILLAGE_WEAPONSMITH_CHEST = LootTables.register("chests/village/village_weaponsmith");
    public static final Identifier VILLAGE_TOOLSMITH_CHEST = LootTables.register("chests/village/village_toolsmith");
    public static final Identifier VILLAGE_ARMORER_CHEST = LootTables.register("chests/village/village_armorer");
    public static final Identifier VILLAGE_CARTOGRAPHER_CHEST = LootTables.register("chests/village/village_cartographer");
    public static final Identifier VILLAGE_MASON_CHEST = LootTables.register("chests/village/village_mason");
    public static final Identifier VILLAGE_SHEPARD_CHEST = LootTables.register("chests/village/village_shepherd");
    public static final Identifier VILLAGE_BUTCHER_CHEST = LootTables.register("chests/village/village_butcher");
    public static final Identifier VILLAGE_FLETCHER_CHEST = LootTables.register("chests/village/village_fletcher");
    public static final Identifier VILLAGE_FISHER_CHEST = LootTables.register("chests/village/village_fisher");
    public static final Identifier VILLAGE_TANNERY_CHEST = LootTables.register("chests/village/village_tannery");
    public static final Identifier VILLAGE_TEMPLE_CHEST = LootTables.register("chests/village/village_temple");
    public static final Identifier VILLAGE_DESERT_HOUSE_CHEST = LootTables.register("chests/village/village_desert_house");
    public static final Identifier VILLAGE_PLAINS_CHEST = LootTables.register("chests/village/village_plains_house");
    public static final Identifier VILLAGE_TAIGA_HOUSE_CHEST = LootTables.register("chests/village/village_taiga_house");
    public static final Identifier VILLAGE_SNOWY_HOUSE_CHEST = LootTables.register("chests/village/village_snowy_house");
    public static final Identifier VILLAGE_SAVANNA_HOUSE_CHEST = LootTables.register("chests/village/village_savanna_house");
    public static final Identifier ABANDONED_MINESHAFT_CHEST = LootTables.register("chests/abandoned_mineshaft");
    public static final Identifier NETHER_BRIDGE_CHEST = LootTables.register("chests/nether_bridge");
    public static final Identifier STRONGHOLD_LIBRARY_CHEST = LootTables.register("chests/stronghold_library");
    public static final Identifier STRONGHOLD_CROSSING_CHEST = LootTables.register("chests/stronghold_crossing");
    public static final Identifier STRONGHOLD_CORRIDOR_CHEST = LootTables.register("chests/stronghold_corridor");
    public static final Identifier DESERT_PYRAMID_CHEST = LootTables.register("chests/desert_pyramid");
    public static final Identifier JUNGLE_TEMPLE_CHEST = LootTables.register("chests/jungle_temple");
    public static final Identifier JUNGLE_TEMPLE_DISPENSER_CHEST = LootTables.register("chests/jungle_temple_dispenser");
    public static final Identifier IGLOO_CHEST_CHEST = LootTables.register("chests/igloo_chest");
    public static final Identifier WOODLAND_MANSION_CHEST = LootTables.register("chests/woodland_mansion");
    public static final Identifier UNDERWATER_RUIN_SMALL_CHEST = LootTables.register("chests/underwater_ruin_small");
    public static final Identifier UNDERWATER_RUIN_BIG_CHEST = LootTables.register("chests/underwater_ruin_big");
    public static final Identifier BURIED_TREASURE_CHEST = LootTables.register("chests/buried_treasure");
    public static final Identifier SHIPWRECK_MAP_CHEST = LootTables.register("chests/shipwreck_map");
    public static final Identifier SHIPWRECK_SUPPLY_CHEST = LootTables.register("chests/shipwreck_supply");
    public static final Identifier SHIPWRECK_TREASURE_CHEST = LootTables.register("chests/shipwreck_treasure");
    public static final Identifier PILLAGER_OUTPOST_CHEST = LootTables.register("chests/pillager_outpost");
    public static final Identifier BASTION_TREASURE_CHEST = LootTables.register("chests/bastion_treasure");
    public static final Identifier BASTION_OTHER_CHEST = LootTables.register("chests/bastion_other");
    public static final Identifier BASTION_BRIDGE_CHEST = LootTables.register("chests/bastion_bridge");
    public static final Identifier BASTION_HOGLIN_STABLE_CHEST = LootTables.register("chests/bastion_hoglin_stable");
    public static final Identifier ANCIENT_CITY_CHEST = LootTables.register("chests/ancient_city");
    public static final Identifier ANCIENT_CITY_ICE_BOX_CHEST = LootTables.register("chests/ancient_city_ice_box");
    public static final Identifier RUINED_PORTAL_CHEST = LootTables.register("chests/ruined_portal");
    public static final Identifier WHITE_SHEEP_ENTITY = LootTables.register("entities/sheep/white");
    public static final Identifier ORANGE_SHEEP_ENTITY = LootTables.register("entities/sheep/orange");
    public static final Identifier MAGENTA_SHEEP_ENTITY = LootTables.register("entities/sheep/magenta");
    public static final Identifier LIGHT_BLUE_SHEEP_ENTITY = LootTables.register("entities/sheep/light_blue");
    public static final Identifier YELLOW_SHEEP_ENTITY = LootTables.register("entities/sheep/yellow");
    public static final Identifier LIME_SHEEP_ENTITY = LootTables.register("entities/sheep/lime");
    public static final Identifier PINK_SHEEP_ENTITY = LootTables.register("entities/sheep/pink");
    public static final Identifier GRAY_SHEEP_ENTITY = LootTables.register("entities/sheep/gray");
    public static final Identifier LIGHT_GRAY_SHEEP_ENTITY = LootTables.register("entities/sheep/light_gray");
    public static final Identifier CYAN_SHEEP_ENTITY = LootTables.register("entities/sheep/cyan");
    public static final Identifier PURPLE_SHEEP_ENTITY = LootTables.register("entities/sheep/purple");
    public static final Identifier BLUE_SHEEP_ENTITY = LootTables.register("entities/sheep/blue");
    public static final Identifier BROWN_SHEEP_ENTITY = LootTables.register("entities/sheep/brown");
    public static final Identifier GREEN_SHEEP_ENTITY = LootTables.register("entities/sheep/green");
    public static final Identifier RED_SHEEP_ENTITY = LootTables.register("entities/sheep/red");
    public static final Identifier BLACK_SHEEP_ENTITY = LootTables.register("entities/sheep/black");
    public static final Identifier FISHING_GAMEPLAY = LootTables.register("gameplay/fishing");
    public static final Identifier FISHING_JUNK_GAMEPLAY = LootTables.register("gameplay/fishing/junk");
    public static final Identifier FISHING_TREASURE_GAMEPLAY = LootTables.register("gameplay/fishing/treasure");
    public static final Identifier FISHING_FISH_GAMEPLAY = LootTables.register("gameplay/fishing/fish");
    public static final Identifier CAT_MORNING_GIFT_GAMEPLAY = LootTables.register("gameplay/cat_morning_gift");
    public static final Identifier HERO_OF_THE_VILLAGE_ARMORER_GIFT_GAMEPLAY = LootTables.register("gameplay/hero_of_the_village/armorer_gift");
    public static final Identifier HERO_OF_THE_VILLAGE_BUTCHER_GIFT_GAMEPLAY = LootTables.register("gameplay/hero_of_the_village/butcher_gift");
    public static final Identifier HERO_OF_THE_VILLAGE_CARTOGRAPHER_GIFT_GAMEPLAY = LootTables.register("gameplay/hero_of_the_village/cartographer_gift");
    public static final Identifier HERO_OF_THE_VILLAGE_CLERIC_GIFT_GAMEPLAY = LootTables.register("gameplay/hero_of_the_village/cleric_gift");
    public static final Identifier HERO_OF_THE_VILLAGE_FARMER_GIFT_GAMEPLAY = LootTables.register("gameplay/hero_of_the_village/farmer_gift");
    public static final Identifier HERO_OF_THE_VILLAGE_FISHERMAN_GIFT_GAMEPLAY = LootTables.register("gameplay/hero_of_the_village/fisherman_gift");
    public static final Identifier HERO_OF_THE_VILLAGE_FLETCHER_GIFT_GAMEPLAY = LootTables.register("gameplay/hero_of_the_village/fletcher_gift");
    public static final Identifier HERO_OF_THE_VILLAGE_LEATHERWORKER_GIFT_GAMEPLAY = LootTables.register("gameplay/hero_of_the_village/leatherworker_gift");
    public static final Identifier HERO_OF_THE_VILLAGE_LIBRARIAN_GIFT_GAMEPLAY = LootTables.register("gameplay/hero_of_the_village/librarian_gift");
    public static final Identifier HERO_OF_THE_VILLAGE_MASON_GIFT_GAMEPLAY = LootTables.register("gameplay/hero_of_the_village/mason_gift");
    public static final Identifier HERO_OF_THE_VILLAGE_SHEPHERD_GIFT_GAMEPLAY = LootTables.register("gameplay/hero_of_the_village/shepherd_gift");
    public static final Identifier HERO_OF_THE_VILLAGE_TOOLSMITH_GIFT_GAMEPLAY = LootTables.register("gameplay/hero_of_the_village/toolsmith_gift");
    public static final Identifier HERO_OF_THE_VILLAGE_WEAPONSMITH_GIFT_GAMEPLAY = LootTables.register("gameplay/hero_of_the_village/weaponsmith_gift");
    public static final Identifier PIGLIN_BARTERING_GAMEPLAY = LootTables.register("gameplay/piglin_bartering");

    private static Identifier register(String id) {
        return LootTables.registerLootTable(new Identifier(id));
    }

    private static Identifier registerLootTable(Identifier id) {
        if (LOOT_TABLES.add(id)) {
            return id;
        }
        throw new IllegalArgumentException(id + " is already a registered built-in loot table");
    }

    public static Set<Identifier> getAll() {
        return LOOT_TABLES_READ_ONLY;
    }
}


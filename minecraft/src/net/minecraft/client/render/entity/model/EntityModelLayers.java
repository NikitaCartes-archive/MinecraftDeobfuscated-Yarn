package net.minecraft.client.render.entity.model;

import com.google.common.collect.Sets;
import java.util.Set;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.WoodType;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class EntityModelLayers {
	private static final String MAIN = "main";
	private static final Set<EntityModelLayer> LAYERS = Sets.<EntityModelLayer>newHashSet();
	public static final EntityModelLayer ACACIA_BOAT = registerMain("boat/acacia");
	public static final EntityModelLayer ACACIA_CHEST_BOAT = registerMain("chest_boat/acacia");
	public static final EntityModelLayer ALLAY = registerMain("allay");
	public static final EntityModelLayer ARMADILLO = registerMain("armadillo");
	public static final EntityModelLayer ARMADILLO_BABY = registerMain("armadillo_baby");
	public static final EntityModelLayer ARMOR_STAND = registerMain("armor_stand");
	public static final EntityModelLayer ARMOR_STAND_INNER_ARMOR = createInnerArmor("armor_stand");
	public static final EntityModelLayer ARMOR_STAND_OUTER_ARMOR = createOuterArmor("armor_stand");
	public static final EntityModelLayer ARMOR_STAND_SMALL = registerMain("armor_stand_small");
	public static final EntityModelLayer ARMOR_STAND_SMALL_INNER_ARMOR = createInnerArmor("armor_stand_small");
	public static final EntityModelLayer ARMOR_STAND_SMALL_OUTER_ARMOR = createOuterArmor("armor_stand_small");
	public static final EntityModelLayer ARROW = registerMain("arrow");
	public static final EntityModelLayer AXOLOTL = registerMain("axolotl");
	public static final EntityModelLayer AXOLOTL_BABY = registerMain("axolotl_baby");
	public static final EntityModelLayer BAMBOO_CHEST_BOAT = registerMain("chest_boat/bamboo");
	public static final EntityModelLayer BAMBOO_BOAT = registerMain("boat/bamboo");
	public static final EntityModelLayer BANNER = registerMain("banner");
	public static final EntityModelLayer BAT = registerMain("bat");
	public static final EntityModelLayer BED_FOOT = registerMain("bed_foot");
	public static final EntityModelLayer BED_HEAD = registerMain("bed_head");
	public static final EntityModelLayer BEE = registerMain("bee");
	public static final EntityModelLayer BEE_BABY = registerMain("bee_baby");
	public static final EntityModelLayer BEE_STINGER = registerMain("bee_stinger");
	public static final EntityModelLayer BELL = registerMain("bell");
	public static final EntityModelLayer BIRCH_BOAT = registerMain("boat/birch");
	public static final EntityModelLayer BIRCH_CHEST_BOAT = registerMain("chest_boat/birch");
	public static final EntityModelLayer BLAZE = registerMain("blaze");
	public static final EntityModelLayer BOAT = register("boat", "water_patch");
	public static final EntityModelLayer BOGGED = registerMain("bogged");
	public static final EntityModelLayer BOGGED_INNER_ARMOR = createInnerArmor("bogged");
	public static final EntityModelLayer BOGGED_OUTER_ARMOR = createOuterArmor("bogged");
	public static final EntityModelLayer BOGGED_OUTER = register("bogged", "outer");
	public static final EntityModelLayer BOOK = registerMain("book");
	public static final EntityModelLayer BREEZE = registerMain("breeze");
	public static final EntityModelLayer BREEZE_WIND = registerMain("breeze_wind");
	public static final EntityModelLayer CAMEL = registerMain("camel");
	public static final EntityModelLayer CAMEL_BABY = registerMain("camel_baby");
	public static final EntityModelLayer CAT = registerMain("cat");
	public static final EntityModelLayer CAT_BABY = registerMain("cat_baby");
	public static final EntityModelLayer CAT_BABY_COLLAR = register("cat_baby", "collar");
	public static final EntityModelLayer CAT_COLLAR = register("cat", "collar");
	public static final EntityModelLayer CAVE_SPIDER = registerMain("cave_spider");
	public static final EntityModelLayer CHERRY_BOAT = registerMain("boat/cherry");
	public static final EntityModelLayer CHERRY_CHEST_BOAT = registerMain("chest_boat/cherry");
	public static final EntityModelLayer CHEST = registerMain("chest");
	public static final EntityModelLayer CHEST_MINECART = registerMain("chest_minecart");
	public static final EntityModelLayer CHICKEN = registerMain("chicken");
	public static final EntityModelLayer CHICKEN_BABY = registerMain("chicken_baby");
	public static final EntityModelLayer COD = registerMain("cod");
	public static final EntityModelLayer COMMAND_BLOCK_MINECART = registerMain("command_block_minecart");
	public static final EntityModelLayer CONDUIT = register("conduit", "cage");
	public static final EntityModelLayer CONDUIT_EYE = register("conduit", "eye");
	public static final EntityModelLayer CONDUIT_SHELL = register("conduit", "shell");
	public static final EntityModelLayer CONDUIT_WIND = register("conduit", "wind");
	public static final EntityModelLayer COW = registerMain("cow");
	public static final EntityModelLayer COW_BABY = registerMain("cow_baby");
	public static final EntityModelLayer CREAKING = registerMain("creaking");
	public static final EntityModelLayer CREEPER = registerMain("creeper");
	public static final EntityModelLayer CREEPER_ARMOR = register("creeper", "armor");
	public static final EntityModelLayer CREEPER_HEAD = registerMain("creeper_head");
	public static final EntityModelLayer DARK_OAK_BOAT = registerMain("boat/dark_oak");
	public static final EntityModelLayer DARK_OAK_CHEST_BOAT = registerMain("chest_boat/dark_oak");
	public static final EntityModelLayer DECORATED_POT_BASE = registerMain("decorated_pot_base");
	public static final EntityModelLayer DECORATED_POT_SIDES = registerMain("decorated_pot_sides");
	public static final EntityModelLayer DOLPHIN = registerMain("dolphin");
	public static final EntityModelLayer DOLPHIN_BABY = registerMain("dolphin_baby");
	public static final EntityModelLayer DONKEY = registerMain("donkey");
	public static final EntityModelLayer DONKEY_BABY = registerMain("donkey_baby");
	public static final EntityModelLayer DOUBLE_CHEST_LEFT = registerMain("double_chest_left");
	public static final EntityModelLayer DOUBLE_CHEST_RIGHT = registerMain("double_chest_right");
	public static final EntityModelLayer DRAGON_SKULL = registerMain("dragon_skull");
	public static final EntityModelLayer DROWNED = registerMain("drowned");
	public static final EntityModelLayer DROWNED_BABY = registerMain("drowned_baby");
	public static final EntityModelLayer DROWNED_BABY_INNER_ARMOR = createInnerArmor("drowned_baby");
	public static final EntityModelLayer DROWNED_BABY_OUTER_ARMOR = createOuterArmor("drowned_baby");
	public static final EntityModelLayer DROWNED_BABY_OUTER = register("drowned_baby", "outer");
	public static final EntityModelLayer DROWNED_INNER_ARMOR = createInnerArmor("drowned");
	public static final EntityModelLayer DROWNED_OUTER_ARMOR = createOuterArmor("drowned");
	public static final EntityModelLayer DROWNED_OUTER = register("drowned", "outer");
	public static final EntityModelLayer ELDER_GUARDIAN = registerMain("elder_guardian");
	public static final EntityModelLayer ELYTRA = registerMain("elytra");
	public static final EntityModelLayer ELYTRA_BABY = registerMain("elytra_baby");
	public static final EntityModelLayer ENDERMAN = registerMain("enderman");
	public static final EntityModelLayer ENDERMITE = registerMain("endermite");
	public static final EntityModelLayer ENDER_DRAGON = registerMain("ender_dragon");
	public static final EntityModelLayer END_CRYSTAL = registerMain("end_crystal");
	public static final EntityModelLayer EVOKER = registerMain("evoker");
	public static final EntityModelLayer EVOKER_FANGS = registerMain("evoker_fangs");
	public static final EntityModelLayer FOX = registerMain("fox");
	public static final EntityModelLayer FOX_BABY = registerMain("fox_baby");
	public static final EntityModelLayer FROG = registerMain("frog");
	public static final EntityModelLayer FURNACE_MINECART = registerMain("furnace_minecart");
	public static final EntityModelLayer GHAST = registerMain("ghast");
	public static final EntityModelLayer GIANT = registerMain("giant");
	public static final EntityModelLayer GIANT_INNER_ARMOR = createInnerArmor("giant");
	public static final EntityModelLayer GIANT_OUTER_ARMOR = createOuterArmor("giant");
	public static final EntityModelLayer GLOW_SQUID = registerMain("glow_squid");
	public static final EntityModelLayer GLOW_SQUID_BABY = registerMain("glow_squid_baby");
	public static final EntityModelLayer GOAT = registerMain("goat");
	public static final EntityModelLayer GOAT_BABY = registerMain("goat_baby");
	public static final EntityModelLayer GUARDIAN = registerMain("guardian");
	public static final EntityModelLayer HOGLIN = registerMain("hoglin");
	public static final EntityModelLayer HOGLIN_BABY = registerMain("hoglin_baby");
	public static final EntityModelLayer HOPPER_MINECART = registerMain("hopper_minecart");
	public static final EntityModelLayer HORSE = registerMain("horse");
	public static final EntityModelLayer HORSE_ARMOR = registerMain("horse_armor");
	public static final EntityModelLayer HORSE_BABY = registerMain("horse_baby");
	public static final EntityModelLayer HORSE_ARMOR_BABY = registerMain("horse_armor_baby");
	public static final EntityModelLayer HUSK = registerMain("husk");
	public static final EntityModelLayer HUSK_BABY = registerMain("husk_baby");
	public static final EntityModelLayer HUSK_BABY_INNER_ARMOR = createInnerArmor("husk_baby");
	public static final EntityModelLayer HUSK_BABY_OUTER_ARMOR = createOuterArmor("husk_baby");
	public static final EntityModelLayer HUSK_INNER_ARMOR = createInnerArmor("husk");
	public static final EntityModelLayer HUSK_OUTER_ARMOR = createOuterArmor("husk");
	public static final EntityModelLayer ILLUSIONER = registerMain("illusioner");
	public static final EntityModelLayer IRON_GOLEM = registerMain("iron_golem");
	public static final EntityModelLayer JUNGLE_BOAT = registerMain("boat/jungle");
	public static final EntityModelLayer JUNGLE_CHEST_BOAT = registerMain("chest_boat/jungle");
	public static final EntityModelLayer LEASH_KNOT = registerMain("leash_knot");
	public static final EntityModelLayer LLAMA = registerMain("llama");
	public static final EntityModelLayer LLAMA_BABY = registerMain("llama_baby");
	public static final EntityModelLayer LLAMA_BABY_DECOR = register("llama_baby", "decor");
	public static final EntityModelLayer LLAMA_DECOR = register("llama", "decor");
	public static final EntityModelLayer LLAMA_SPIT = registerMain("llama_spit");
	public static final EntityModelLayer MAGMA_CUBE = registerMain("magma_cube");
	public static final EntityModelLayer MANGROVE_BOAT = registerMain("boat/mangrove");
	public static final EntityModelLayer MANGROVE_CHEST_BOAT = registerMain("chest_boat/mangrove");
	public static final EntityModelLayer MINECART = registerMain("minecart");
	public static final EntityModelLayer MOOSHROOM = registerMain("mooshroom");
	public static final EntityModelLayer MOOSHROOM_BABY = registerMain("mooshroom_baby");
	public static final EntityModelLayer MULE = registerMain("mule");
	public static final EntityModelLayer MULE_BABY = registerMain("mule_baby");
	public static final EntityModelLayer OAK_BOAT = registerMain("boat/oak");
	public static final EntityModelLayer OAK_CHEST_BOAT = registerMain("chest_boat/oak");
	public static final EntityModelLayer OCELOT = registerMain("ocelot");
	public static final EntityModelLayer OCELOT_BABY = registerMain("ocelot_baby");
	public static final EntityModelLayer PALE_OAK_BOAT = registerMain("boat/pale_oak");
	public static final EntityModelLayer PALE_OAK_CHEST_BOAT = registerMain("chest_boat/pale_oak");
	public static final EntityModelLayer PANDA = registerMain("panda");
	public static final EntityModelLayer PANDA_BABY = registerMain("panda_baby");
	public static final EntityModelLayer PARROT = registerMain("parrot");
	public static final EntityModelLayer PHANTOM = registerMain("phantom");
	public static final EntityModelLayer PIG = registerMain("pig");
	public static final EntityModelLayer PIGLIN = registerMain("piglin");
	public static final EntityModelLayer PIGLIN_BABY = registerMain("piglin_baby");
	public static final EntityModelLayer PIGLIN_BABY_INNER_ARMOR = createInnerArmor("piglin_baby");
	public static final EntityModelLayer PIGLIN_BABY_OUTER_ARMOR = createOuterArmor("piglin_baby");
	public static final EntityModelLayer PIGLIN_BRUTE = registerMain("piglin_brute");
	public static final EntityModelLayer PIGLIN_BRUTE_INNER_ARMOR = createInnerArmor("piglin_brute");
	public static final EntityModelLayer PIGLIN_BRUTE_OUTER_ARMOR = createOuterArmor("piglin_brute");
	public static final EntityModelLayer PIGLIN_HEAD = registerMain("piglin_head");
	public static final EntityModelLayer PIGLIN_INNER_ARMOR = createInnerArmor("piglin");
	public static final EntityModelLayer PIGLIN_OUTER_ARMOR = createOuterArmor("piglin");
	public static final EntityModelLayer PIG_BABY = registerMain("pig_baby");
	public static final EntityModelLayer PIG_BABY_SADDLE = register("pig_baby", "saddle");
	public static final EntityModelLayer PIG_SADDLE = register("pig", "saddle");
	public static final EntityModelLayer PILLAGER = registerMain("pillager");
	public static final EntityModelLayer PLAYER = registerMain("player");
	public static final EntityModelLayer PLAYER_CAPE = register("player", "cape");
	public static final EntityModelLayer PLAYER_EARS = register("player", "ears");
	public static final EntityModelLayer PLAYER_HEAD = registerMain("player_head");
	public static final EntityModelLayer PLAYER_INNER_ARMOR = createInnerArmor("player");
	public static final EntityModelLayer PLAYER_OUTER_ARMOR = createOuterArmor("player");
	public static final EntityModelLayer PLAYER_SLIM = registerMain("player_slim");
	public static final EntityModelLayer PLAYER_SLIM_INNER_ARMOR = createInnerArmor("player_slim");
	public static final EntityModelLayer PLAYER_SLIM_OUTER_ARMOR = createOuterArmor("player_slim");
	public static final EntityModelLayer SPIN_ATTACK = registerMain("spin_attack");
	public static final EntityModelLayer POLAR_BEAR = registerMain("polar_bear");
	public static final EntityModelLayer POLAR_BEAR_BABY = registerMain("polar_bear_baby");
	public static final EntityModelLayer PUFFERFISH_BIG = registerMain("pufferfish_big");
	public static final EntityModelLayer PUFFERFISH_MEDIUM = registerMain("pufferfish_medium");
	public static final EntityModelLayer PUFFERFISH_SMALL = registerMain("pufferfish_small");
	public static final EntityModelLayer RABBIT = registerMain("rabbit");
	public static final EntityModelLayer RABBIT_BABY = registerMain("rabbit_baby");
	public static final EntityModelLayer RAVAGER = registerMain("ravager");
	public static final EntityModelLayer SALMON = registerMain("salmon");
	public static final EntityModelLayer SALMON_LARGE = registerMain("salmon_large");
	public static final EntityModelLayer SALMON_SMALL = registerMain("salmon_small");
	public static final EntityModelLayer SHEEP = registerMain("sheep");
	public static final EntityModelLayer SHEEP_BABY = registerMain("sheep_baby");
	public static final EntityModelLayer SHEEP_BABY_WOOL = register("sheep_baby", "wool");
	public static final EntityModelLayer SHEEP_WOOL = register("sheep", "wool");
	public static final EntityModelLayer SHIELD = registerMain("shield");
	public static final EntityModelLayer SHULKER = registerMain("shulker");
	public static final EntityModelLayer SHULKER_BOX = registerMain("shulker_box");
	public static final EntityModelLayer SHULKER_BULLET = registerMain("shulker_bullet");
	public static final EntityModelLayer SILVERFISH = registerMain("silverfish");
	public static final EntityModelLayer SKELETON = registerMain("skeleton");
	public static final EntityModelLayer SKELETON_HORSE = registerMain("skeleton_horse");
	public static final EntityModelLayer SKELETON_HORSE_BABY = registerMain("skeleton_horse_baby");
	public static final EntityModelLayer SKELETON_INNER_ARMOR = createInnerArmor("skeleton");
	public static final EntityModelLayer SKELETON_OUTER_ARMOR = createOuterArmor("skeleton");
	public static final EntityModelLayer SKELETON_SKULL = registerMain("skeleton_skull");
	public static final EntityModelLayer SLIME = registerMain("slime");
	public static final EntityModelLayer SLIME_OUTER = register("slime", "outer");
	public static final EntityModelLayer SNIFFER = registerMain("sniffer");
	public static final EntityModelLayer SNIFFER_BABY = registerMain("sniffer_baby");
	public static final EntityModelLayer SNOW_GOLEM = registerMain("snow_golem");
	public static final EntityModelLayer SPAWNER_MINECART = registerMain("spawner_minecart");
	public static final EntityModelLayer SPIDER = registerMain("spider");
	public static final EntityModelLayer SPRUCE_BOAT = registerMain("boat/spruce");
	public static final EntityModelLayer SPRUCE_CHEST_BOAT = registerMain("chest_boat/spruce");
	public static final EntityModelLayer SQUID = registerMain("squid");
	public static final EntityModelLayer SQUID_BABY = registerMain("squid_baby");
	public static final EntityModelLayer STRAY = registerMain("stray");
	public static final EntityModelLayer STRAY_INNER_ARMOR = createInnerArmor("stray");
	public static final EntityModelLayer STRAY_OUTER_ARMOR = createOuterArmor("stray");
	public static final EntityModelLayer STRAY_OUTER = register("stray", "outer");
	public static final EntityModelLayer STRIDER = registerMain("strider");
	public static final EntityModelLayer STRIDER_SADDLE = register("strider", "saddle");
	public static final EntityModelLayer TADPOLE = registerMain("tadpole");
	public static final EntityModelLayer TNT_MINECART = registerMain("tnt_minecart");
	public static final EntityModelLayer TRADER_LLAMA = registerMain("trader_llama");
	public static final EntityModelLayer TRADER_LLAMA_BABY = registerMain("trader_llama_baby");
	public static final EntityModelLayer TRIDENT = registerMain("trident");
	public static final EntityModelLayer TROPICAL_FISH_LARGE = registerMain("tropical_fish_large");
	public static final EntityModelLayer TROPICAL_FISH_LARGE_PATTERN = register("tropical_fish_large", "pattern");
	public static final EntityModelLayer TROPICAL_FISH_SMALL = registerMain("tropical_fish_small");
	public static final EntityModelLayer TROPICAL_FISH_SMALL_PATTERN = register("tropical_fish_small", "pattern");
	public static final EntityModelLayer TURTLE = registerMain("turtle");
	public static final EntityModelLayer TURTLE_BABY = registerMain("turtle_baby");
	public static final EntityModelLayer VEX = registerMain("vex");
	public static final EntityModelLayer VILLAGER = registerMain("villager");
	public static final EntityModelLayer VINDICATOR = registerMain("vindicator");
	public static final EntityModelLayer WANDERING_TRADER = registerMain("wandering_trader");
	public static final EntityModelLayer WARDEN = registerMain("warden");
	public static final EntityModelLayer WIND_CHARGE = registerMain("wind_charge");
	public static final EntityModelLayer WITCH = registerMain("witch");
	public static final EntityModelLayer WITHER = registerMain("wither");
	public static final EntityModelLayer WITHER_ARMOR = register("wither", "armor");
	public static final EntityModelLayer WITHER_SKELETON = registerMain("wither_skeleton");
	public static final EntityModelLayer WITHER_SKELETON_INNER_ARMOR = createInnerArmor("wither_skeleton");
	public static final EntityModelLayer WITHER_SKELETON_OUTER_ARMOR = createOuterArmor("wither_skeleton");
	public static final EntityModelLayer WITHER_SKELETON_SKULL = registerMain("wither_skeleton_skull");
	public static final EntityModelLayer WITHER_SKULL = registerMain("wither_skull");
	public static final EntityModelLayer WOLF = registerMain("wolf");
	public static final EntityModelLayer WOLF_ARMOR = registerMain("wolf_armor");
	public static final EntityModelLayer WOLF_BABY = registerMain("wolf_baby");
	public static final EntityModelLayer WOLF_BABY_ARMOR = registerMain("wolf_baby_armor");
	public static final EntityModelLayer ZOGLIN = registerMain("zoglin");
	public static final EntityModelLayer ZOGLIN_BABY = registerMain("zoglin_baby");
	public static final EntityModelLayer ZOMBIE = registerMain("zombie");
	public static final EntityModelLayer ZOMBIE_BABY = registerMain("zombie_baby");
	public static final EntityModelLayer ZOMBIE_BABY_INNER_ARMOR = createInnerArmor("zombie_baby");
	public static final EntityModelLayer ZOMBIE_BABY_OUTER_ARMOR = createOuterArmor("zombie_baby");
	public static final EntityModelLayer ZOMBIE_HEAD = registerMain("zombie_head");
	public static final EntityModelLayer ZOMBIE_HORSE = registerMain("zombie_horse");
	public static final EntityModelLayer ZOMBIE_HORSE_BABY = registerMain("zombie_horse_baby");
	public static final EntityModelLayer ZOMBIE_INNER_ARMOR = createInnerArmor("zombie");
	public static final EntityModelLayer ZOMBIE_OUTER_ARMOR = createOuterArmor("zombie");
	public static final EntityModelLayer ZOMBIE_VILLAGER = registerMain("zombie_villager");
	public static final EntityModelLayer ZOMBIE_VILLAGER_BABY = registerMain("zombie_villager_baby");
	public static final EntityModelLayer ZOMBIE_VILLAGER_BABY_INNER_ARMOR = createInnerArmor("zombie_villager_baby");
	public static final EntityModelLayer ZOMBIE_VILLAGER_BABY_OUTER_ARMOR = createOuterArmor("zombie_villager_baby");
	public static final EntityModelLayer ZOMBIE_VILLAGER_INNER_ARMOR = createInnerArmor("zombie_villager");
	public static final EntityModelLayer ZOMBIE_VILLAGER_OUTER_ARMOR = createOuterArmor("zombie_villager");
	public static final EntityModelLayer ZOMBIFIED_PIGLIN = registerMain("zombified_piglin");
	public static final EntityModelLayer ZOMBIFIED_PIGLIN_BABY = registerMain("zombified_piglin_baby");
	public static final EntityModelLayer ZOMBIFIED_PIGLIN_BABY_INNER_ARMOR = createInnerArmor("zombified_piglin_baby");
	public static final EntityModelLayer ZOMBIFIED_PIGLIN_BABY_OUTER_ARMOR = createOuterArmor("zombified_piglin_baby");
	public static final EntityModelLayer ZOMBIFIED_PIGLIN_INNER_ARMOR = createInnerArmor("zombified_piglin");
	public static final EntityModelLayer ZOMBIFIED_PIGLIN_OUTER_ARMOR = createOuterArmor("zombified_piglin");

	private static EntityModelLayer registerMain(String id) {
		return register(id, "main");
	}

	private static EntityModelLayer register(String id, String layer) {
		EntityModelLayer entityModelLayer = create(id, layer);
		if (!LAYERS.add(entityModelLayer)) {
			throw new IllegalStateException("Duplicate registration for " + entityModelLayer);
		} else {
			return entityModelLayer;
		}
	}

	private static EntityModelLayer create(String id, String layer) {
		return new EntityModelLayer(Identifier.ofVanilla(id), layer);
	}

	private static EntityModelLayer createInnerArmor(String id) {
		return register(id, "inner_armor");
	}

	private static EntityModelLayer createOuterArmor(String id) {
		return register(id, "outer_armor");
	}

	public static EntityModelLayer createStandingSign(WoodType type) {
		return create("sign/standing/" + type.name(), "main");
	}

	public static EntityModelLayer createWallSign(WoodType type) {
		return create("sign/wall/" + type.name(), "main");
	}

	public static EntityModelLayer createHangingSign(WoodType type) {
		return create("hanging_sign/" + type.name(), "main");
	}

	public static Stream<EntityModelLayer> getLayers() {
		return LAYERS.stream();
	}
}

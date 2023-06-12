package net.minecraft.sound;

public class BlockSoundGroup {
	public static final BlockSoundGroup INTENTIONALLY_EMPTY = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.INTENTIONALLY_EMPTY,
		SoundEvents.INTENTIONALLY_EMPTY,
		SoundEvents.INTENTIONALLY_EMPTY,
		SoundEvents.INTENTIONALLY_EMPTY,
		SoundEvents.INTENTIONALLY_EMPTY
	);
	public static final BlockSoundGroup WOOD = new BlockSoundGroup(
		1.0F, 1.0F, SoundEvents.BLOCK_WOOD_BREAK, SoundEvents.BLOCK_WOOD_STEP, SoundEvents.BLOCK_WOOD_PLACE, SoundEvents.BLOCK_WOOD_HIT, SoundEvents.BLOCK_WOOD_FALL
	);
	public static final BlockSoundGroup GRAVEL = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_GRAVEL_BREAK,
		SoundEvents.BLOCK_GRAVEL_STEP,
		SoundEvents.BLOCK_GRAVEL_PLACE,
		SoundEvents.BLOCK_GRAVEL_HIT,
		SoundEvents.BLOCK_GRAVEL_FALL
	);
	public static final BlockSoundGroup GRASS = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_GRASS_BREAK,
		SoundEvents.BLOCK_GRASS_STEP,
		SoundEvents.BLOCK_GRASS_PLACE,
		SoundEvents.BLOCK_GRASS_HIT,
		SoundEvents.BLOCK_GRASS_FALL
	);
	public static final BlockSoundGroup LILY_PAD = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_BIG_DRIPLEAF_BREAK,
		SoundEvents.BLOCK_BIG_DRIPLEAF_STEP,
		SoundEvents.BLOCK_LILY_PAD_PLACE,
		SoundEvents.BLOCK_BIG_DRIPLEAF_HIT,
		SoundEvents.BLOCK_BIG_DRIPLEAF_FALL
	);
	public static final BlockSoundGroup STONE = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_STONE_BREAK,
		SoundEvents.BLOCK_STONE_STEP,
		SoundEvents.BLOCK_STONE_PLACE,
		SoundEvents.BLOCK_STONE_HIT,
		SoundEvents.BLOCK_STONE_FALL
	);
	public static final BlockSoundGroup METAL = new BlockSoundGroup(
		1.0F,
		1.5F,
		SoundEvents.BLOCK_METAL_BREAK,
		SoundEvents.BLOCK_METAL_STEP,
		SoundEvents.BLOCK_METAL_PLACE,
		SoundEvents.BLOCK_METAL_HIT,
		SoundEvents.BLOCK_METAL_FALL
	);
	public static final BlockSoundGroup GLASS = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_GLASS_BREAK,
		SoundEvents.BLOCK_GLASS_STEP,
		SoundEvents.BLOCK_GLASS_PLACE,
		SoundEvents.BLOCK_GLASS_HIT,
		SoundEvents.BLOCK_GLASS_FALL
	);
	public static final BlockSoundGroup WOOL = new BlockSoundGroup(
		1.0F, 1.0F, SoundEvents.BLOCK_WOOL_BREAK, SoundEvents.BLOCK_WOOL_STEP, SoundEvents.BLOCK_WOOL_PLACE, SoundEvents.BLOCK_WOOL_HIT, SoundEvents.BLOCK_WOOL_FALL
	);
	public static final BlockSoundGroup SAND = new BlockSoundGroup(
		1.0F, 1.0F, SoundEvents.BLOCK_SAND_BREAK, SoundEvents.BLOCK_SAND_STEP, SoundEvents.BLOCK_SAND_PLACE, SoundEvents.BLOCK_SAND_HIT, SoundEvents.BLOCK_SAND_FALL
	);
	public static final BlockSoundGroup SNOW = new BlockSoundGroup(
		1.0F, 1.0F, SoundEvents.BLOCK_SNOW_BREAK, SoundEvents.BLOCK_SNOW_STEP, SoundEvents.BLOCK_SNOW_PLACE, SoundEvents.BLOCK_SNOW_HIT, SoundEvents.BLOCK_SNOW_FALL
	);
	public static final BlockSoundGroup POWDER_SNOW = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_POWDER_SNOW_BREAK,
		SoundEvents.BLOCK_POWDER_SNOW_STEP,
		SoundEvents.BLOCK_POWDER_SNOW_PLACE,
		SoundEvents.BLOCK_POWDER_SNOW_HIT,
		SoundEvents.BLOCK_POWDER_SNOW_FALL
	);
	public static final BlockSoundGroup LADDER = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_LADDER_BREAK,
		SoundEvents.BLOCK_LADDER_STEP,
		SoundEvents.BLOCK_LADDER_PLACE,
		SoundEvents.BLOCK_LADDER_HIT,
		SoundEvents.BLOCK_LADDER_FALL
	);
	public static final BlockSoundGroup ANVIL = new BlockSoundGroup(
		0.3F,
		1.0F,
		SoundEvents.BLOCK_ANVIL_BREAK,
		SoundEvents.BLOCK_ANVIL_STEP,
		SoundEvents.BLOCK_ANVIL_PLACE,
		SoundEvents.BLOCK_ANVIL_HIT,
		SoundEvents.BLOCK_ANVIL_FALL
	);
	public static final BlockSoundGroup SLIME = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_SLIME_BLOCK_BREAK,
		SoundEvents.BLOCK_SLIME_BLOCK_STEP,
		SoundEvents.BLOCK_SLIME_BLOCK_PLACE,
		SoundEvents.BLOCK_SLIME_BLOCK_HIT,
		SoundEvents.BLOCK_SLIME_BLOCK_FALL
	);
	public static final BlockSoundGroup HONEY = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_HONEY_BLOCK_BREAK,
		SoundEvents.BLOCK_HONEY_BLOCK_STEP,
		SoundEvents.BLOCK_HONEY_BLOCK_PLACE,
		SoundEvents.BLOCK_HONEY_BLOCK_HIT,
		SoundEvents.BLOCK_HONEY_BLOCK_FALL
	);
	public static final BlockSoundGroup WET_GRASS = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_WET_GRASS_BREAK,
		SoundEvents.BLOCK_WET_GRASS_STEP,
		SoundEvents.BLOCK_WET_GRASS_PLACE,
		SoundEvents.BLOCK_WET_GRASS_HIT,
		SoundEvents.BLOCK_WET_GRASS_FALL
	);
	public static final BlockSoundGroup CORAL = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_CORAL_BLOCK_BREAK,
		SoundEvents.BLOCK_CORAL_BLOCK_STEP,
		SoundEvents.BLOCK_CORAL_BLOCK_PLACE,
		SoundEvents.BLOCK_CORAL_BLOCK_HIT,
		SoundEvents.BLOCK_CORAL_BLOCK_FALL
	);
	public static final BlockSoundGroup BAMBOO = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_BAMBOO_BREAK,
		SoundEvents.BLOCK_BAMBOO_STEP,
		SoundEvents.BLOCK_BAMBOO_PLACE,
		SoundEvents.BLOCK_BAMBOO_HIT,
		SoundEvents.BLOCK_BAMBOO_FALL
	);
	public static final BlockSoundGroup BAMBOO_SAPLING = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_BAMBOO_SAPLING_BREAK,
		SoundEvents.BLOCK_BAMBOO_STEP,
		SoundEvents.BLOCK_BAMBOO_SAPLING_PLACE,
		SoundEvents.BLOCK_BAMBOO_SAPLING_HIT,
		SoundEvents.BLOCK_BAMBOO_FALL
	);
	public static final BlockSoundGroup SCAFFOLDING = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_SCAFFOLDING_BREAK,
		SoundEvents.BLOCK_SCAFFOLDING_STEP,
		SoundEvents.BLOCK_SCAFFOLDING_PLACE,
		SoundEvents.BLOCK_SCAFFOLDING_HIT,
		SoundEvents.BLOCK_SCAFFOLDING_FALL
	);
	public static final BlockSoundGroup SWEET_BERRY_BUSH = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_SWEET_BERRY_BUSH_BREAK,
		SoundEvents.BLOCK_GRASS_STEP,
		SoundEvents.BLOCK_SWEET_BERRY_BUSH_PLACE,
		SoundEvents.BLOCK_GRASS_HIT,
		SoundEvents.BLOCK_GRASS_FALL
	);
	public static final BlockSoundGroup CROP = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_CROP_BREAK,
		SoundEvents.BLOCK_GRASS_STEP,
		SoundEvents.ITEM_CROP_PLANT,
		SoundEvents.BLOCK_GRASS_HIT,
		SoundEvents.BLOCK_GRASS_FALL
	);
	public static final BlockSoundGroup STEM = new BlockSoundGroup(
		1.0F, 1.0F, SoundEvents.BLOCK_WOOD_BREAK, SoundEvents.BLOCK_WOOD_STEP, SoundEvents.ITEM_CROP_PLANT, SoundEvents.BLOCK_WOOD_HIT, SoundEvents.BLOCK_WOOD_FALL
	);
	public static final BlockSoundGroup VINE = new BlockSoundGroup(
		1.0F, 1.0F, SoundEvents.BLOCK_VINE_BREAK, SoundEvents.BLOCK_VINE_STEP, SoundEvents.BLOCK_VINE_PLACE, SoundEvents.BLOCK_VINE_HIT, SoundEvents.BLOCK_VINE_FALL
	);
	public static final BlockSoundGroup NETHER_WART = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_NETHER_WART_BREAK,
		SoundEvents.BLOCK_STONE_STEP,
		SoundEvents.ITEM_NETHER_WART_PLANT,
		SoundEvents.BLOCK_STONE_HIT,
		SoundEvents.BLOCK_STONE_FALL
	);
	public static final BlockSoundGroup LANTERN = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_LANTERN_BREAK,
		SoundEvents.BLOCK_LANTERN_STEP,
		SoundEvents.BLOCK_LANTERN_PLACE,
		SoundEvents.BLOCK_LANTERN_HIT,
		SoundEvents.BLOCK_LANTERN_FALL
	);
	public static final BlockSoundGroup NETHER_STEM = new BlockSoundGroup(
		1.0F, 1.0F, SoundEvents.BLOCK_STEM_BREAK, SoundEvents.BLOCK_STEM_STEP, SoundEvents.BLOCK_STEM_PLACE, SoundEvents.BLOCK_STEM_HIT, SoundEvents.BLOCK_STEM_FALL
	);
	public static final BlockSoundGroup NYLIUM = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_NYLIUM_BREAK,
		SoundEvents.BLOCK_NYLIUM_STEP,
		SoundEvents.BLOCK_NYLIUM_PLACE,
		SoundEvents.BLOCK_NYLIUM_HIT,
		SoundEvents.BLOCK_NYLIUM_FALL
	);
	public static final BlockSoundGroup FUNGUS = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_FUNGUS_BREAK,
		SoundEvents.BLOCK_FUNGUS_STEP,
		SoundEvents.BLOCK_FUNGUS_PLACE,
		SoundEvents.BLOCK_FUNGUS_HIT,
		SoundEvents.BLOCK_FUNGUS_FALL
	);
	public static final BlockSoundGroup ROOTS = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_ROOTS_BREAK,
		SoundEvents.BLOCK_ROOTS_STEP,
		SoundEvents.BLOCK_ROOTS_PLACE,
		SoundEvents.BLOCK_ROOTS_HIT,
		SoundEvents.BLOCK_ROOTS_FALL
	);
	public static final BlockSoundGroup SHROOMLIGHT = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_SHROOMLIGHT_BREAK,
		SoundEvents.BLOCK_SHROOMLIGHT_STEP,
		SoundEvents.BLOCK_SHROOMLIGHT_PLACE,
		SoundEvents.BLOCK_SHROOMLIGHT_HIT,
		SoundEvents.BLOCK_SHROOMLIGHT_FALL
	);
	public static final BlockSoundGroup WEEPING_VINES = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_WEEPING_VINES_BREAK,
		SoundEvents.BLOCK_WEEPING_VINES_STEP,
		SoundEvents.BLOCK_WEEPING_VINES_PLACE,
		SoundEvents.BLOCK_WEEPING_VINES_HIT,
		SoundEvents.BLOCK_WEEPING_VINES_FALL
	);
	public static final BlockSoundGroup WEEPING_VINES_LOW_PITCH = new BlockSoundGroup(
		1.0F,
		0.5F,
		SoundEvents.BLOCK_WEEPING_VINES_BREAK,
		SoundEvents.BLOCK_WEEPING_VINES_STEP,
		SoundEvents.BLOCK_WEEPING_VINES_PLACE,
		SoundEvents.BLOCK_WEEPING_VINES_HIT,
		SoundEvents.BLOCK_WEEPING_VINES_FALL
	);
	public static final BlockSoundGroup SOUL_SAND = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_SOUL_SAND_BREAK,
		SoundEvents.BLOCK_SOUL_SAND_STEP,
		SoundEvents.BLOCK_SOUL_SAND_PLACE,
		SoundEvents.BLOCK_SOUL_SAND_HIT,
		SoundEvents.BLOCK_SOUL_SAND_FALL
	);
	public static final BlockSoundGroup SOUL_SOIL = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_SOUL_SOIL_BREAK,
		SoundEvents.BLOCK_SOUL_SOIL_STEP,
		SoundEvents.BLOCK_SOUL_SOIL_PLACE,
		SoundEvents.BLOCK_SOUL_SOIL_HIT,
		SoundEvents.BLOCK_SOUL_SOIL_FALL
	);
	public static final BlockSoundGroup BASALT = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_BASALT_BREAK,
		SoundEvents.BLOCK_BASALT_STEP,
		SoundEvents.BLOCK_BASALT_PLACE,
		SoundEvents.BLOCK_BASALT_HIT,
		SoundEvents.BLOCK_BASALT_FALL
	);
	public static final BlockSoundGroup WART_BLOCK = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_WART_BLOCK_BREAK,
		SoundEvents.BLOCK_WART_BLOCK_STEP,
		SoundEvents.BLOCK_WART_BLOCK_PLACE,
		SoundEvents.BLOCK_WART_BLOCK_HIT,
		SoundEvents.BLOCK_WART_BLOCK_FALL
	);
	public static final BlockSoundGroup NETHERRACK = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_NETHERRACK_BREAK,
		SoundEvents.BLOCK_NETHERRACK_STEP,
		SoundEvents.BLOCK_NETHERRACK_PLACE,
		SoundEvents.BLOCK_NETHERRACK_HIT,
		SoundEvents.BLOCK_NETHERRACK_FALL
	);
	public static final BlockSoundGroup NETHER_BRICKS = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_NETHER_BRICKS_BREAK,
		SoundEvents.BLOCK_NETHER_BRICKS_STEP,
		SoundEvents.BLOCK_NETHER_BRICKS_PLACE,
		SoundEvents.BLOCK_NETHER_BRICKS_HIT,
		SoundEvents.BLOCK_NETHER_BRICKS_FALL
	);
	public static final BlockSoundGroup NETHER_SPROUTS = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_NETHER_SPROUTS_BREAK,
		SoundEvents.BLOCK_NETHER_SPROUTS_STEP,
		SoundEvents.BLOCK_NETHER_SPROUTS_PLACE,
		SoundEvents.BLOCK_NETHER_SPROUTS_HIT,
		SoundEvents.BLOCK_NETHER_SPROUTS_FALL
	);
	public static final BlockSoundGroup NETHER_ORE = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_NETHER_ORE_BREAK,
		SoundEvents.BLOCK_NETHER_ORE_STEP,
		SoundEvents.BLOCK_NETHER_ORE_PLACE,
		SoundEvents.BLOCK_NETHER_ORE_HIT,
		SoundEvents.BLOCK_NETHER_ORE_FALL
	);
	public static final BlockSoundGroup BONE = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_BONE_BLOCK_BREAK,
		SoundEvents.BLOCK_BONE_BLOCK_STEP,
		SoundEvents.BLOCK_BONE_BLOCK_PLACE,
		SoundEvents.BLOCK_BONE_BLOCK_HIT,
		SoundEvents.BLOCK_BONE_BLOCK_FALL
	);
	public static final BlockSoundGroup NETHERITE = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_NETHERITE_BLOCK_BREAK,
		SoundEvents.BLOCK_NETHERITE_BLOCK_STEP,
		SoundEvents.BLOCK_NETHERITE_BLOCK_PLACE,
		SoundEvents.BLOCK_NETHERITE_BLOCK_HIT,
		SoundEvents.BLOCK_NETHERITE_BLOCK_FALL
	);
	public static final BlockSoundGroup ANCIENT_DEBRIS = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_ANCIENT_DEBRIS_BREAK,
		SoundEvents.BLOCK_ANCIENT_DEBRIS_STEP,
		SoundEvents.BLOCK_ANCIENT_DEBRIS_PLACE,
		SoundEvents.BLOCK_ANCIENT_DEBRIS_HIT,
		SoundEvents.BLOCK_ANCIENT_DEBRIS_FALL
	);
	public static final BlockSoundGroup LODESTONE = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_LODESTONE_BREAK,
		SoundEvents.BLOCK_LODESTONE_STEP,
		SoundEvents.BLOCK_LODESTONE_PLACE,
		SoundEvents.BLOCK_LODESTONE_HIT,
		SoundEvents.BLOCK_LODESTONE_FALL
	);
	public static final BlockSoundGroup CHAIN = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_CHAIN_BREAK,
		SoundEvents.BLOCK_CHAIN_STEP,
		SoundEvents.BLOCK_CHAIN_PLACE,
		SoundEvents.BLOCK_CHAIN_HIT,
		SoundEvents.BLOCK_CHAIN_FALL
	);
	public static final BlockSoundGroup NETHER_GOLD_ORE = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_NETHER_GOLD_ORE_BREAK,
		SoundEvents.BLOCK_NETHER_GOLD_ORE_STEP,
		SoundEvents.BLOCK_NETHER_GOLD_ORE_PLACE,
		SoundEvents.BLOCK_NETHER_GOLD_ORE_HIT,
		SoundEvents.BLOCK_NETHER_GOLD_ORE_FALL
	);
	public static final BlockSoundGroup GILDED_BLACKSTONE = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_GILDED_BLACKSTONE_BREAK,
		SoundEvents.BLOCK_GILDED_BLACKSTONE_STEP,
		SoundEvents.BLOCK_GILDED_BLACKSTONE_PLACE,
		SoundEvents.BLOCK_GILDED_BLACKSTONE_HIT,
		SoundEvents.BLOCK_GILDED_BLACKSTONE_FALL
	);
	public static final BlockSoundGroup CANDLE = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_CANDLE_BREAK,
		SoundEvents.BLOCK_CANDLE_STEP,
		SoundEvents.BLOCK_CANDLE_PLACE,
		SoundEvents.BLOCK_CANDLE_HIT,
		SoundEvents.BLOCK_CANDLE_FALL
	);
	public static final BlockSoundGroup AMETHYST_BLOCK = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_AMETHYST_BLOCK_BREAK,
		SoundEvents.BLOCK_AMETHYST_BLOCK_STEP,
		SoundEvents.BLOCK_AMETHYST_BLOCK_PLACE,
		SoundEvents.BLOCK_AMETHYST_BLOCK_HIT,
		SoundEvents.BLOCK_AMETHYST_BLOCK_FALL
	);
	public static final BlockSoundGroup AMETHYST_CLUSTER = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_AMETHYST_CLUSTER_BREAK,
		SoundEvents.BLOCK_AMETHYST_CLUSTER_STEP,
		SoundEvents.BLOCK_AMETHYST_CLUSTER_PLACE,
		SoundEvents.BLOCK_AMETHYST_CLUSTER_HIT,
		SoundEvents.BLOCK_AMETHYST_CLUSTER_FALL
	);
	public static final BlockSoundGroup SMALL_AMETHYST_BUD = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_SMALL_AMETHYST_BUD_BREAK,
		SoundEvents.BLOCK_AMETHYST_CLUSTER_STEP,
		SoundEvents.BLOCK_SMALL_AMETHYST_BUD_PLACE,
		SoundEvents.BLOCK_AMETHYST_CLUSTER_HIT,
		SoundEvents.BLOCK_AMETHYST_CLUSTER_FALL
	);
	public static final BlockSoundGroup MEDIUM_AMETHYST_BUD = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_MEDIUM_AMETHYST_BUD_BREAK,
		SoundEvents.BLOCK_AMETHYST_CLUSTER_STEP,
		SoundEvents.BLOCK_MEDIUM_AMETHYST_BUD_PLACE,
		SoundEvents.BLOCK_AMETHYST_CLUSTER_HIT,
		SoundEvents.BLOCK_AMETHYST_CLUSTER_FALL
	);
	public static final BlockSoundGroup LARGE_AMETHYST_BUD = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_LARGE_AMETHYST_BUD_BREAK,
		SoundEvents.BLOCK_AMETHYST_CLUSTER_STEP,
		SoundEvents.BLOCK_LARGE_AMETHYST_BUD_PLACE,
		SoundEvents.BLOCK_AMETHYST_CLUSTER_HIT,
		SoundEvents.BLOCK_AMETHYST_CLUSTER_FALL
	);
	public static final BlockSoundGroup TUFF = new BlockSoundGroup(
		1.0F, 1.0F, SoundEvents.BLOCK_TUFF_BREAK, SoundEvents.BLOCK_TUFF_STEP, SoundEvents.BLOCK_TUFF_PLACE, SoundEvents.BLOCK_TUFF_HIT, SoundEvents.BLOCK_TUFF_FALL
	);
	public static final BlockSoundGroup CALCITE = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_CALCITE_BREAK,
		SoundEvents.BLOCK_CALCITE_STEP,
		SoundEvents.BLOCK_CALCITE_PLACE,
		SoundEvents.BLOCK_CALCITE_HIT,
		SoundEvents.BLOCK_CALCITE_FALL
	);
	public static final BlockSoundGroup DRIPSTONE_BLOCK = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_DRIPSTONE_BLOCK_BREAK,
		SoundEvents.BLOCK_DRIPSTONE_BLOCK_STEP,
		SoundEvents.BLOCK_DRIPSTONE_BLOCK_PLACE,
		SoundEvents.BLOCK_DRIPSTONE_BLOCK_HIT,
		SoundEvents.BLOCK_DRIPSTONE_BLOCK_FALL
	);
	public static final BlockSoundGroup POINTED_DRIPSTONE = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_POINTED_DRIPSTONE_BREAK,
		SoundEvents.BLOCK_POINTED_DRIPSTONE_STEP,
		SoundEvents.BLOCK_POINTED_DRIPSTONE_PLACE,
		SoundEvents.BLOCK_POINTED_DRIPSTONE_HIT,
		SoundEvents.BLOCK_POINTED_DRIPSTONE_FALL
	);
	public static final BlockSoundGroup COPPER = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_COPPER_BREAK,
		SoundEvents.BLOCK_COPPER_STEP,
		SoundEvents.BLOCK_COPPER_PLACE,
		SoundEvents.BLOCK_COPPER_HIT,
		SoundEvents.BLOCK_COPPER_FALL
	);
	public static final BlockSoundGroup CAVE_VINES = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_CAVE_VINES_BREAK,
		SoundEvents.BLOCK_CAVE_VINES_STEP,
		SoundEvents.BLOCK_CAVE_VINES_PLACE,
		SoundEvents.BLOCK_CAVE_VINES_HIT,
		SoundEvents.BLOCK_CAVE_VINES_FALL
	);
	public static final BlockSoundGroup SPORE_BLOSSOM = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_SPORE_BLOSSOM_BREAK,
		SoundEvents.BLOCK_SPORE_BLOSSOM_STEP,
		SoundEvents.BLOCK_SPORE_BLOSSOM_PLACE,
		SoundEvents.BLOCK_SPORE_BLOSSOM_HIT,
		SoundEvents.BLOCK_SPORE_BLOSSOM_FALL
	);
	public static final BlockSoundGroup AZALEA = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_AZALEA_BREAK,
		SoundEvents.BLOCK_AZALEA_STEP,
		SoundEvents.BLOCK_AZALEA_PLACE,
		SoundEvents.BLOCK_AZALEA_HIT,
		SoundEvents.BLOCK_AZALEA_FALL
	);
	public static final BlockSoundGroup FLOWERING_AZALEA = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_FLOWERING_AZALEA_BREAK,
		SoundEvents.BLOCK_FLOWERING_AZALEA_STEP,
		SoundEvents.BLOCK_FLOWERING_AZALEA_PLACE,
		SoundEvents.BLOCK_FLOWERING_AZALEA_HIT,
		SoundEvents.BLOCK_FLOWERING_AZALEA_FALL
	);
	public static final BlockSoundGroup MOSS_CARPET = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_MOSS_CARPET_BREAK,
		SoundEvents.BLOCK_MOSS_CARPET_STEP,
		SoundEvents.BLOCK_MOSS_CARPET_PLACE,
		SoundEvents.BLOCK_MOSS_CARPET_HIT,
		SoundEvents.BLOCK_MOSS_CARPET_FALL
	);
	public static final BlockSoundGroup PINK_PETALS = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_PINK_PETALS_BREAK,
		SoundEvents.BLOCK_PINK_PETALS_STEP,
		SoundEvents.BLOCK_PINK_PETALS_PLACE,
		SoundEvents.BLOCK_PINK_PETALS_HIT,
		SoundEvents.BLOCK_PINK_PETALS_FALL
	);
	public static final BlockSoundGroup MOSS_BLOCK = new BlockSoundGroup(
		1.0F, 1.0F, SoundEvents.BLOCK_MOSS_BREAK, SoundEvents.BLOCK_MOSS_STEP, SoundEvents.BLOCK_MOSS_PLACE, SoundEvents.BLOCK_MOSS_HIT, SoundEvents.BLOCK_MOSS_FALL
	);
	public static final BlockSoundGroup BIG_DRIPLEAF = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_BIG_DRIPLEAF_BREAK,
		SoundEvents.BLOCK_BIG_DRIPLEAF_STEP,
		SoundEvents.BLOCK_BIG_DRIPLEAF_PLACE,
		SoundEvents.BLOCK_BIG_DRIPLEAF_HIT,
		SoundEvents.BLOCK_BIG_DRIPLEAF_FALL
	);
	public static final BlockSoundGroup SMALL_DRIPLEAF = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_SMALL_DRIPLEAF_BREAK,
		SoundEvents.BLOCK_SMALL_DRIPLEAF_STEP,
		SoundEvents.BLOCK_SMALL_DRIPLEAF_PLACE,
		SoundEvents.BLOCK_SMALL_DRIPLEAF_HIT,
		SoundEvents.BLOCK_SMALL_DRIPLEAF_FALL
	);
	public static final BlockSoundGroup ROOTED_DIRT = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_ROOTED_DIRT_BREAK,
		SoundEvents.BLOCK_ROOTED_DIRT_STEP,
		SoundEvents.BLOCK_ROOTED_DIRT_PLACE,
		SoundEvents.BLOCK_ROOTED_DIRT_HIT,
		SoundEvents.BLOCK_ROOTED_DIRT_FALL
	);
	public static final BlockSoundGroup HANGING_ROOTS = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_HANGING_ROOTS_BREAK,
		SoundEvents.BLOCK_HANGING_ROOTS_STEP,
		SoundEvents.BLOCK_HANGING_ROOTS_PLACE,
		SoundEvents.BLOCK_HANGING_ROOTS_HIT,
		SoundEvents.BLOCK_HANGING_ROOTS_FALL
	);
	public static final BlockSoundGroup AZALEA_LEAVES = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_AZALEA_LEAVES_BREAK,
		SoundEvents.BLOCK_AZALEA_LEAVES_STEP,
		SoundEvents.BLOCK_AZALEA_LEAVES_PLACE,
		SoundEvents.BLOCK_AZALEA_LEAVES_HIT,
		SoundEvents.BLOCK_AZALEA_LEAVES_FALL
	);
	public static final BlockSoundGroup SCULK_SENSOR = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_SCULK_SENSOR_BREAK,
		SoundEvents.BLOCK_SCULK_SENSOR_STEP,
		SoundEvents.BLOCK_SCULK_SENSOR_PLACE,
		SoundEvents.BLOCK_SCULK_SENSOR_HIT,
		SoundEvents.BLOCK_SCULK_SENSOR_FALL
	);
	public static final BlockSoundGroup SCULK_CATALYST = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_SCULK_CATALYST_BREAK,
		SoundEvents.BLOCK_SCULK_CATALYST_STEP,
		SoundEvents.BLOCK_SCULK_CATALYST_PLACE,
		SoundEvents.BLOCK_SCULK_CATALYST_HIT,
		SoundEvents.BLOCK_SCULK_CATALYST_FALL
	);
	public static final BlockSoundGroup SCULK = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_SCULK_BREAK,
		SoundEvents.BLOCK_SCULK_STEP,
		SoundEvents.BLOCK_SCULK_PLACE,
		SoundEvents.BLOCK_SCULK_HIT,
		SoundEvents.BLOCK_SCULK_FALL
	);
	public static final BlockSoundGroup SCULK_VEIN = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_SCULK_VEIN_BREAK,
		SoundEvents.BLOCK_SCULK_VEIN_STEP,
		SoundEvents.BLOCK_SCULK_VEIN_PLACE,
		SoundEvents.BLOCK_SCULK_VEIN_HIT,
		SoundEvents.BLOCK_SCULK_VEIN_FALL
	);
	public static final BlockSoundGroup SCULK_SHRIEKER = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_SCULK_SHRIEKER_BREAK,
		SoundEvents.BLOCK_SCULK_SHRIEKER_STEP,
		SoundEvents.BLOCK_SCULK_SHRIEKER_PLACE,
		SoundEvents.BLOCK_SCULK_SHRIEKER_HIT,
		SoundEvents.BLOCK_SCULK_SHRIEKER_FALL
	);
	public static final BlockSoundGroup GLOW_LICHEN = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_GRASS_BREAK,
		SoundEvents.BLOCK_VINE_STEP,
		SoundEvents.BLOCK_GRASS_PLACE,
		SoundEvents.BLOCK_GRASS_HIT,
		SoundEvents.BLOCK_GRASS_FALL
	);
	public static final BlockSoundGroup DEEPSLATE = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_DEEPSLATE_BREAK,
		SoundEvents.BLOCK_DEEPSLATE_STEP,
		SoundEvents.BLOCK_DEEPSLATE_PLACE,
		SoundEvents.BLOCK_DEEPSLATE_HIT,
		SoundEvents.BLOCK_DEEPSLATE_FALL
	);
	public static final BlockSoundGroup DEEPSLATE_BRICKS = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_DEEPSLATE_BRICKS_BREAK,
		SoundEvents.BLOCK_DEEPSLATE_BRICKS_STEP,
		SoundEvents.BLOCK_DEEPSLATE_BRICKS_PLACE,
		SoundEvents.BLOCK_DEEPSLATE_BRICKS_HIT,
		SoundEvents.BLOCK_DEEPSLATE_BRICKS_FALL
	);
	public static final BlockSoundGroup DEEPSLATE_TILES = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_DEEPSLATE_TILES_BREAK,
		SoundEvents.BLOCK_DEEPSLATE_TILES_STEP,
		SoundEvents.BLOCK_DEEPSLATE_TILES_PLACE,
		SoundEvents.BLOCK_DEEPSLATE_TILES_HIT,
		SoundEvents.BLOCK_DEEPSLATE_TILES_FALL
	);
	public static final BlockSoundGroup POLISHED_DEEPSLATE = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_POLISHED_DEEPSLATE_BREAK,
		SoundEvents.BLOCK_POLISHED_DEEPSLATE_STEP,
		SoundEvents.BLOCK_POLISHED_DEEPSLATE_PLACE,
		SoundEvents.BLOCK_POLISHED_DEEPSLATE_HIT,
		SoundEvents.BLOCK_POLISHED_DEEPSLATE_FALL
	);
	public static final BlockSoundGroup FROGLIGHT = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_FROGLIGHT_BREAK,
		SoundEvents.BLOCK_FROGLIGHT_STEP,
		SoundEvents.BLOCK_FROGLIGHT_PLACE,
		SoundEvents.BLOCK_FROGLIGHT_HIT,
		SoundEvents.BLOCK_FROGLIGHT_FALL
	);
	public static final BlockSoundGroup FROGSPAWN = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_FROGSPAWN_BREAK,
		SoundEvents.BLOCK_FROGSPAWN_STEP,
		SoundEvents.BLOCK_FROGSPAWN_PLACE,
		SoundEvents.BLOCK_FROGSPAWN_HIT,
		SoundEvents.BLOCK_FROGSPAWN_FALL
	);
	public static final BlockSoundGroup MANGROVE_ROOTS = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_MANGROVE_ROOTS_BREAK,
		SoundEvents.BLOCK_MANGROVE_ROOTS_STEP,
		SoundEvents.BLOCK_MANGROVE_ROOTS_PLACE,
		SoundEvents.BLOCK_MANGROVE_ROOTS_HIT,
		SoundEvents.BLOCK_MANGROVE_ROOTS_FALL
	);
	public static final BlockSoundGroup MUDDY_MANGROVE_ROOTS = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_MUDDY_MANGROVE_ROOTS_BREAK,
		SoundEvents.BLOCK_MUDDY_MANGROVE_ROOTS_STEP,
		SoundEvents.BLOCK_MUDDY_MANGROVE_ROOTS_PLACE,
		SoundEvents.BLOCK_MUDDY_MANGROVE_ROOTS_HIT,
		SoundEvents.BLOCK_MUDDY_MANGROVE_ROOTS_FALL
	);
	public static final BlockSoundGroup MUD = new BlockSoundGroup(
		1.0F, 1.0F, SoundEvents.BLOCK_MUD_BREAK, SoundEvents.BLOCK_MUD_STEP, SoundEvents.BLOCK_MUD_PLACE, SoundEvents.BLOCK_MUD_HIT, SoundEvents.BLOCK_MUD_FALL
	);
	public static final BlockSoundGroup MUD_BRICKS = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_MUD_BRICKS_BREAK,
		SoundEvents.BLOCK_MUD_BRICKS_STEP,
		SoundEvents.BLOCK_MUD_BRICKS_PLACE,
		SoundEvents.BLOCK_MUD_BRICKS_HIT,
		SoundEvents.BLOCK_MUD_BRICKS_FALL
	);
	public static final BlockSoundGroup PACKED_MUD = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_PACKED_MUD_BREAK,
		SoundEvents.BLOCK_PACKED_MUD_STEP,
		SoundEvents.BLOCK_PACKED_MUD_PLACE,
		SoundEvents.BLOCK_PACKED_MUD_HIT,
		SoundEvents.BLOCK_PACKED_MUD_FALL
	);
	public static final BlockSoundGroup HANGING_SIGN = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_HANGING_SIGN_BREAK,
		SoundEvents.BLOCK_HANGING_SIGN_STEP,
		SoundEvents.BLOCK_HANGING_SIGN_PLACE,
		SoundEvents.BLOCK_HANGING_SIGN_HIT,
		SoundEvents.BLOCK_HANGING_SIGN_FALL
	);
	public static final BlockSoundGroup NETHER_WOOD_HANGING_SIGN = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_NETHER_WOOD_HANGING_SIGN_BREAK,
		SoundEvents.BLOCK_NETHER_WOOD_HANGING_SIGN_STEP,
		SoundEvents.BLOCK_NETHER_WOOD_HANGING_SIGN_PLACE,
		SoundEvents.BLOCK_NETHER_WOOD_HANGING_SIGN_HIT,
		SoundEvents.BLOCK_NETHER_WOOD_HANGING_SIGN_FALL
	);
	public static final BlockSoundGroup BAMBOO_WOOD_HANGING_SIGN = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_BAMBOO_WOOD_HANGING_SIGN_BREAK,
		SoundEvents.BLOCK_BAMBOO_WOOD_HANGING_SIGN_STEP,
		SoundEvents.BLOCK_BAMBOO_WOOD_HANGING_SIGN_PLACE,
		SoundEvents.BLOCK_BAMBOO_WOOD_HANGING_SIGN_HIT,
		SoundEvents.BLOCK_BAMBOO_WOOD_HANGING_SIGN_FALL
	);
	public static final BlockSoundGroup BAMBOO_WOOD = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_BAMBOO_WOOD_BREAK,
		SoundEvents.BLOCK_BAMBOO_WOOD_STEP,
		SoundEvents.BLOCK_BAMBOO_WOOD_PLACE,
		SoundEvents.BLOCK_BAMBOO_WOOD_HIT,
		SoundEvents.BLOCK_BAMBOO_WOOD_FALL
	);
	public static final BlockSoundGroup NETHER_WOOD = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_NETHER_WOOD_BREAK,
		SoundEvents.BLOCK_NETHER_WOOD_STEP,
		SoundEvents.BLOCK_NETHER_WOOD_PLACE,
		SoundEvents.BLOCK_NETHER_WOOD_HIT,
		SoundEvents.BLOCK_NETHER_WOOD_FALL
	);
	public static final BlockSoundGroup CHERRY_WOOD = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_CHERRY_WOOD_BREAK,
		SoundEvents.BLOCK_CHERRY_WOOD_STEP,
		SoundEvents.BLOCK_CHERRY_WOOD_PLACE,
		SoundEvents.BLOCK_CHERRY_WOOD_HIT,
		SoundEvents.BLOCK_CHERRY_WOOD_FALL
	);
	public static final BlockSoundGroup CHERRY_SAPLING = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_CHERRY_SAPLING_BREAK,
		SoundEvents.BLOCK_CHERRY_SAPLING_STEP,
		SoundEvents.BLOCK_CHERRY_SAPLING_PLACE,
		SoundEvents.BLOCK_CHERRY_SAPLING_HIT,
		SoundEvents.BLOCK_CHERRY_SAPLING_FALL
	);
	public static final BlockSoundGroup CHERRY_LEAVES = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_CHERRY_LEAVES_BREAK,
		SoundEvents.BLOCK_CHERRY_LEAVES_STEP,
		SoundEvents.BLOCK_CHERRY_LEAVES_PLACE,
		SoundEvents.BLOCK_CHERRY_LEAVES_HIT,
		SoundEvents.BLOCK_CHERRY_LEAVES_FALL
	);
	public static final BlockSoundGroup CHERRY_WOOD_HANGING_SIGN = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_CHERRY_WOOD_HANGING_SIGN_BREAK,
		SoundEvents.BLOCK_CHERRY_WOOD_HANGING_SIGN_STEP,
		SoundEvents.BLOCK_CHERRY_WOOD_HANGING_SIGN_PLACE,
		SoundEvents.BLOCK_CHERRY_WOOD_HANGING_SIGN_HIT,
		SoundEvents.BLOCK_CHERRY_WOOD_HANGING_SIGN_FALL
	);
	public static final BlockSoundGroup CHISELED_BOOKSHELF = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_CHISELED_BOOKSHELF_BREAK,
		SoundEvents.BLOCK_CHISELED_BOOKSHELF_STEP,
		SoundEvents.BLOCK_CHISELED_BOOKSHELF_PLACE,
		SoundEvents.BLOCK_CHISELED_BOOKSHELF_HIT,
		SoundEvents.BLOCK_CHISELED_BOOKSHELF_FALL
	);
	public static final BlockSoundGroup SUSPICIOUS_SAND = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_SUSPICIOUS_SAND_BREAK,
		SoundEvents.BLOCK_SUSPICIOUS_SAND_STEP,
		SoundEvents.BLOCK_SUSPICIOUS_SAND_PLACE,
		SoundEvents.BLOCK_SUSPICIOUS_SAND_HIT,
		SoundEvents.BLOCK_SUSPICIOUS_SAND_FALL
	);
	public static final BlockSoundGroup SUSPICIOUS_GRAVEL = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_SUSPICIOUS_GRAVEL_BREAK,
		SoundEvents.BLOCK_SUSPICIOUS_GRAVEL_STEP,
		SoundEvents.BLOCK_SUSPICIOUS_GRAVEL_PLACE,
		SoundEvents.BLOCK_SUSPICIOUS_GRAVEL_HIT,
		SoundEvents.BLOCK_SUSPICIOUS_GRAVEL_FALL
	);
	public static final BlockSoundGroup DECORATED_POT = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_DECORATED_POT_BREAK,
		SoundEvents.BLOCK_DECORATED_POT_STEP,
		SoundEvents.BLOCK_DECORATED_POT_PLACE,
		SoundEvents.BLOCK_DECORATED_POT_HIT,
		SoundEvents.BLOCK_DECORATED_POT_FALL
	);
	public static final BlockSoundGroup DECORATED_POT_SHATTER = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_DECORATED_POT_SHATTER,
		SoundEvents.BLOCK_DECORATED_POT_STEP,
		SoundEvents.BLOCK_DECORATED_POT_PLACE,
		SoundEvents.BLOCK_DECORATED_POT_HIT,
		SoundEvents.BLOCK_DECORATED_POT_FALL
	);
	public final float volume;
	public final float pitch;
	private final SoundEvent breakSound;
	private final SoundEvent stepSound;
	private final SoundEvent placeSound;
	private final SoundEvent hitSound;
	private final SoundEvent fallSound;

	public BlockSoundGroup(
		float volume, float pitch, SoundEvent breakSound, SoundEvent stepSound, SoundEvent placeSound, SoundEvent hitSound, SoundEvent fallSound
	) {
		this.volume = volume;
		this.pitch = pitch;
		this.breakSound = breakSound;
		this.stepSound = stepSound;
		this.placeSound = placeSound;
		this.hitSound = hitSound;
		this.fallSound = fallSound;
	}

	public float getVolume() {
		return this.volume;
	}

	public float getPitch() {
		return this.pitch;
	}

	public SoundEvent getBreakSound() {
		return this.breakSound;
	}

	public SoundEvent getStepSound() {
		return this.stepSound;
	}

	public SoundEvent getPlaceSound() {
		return this.placeSound;
	}

	public SoundEvent getHitSound() {
		return this.hitSound;
	}

	public SoundEvent getFallSound() {
		return this.fallSound;
	}
}

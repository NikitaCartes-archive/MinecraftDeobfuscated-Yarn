package net.minecraft.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class BlockSoundGroup {
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
		SoundEvents.BLOCK_GRASS_BREAK,
		SoundEvents.BLOCK_GRASS_STEP,
		SoundEvents.BLOCK_LILY_PAD_PLACE,
		SoundEvents.BLOCK_GRASS_HIT,
		SoundEvents.BLOCK_GRASS_FALL
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
		1.0F,
		1.0F,
		SoundEvents.BLOCK_GRASS_BREAK,
		SoundEvents.BLOCK_VINE_STEP,
		SoundEvents.BLOCK_GRASS_PLACE,
		SoundEvents.BLOCK_GRASS_HIT,
		SoundEvents.BLOCK_GRASS_FALL
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
	public static final BlockSoundGroup SCULK_SENSOR = new BlockSoundGroup(
		1.0F,
		1.0F,
		SoundEvents.BLOCK_SCULK_SENSOR_BREAK,
		SoundEvents.BLOCK_SCULK_SENSOR_STEP,
		SoundEvents.BLOCK_SCULK_SENSOR_PLACE,
		SoundEvents.BLOCK_SCULK_SENSOR_HIT,
		SoundEvents.BLOCK_SCULK_SENSOR_FALL
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

	@Environment(EnvType.CLIENT)
	public SoundEvent getBreakSound() {
		return this.breakSound;
	}

	public SoundEvent getStepSound() {
		return this.stepSound;
	}

	public SoundEvent getPlaceSound() {
		return this.placeSound;
	}

	@Environment(EnvType.CLIENT)
	public SoundEvent getHitSound() {
		return this.hitSound;
	}

	public SoundEvent getFallSound() {
		return this.fallSound;
	}
}

package net.minecraft.tag;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;

public final class BlockTags {
	protected static final RequiredTagList<Block> REQUIRED_TAGS = RequiredTagListRegistry.register(new Identifier("block"), TagManager::getBlocks);
	public static final Tag.Identified<Block> field_15481 = register("wool");
	public static final Tag.Identified<Block> field_15471 = register("planks");
	public static final Tag.Identified<Block> field_15465 = register("stone_bricks");
	public static final Tag.Identified<Block> field_15499 = register("wooden_buttons");
	public static final Tag.Identified<Block> field_15493 = register("buttons");
	public static final Tag.Identified<Block> field_15479 = register("carpets");
	public static final Tag.Identified<Block> field_15494 = register("wooden_doors");
	public static final Tag.Identified<Block> field_15502 = register("wooden_stairs");
	public static final Tag.Identified<Block> field_15468 = register("wooden_slabs");
	public static final Tag.Identified<Block> field_17619 = register("wooden_fences");
	public static final Tag.Identified<Block> field_24076 = register("pressure_plates");
	public static final Tag.Identified<Block> field_15477 = register("wooden_pressure_plates");
	public static final Tag.Identified<Block> field_24077 = register("stone_pressure_plates");
	public static final Tag.Identified<Block> field_15491 = register("wooden_trapdoors");
	public static final Tag.Identified<Block> field_15495 = register("doors");
	public static final Tag.Identified<Block> field_15462 = register("saplings");
	public static final Tag.Identified<Block> field_23210 = register("logs_that_burn");
	public static final Tag.Identified<Block> field_15475 = register("logs");
	public static final Tag.Identified<Block> field_15485 = register("dark_oak_logs");
	public static final Tag.Identified<Block> field_15482 = register("oak_logs");
	public static final Tag.Identified<Block> field_15498 = register("birch_logs");
	public static final Tag.Identified<Block> field_15458 = register("acacia_logs");
	public static final Tag.Identified<Block> field_15474 = register("jungle_logs");
	public static final Tag.Identified<Block> field_15489 = register("spruce_logs");
	public static final Tag.Identified<Block> field_21955 = register("crimson_stems");
	public static final Tag.Identified<Block> field_21956 = register("warped_stems");
	public static final Tag.Identified<Block> field_15501 = register("banners");
	public static final Tag.Identified<Block> field_15466 = register("sand");
	public static final Tag.Identified<Block> field_15459 = register("stairs");
	public static final Tag.Identified<Block> field_15469 = register("slabs");
	public static final Tag.Identified<Block> field_15504 = register("walls");
	public static final Tag.Identified<Block> field_15486 = register("anvil");
	public static final Tag.Identified<Block> field_15463 = register("rails");
	public static final Tag.Identified<Block> field_15503 = register("leaves");
	public static final Tag.Identified<Block> field_15487 = register("trapdoors");
	public static final Tag.Identified<Block> field_15480 = register("small_flowers");
	public static final Tag.Identified<Block> field_16443 = register("beds");
	public static final Tag.Identified<Block> field_16584 = register("fences");
	public static final Tag.Identified<Block> field_20338 = register("tall_flowers");
	public static final Tag.Identified<Block> field_20339 = register("flowers");
	public static final Tag.Identified<Block> field_22465 = register("piglin_repellents");
	public static final Tag.Identified<Block> field_23062 = register("gold_ores");
	public static final Tag.Identified<Block> field_23208 = register("non_flammable_wood");
	public static final Tag.Identified<Block> field_15470 = register("flower_pots");
	public static final Tag.Identified<Block> field_15460 = register("enderman_holdable");
	public static final Tag.Identified<Block> field_15467 = register("ice");
	public static final Tag.Identified<Block> field_15478 = register("valid_spawn");
	public static final Tag.Identified<Block> field_15490 = register("impermeable");
	public static final Tag.Identified<Block> field_15496 = register("underwater_bonemeals");
	public static final Tag.Identified<Block> field_15461 = register("coral_blocks");
	public static final Tag.Identified<Block> field_15476 = register("wall_corals");
	public static final Tag.Identified<Block> field_15483 = register("coral_plants");
	public static final Tag.Identified<Block> field_15488 = register("corals");
	public static final Tag.Identified<Block> field_15497 = register("bamboo_plantable_on");
	public static final Tag.Identified<Block> field_15472 = register("standing_signs");
	public static final Tag.Identified<Block> field_15492 = register("wall_signs");
	public static final Tag.Identified<Block> field_15500 = register("signs");
	public static final Tag.Identified<Block> field_17753 = register("dragon_immune");
	public static final Tag.Identified<Block> field_17754 = register("wither_immune");
	public static final Tag.Identified<Block> field_22274 = register("wither_summon_base_blocks");
	public static final Tag.Identified<Block> field_20340 = register("beehives");
	public static final Tag.Identified<Block> field_20341 = register("crops");
	public static final Tag.Identified<Block> field_20342 = register("bee_growables");
	public static final Tag.Identified<Block> field_21780 = register("portals");
	public static final Tag.Identified<Block> field_21952 = register("fire");
	public static final Tag.Identified<Block> field_21953 = register("nylium");
	public static final Tag.Identified<Block> field_21954 = register("wart_blocks");
	public static final Tag.Identified<Block> field_22275 = register("beacon_base_blocks");
	public static final Tag.Identified<Block> field_23063 = register("soul_speed_blocks");
	public static final Tag.Identified<Block> field_22276 = register("wall_post_override");
	public static final Tag.Identified<Block> field_22414 = register("climbable");
	public static final Tag.Identified<Block> field_21490 = register("shulker_boxes");
	public static final Tag.Identified<Block> field_22466 = register("hoglin_repellents");
	public static final Tag.Identified<Block> field_23119 = register("soul_fire_base_blocks");
	public static final Tag.Identified<Block> field_23209 = register("strider_warm_blocks");
	public static final Tag.Identified<Block> field_23799 = register("campfires");
	public static final Tag.Identified<Block> field_23800 = register("guarded_by_piglins");
	public static final Tag.Identified<Block> field_24459 = register("prevent_mob_spawning_inside");
	public static final Tag.Identified<Block> field_25147 = register("fence_gates");
	public static final Tag.Identified<Block> field_25148 = register("unstable_bottom_center");
	public static final Tag.Identified<Block> field_25739 = register("mushroom_grow_block");
	public static final Tag.Identified<Block> field_25588 = register("infiniburn_overworld");
	public static final Tag.Identified<Block> field_25589 = register("infiniburn_nether");
	public static final Tag.Identified<Block> field_25590 = register("infiniburn_end");
	public static final Tag.Identified<Block> field_25806 = register("base_stone_overworld");
	public static final Tag.Identified<Block> field_25807 = register("base_stone_nether");

	private static Tag.Identified<Block> register(String id) {
		return REQUIRED_TAGS.add(id);
	}

	public static TagGroup<Block> getTagGroup() {
		return REQUIRED_TAGS.getGroup();
	}

	public static List<? extends Tag.Identified<Block>> method_31072() {
		return REQUIRED_TAGS.getTags();
	}
}

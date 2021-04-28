package net.minecraft.data.family;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.registry.Registry;

public class BlockFamilies {
	private static final Map<Block, BlockFamily> BASE_BLOCKS_TO_FAMILIES = Maps.<Block, BlockFamily>newHashMap();
	/**
	 * The group used for the recipes of wooden block families.
	 */
	private static final String WOODEN_GROUP = "wooden";
	/**
	 * The name of the criterion used for the recipe unlock advancements of wooden block families.
	 */
	private static final String WOODEN_UNLOCK_CRITERION_NAME = "has_planks";
	public static final BlockFamily ACACIA = register(Blocks.ACACIA_PLANKS)
		.button(Blocks.ACACIA_BUTTON)
		.fence(Blocks.ACACIA_FENCE)
		.fenceGate(Blocks.ACACIA_FENCE_GATE)
		.pressurePlate(Blocks.ACACIA_PRESSURE_PLATE)
		.sign(Blocks.ACACIA_SIGN, Blocks.ACACIA_WALL_SIGN)
		.slab(Blocks.ACACIA_SLAB)
		.stairs(Blocks.ACACIA_STAIRS)
		.door(Blocks.ACACIA_DOOR)
		.trapdoor(Blocks.ACACIA_TRAPDOOR)
		.group("wooden")
		.unlockCriterionName("has_planks")
		.build();
	public static final BlockFamily BIRCH = register(Blocks.BIRCH_PLANKS)
		.button(Blocks.BIRCH_BUTTON)
		.fence(Blocks.BIRCH_FENCE)
		.fenceGate(Blocks.BIRCH_FENCE_GATE)
		.pressurePlate(Blocks.BIRCH_PRESSURE_PLATE)
		.sign(Blocks.BIRCH_SIGN, Blocks.BIRCH_WALL_SIGN)
		.slab(Blocks.BIRCH_SLAB)
		.stairs(Blocks.BIRCH_STAIRS)
		.door(Blocks.BIRCH_DOOR)
		.trapdoor(Blocks.BIRCH_TRAPDOOR)
		.group("wooden")
		.unlockCriterionName("has_planks")
		.build();
	public static final BlockFamily CRIMSON = register(Blocks.CRIMSON_PLANKS)
		.button(Blocks.CRIMSON_BUTTON)
		.fence(Blocks.CRIMSON_FENCE)
		.fenceGate(Blocks.CRIMSON_FENCE_GATE)
		.pressurePlate(Blocks.CRIMSON_PRESSURE_PLATE)
		.sign(Blocks.CRIMSON_SIGN, Blocks.CRIMSON_WALL_SIGN)
		.slab(Blocks.CRIMSON_SLAB)
		.stairs(Blocks.CRIMSON_STAIRS)
		.door(Blocks.CRIMSON_DOOR)
		.trapdoor(Blocks.CRIMSON_TRAPDOOR)
		.group("wooden")
		.unlockCriterionName("has_planks")
		.build();
	public static final BlockFamily JUNGLE = register(Blocks.JUNGLE_PLANKS)
		.button(Blocks.JUNGLE_BUTTON)
		.fence(Blocks.JUNGLE_FENCE)
		.fenceGate(Blocks.JUNGLE_FENCE_GATE)
		.pressurePlate(Blocks.JUNGLE_PRESSURE_PLATE)
		.sign(Blocks.JUNGLE_SIGN, Blocks.JUNGLE_WALL_SIGN)
		.slab(Blocks.JUNGLE_SLAB)
		.stairs(Blocks.JUNGLE_STAIRS)
		.door(Blocks.JUNGLE_DOOR)
		.trapdoor(Blocks.JUNGLE_TRAPDOOR)
		.group("wooden")
		.unlockCriterionName("has_planks")
		.build();
	public static final BlockFamily OAK = register(Blocks.OAK_PLANKS)
		.button(Blocks.OAK_BUTTON)
		.fence(Blocks.OAK_FENCE)
		.fenceGate(Blocks.OAK_FENCE_GATE)
		.pressurePlate(Blocks.OAK_PRESSURE_PLATE)
		.sign(Blocks.OAK_SIGN, Blocks.OAK_WALL_SIGN)
		.slab(Blocks.OAK_SLAB)
		.stairs(Blocks.OAK_STAIRS)
		.door(Blocks.OAK_DOOR)
		.trapdoor(Blocks.OAK_TRAPDOOR)
		.group("wooden")
		.unlockCriterionName("has_planks")
		.build();
	public static final BlockFamily DARK_OAK = register(Blocks.DARK_OAK_PLANKS)
		.button(Blocks.DARK_OAK_BUTTON)
		.fence(Blocks.DARK_OAK_FENCE)
		.fenceGate(Blocks.DARK_OAK_FENCE_GATE)
		.pressurePlate(Blocks.DARK_OAK_PRESSURE_PLATE)
		.sign(Blocks.DARK_OAK_SIGN, Blocks.DARK_OAK_WALL_SIGN)
		.slab(Blocks.DARK_OAK_SLAB)
		.stairs(Blocks.DARK_OAK_STAIRS)
		.door(Blocks.DARK_OAK_DOOR)
		.trapdoor(Blocks.DARK_OAK_TRAPDOOR)
		.group("wooden")
		.unlockCriterionName("has_planks")
		.build();
	public static final BlockFamily SPRUCE = register(Blocks.SPRUCE_PLANKS)
		.button(Blocks.SPRUCE_BUTTON)
		.fence(Blocks.SPRUCE_FENCE)
		.fenceGate(Blocks.SPRUCE_FENCE_GATE)
		.pressurePlate(Blocks.SPRUCE_PRESSURE_PLATE)
		.sign(Blocks.SPRUCE_SIGN, Blocks.SPRUCE_WALL_SIGN)
		.slab(Blocks.SPRUCE_SLAB)
		.stairs(Blocks.SPRUCE_STAIRS)
		.door(Blocks.SPRUCE_DOOR)
		.trapdoor(Blocks.SPRUCE_TRAPDOOR)
		.group("wooden")
		.unlockCriterionName("has_planks")
		.build();
	public static final BlockFamily WARPED = register(Blocks.WARPED_PLANKS)
		.button(Blocks.WARPED_BUTTON)
		.fence(Blocks.WARPED_FENCE)
		.fenceGate(Blocks.WARPED_FENCE_GATE)
		.pressurePlate(Blocks.WARPED_PRESSURE_PLATE)
		.sign(Blocks.WARPED_SIGN, Blocks.WARPED_WALL_SIGN)
		.slab(Blocks.WARPED_SLAB)
		.stairs(Blocks.WARPED_STAIRS)
		.door(Blocks.WARPED_DOOR)
		.trapdoor(Blocks.WARPED_TRAPDOOR)
		.group("wooden")
		.unlockCriterionName("has_planks")
		.build();
	public static final BlockFamily ANDESITE = register(Blocks.ANDESITE)
		.wall(Blocks.ANDESITE_WALL)
		.stairs(Blocks.ANDESITE_STAIRS)
		.slab(Blocks.ANDESITE_SLAB)
		.polished(Blocks.POLISHED_ANDESITE)
		.build();
	public static final BlockFamily POLISHED_ANDESITE = register(Blocks.POLISHED_ANDESITE)
		.stairs(Blocks.POLISHED_ANDESITE_STAIRS)
		.slab(Blocks.POLISHED_ANDESITE_SLAB)
		.build();
	public static final BlockFamily BLACKSTONE = register(Blocks.BLACKSTONE)
		.wall(Blocks.BLACKSTONE_WALL)
		.stairs(Blocks.BLACKSTONE_STAIRS)
		.slab(Blocks.BLACKSTONE_SLAB)
		.polished(Blocks.POLISHED_BLACKSTONE)
		.build();
	public static final BlockFamily POLISHED_BLACKSTONE = register(Blocks.POLISHED_BLACKSTONE)
		.wall(Blocks.POLISHED_BLACKSTONE_WALL)
		.pressurePlate(Blocks.POLISHED_BLACKSTONE_PRESSURE_PLATE)
		.button(Blocks.POLISHED_BLACKSTONE_BUTTON)
		.stairs(Blocks.POLISHED_BLACKSTONE_STAIRS)
		.slab(Blocks.POLISHED_BLACKSTONE_SLAB)
		.polished(Blocks.POLISHED_BLACKSTONE_BRICKS)
		.chiseled(Blocks.CHISELED_POLISHED_BLACKSTONE)
		.build();
	public static final BlockFamily POLISHED_BLACKSTONE_BRICK = register(Blocks.POLISHED_BLACKSTONE_BRICKS)
		.wall(Blocks.POLISHED_BLACKSTONE_BRICK_WALL)
		.stairs(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS)
		.slab(Blocks.POLISHED_BLACKSTONE_BRICK_SLAB)
		.cracked(Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS)
		.build();
	public static final BlockFamily BRICK = register(Blocks.BRICKS).wall(Blocks.BRICK_WALL).stairs(Blocks.BRICK_STAIRS).slab(Blocks.BRICK_SLAB).build();
	public static final BlockFamily END_STONE_BRICK = register(Blocks.END_STONE_BRICKS)
		.wall(Blocks.END_STONE_BRICK_WALL)
		.stairs(Blocks.END_STONE_BRICK_STAIRS)
		.slab(Blocks.END_STONE_BRICK_SLAB)
		.build();
	public static final BlockFamily MOSSY_STONE_BRICK = register(Blocks.MOSSY_STONE_BRICKS)
		.wall(Blocks.MOSSY_STONE_BRICK_WALL)
		.stairs(Blocks.MOSSY_STONE_BRICK_STAIRS)
		.slab(Blocks.MOSSY_STONE_BRICK_SLAB)
		.build();
	public static final BlockFamily field_33684 = register(Blocks.COPPER_BLOCK).method_36544(Blocks.CUT_COPPER).noGenerateModels().build();
	public static final BlockFamily CUT_COPPER = register(Blocks.CUT_COPPER)
		.slab(Blocks.CUT_COPPER_SLAB)
		.stairs(Blocks.CUT_COPPER_STAIRS)
		.noGenerateModels()
		.build();
	public static final BlockFamily field_33685 = register(Blocks.WAXED_COPPER_BLOCK)
		.method_36544(Blocks.WAXED_CUT_COPPER)
		.group("waxed_cut_copper")
		.noGenerateModels()
		.build();
	public static final BlockFamily WAXED_CUT_COPPER = register(Blocks.WAXED_CUT_COPPER)
		.slab(Blocks.WAXED_CUT_COPPER_SLAB)
		.stairs(Blocks.WAXED_CUT_COPPER_STAIRS)
		.group("waxed_cut_copper")
		.noGenerateModels()
		.build();
	public static final BlockFamily field_33686 = register(Blocks.EXPOSED_COPPER).method_36544(Blocks.EXPOSED_CUT_COPPER).noGenerateModels().build();
	public static final BlockFamily EXPOSED_CUT_COPPER = register(Blocks.EXPOSED_CUT_COPPER)
		.slab(Blocks.EXPOSED_CUT_COPPER_SLAB)
		.stairs(Blocks.EXPOSED_CUT_COPPER_STAIRS)
		.noGenerateModels()
		.build();
	public static final BlockFamily field_33687 = register(Blocks.WAXED_EXPOSED_COPPER)
		.method_36544(Blocks.WAXED_EXPOSED_CUT_COPPER)
		.group("waxed_exposed_cut_copper")
		.noGenerateModels()
		.build();
	public static final BlockFamily WAXED_EXPOSED_CUT_COPPER = register(Blocks.WAXED_EXPOSED_CUT_COPPER)
		.slab(Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB)
		.stairs(Blocks.WAXED_EXPOSED_CUT_COPPER_STAIRS)
		.group("waxed_exposed_cut_copper")
		.noGenerateModels()
		.build();
	public static final BlockFamily field_33688 = register(Blocks.WEATHERED_COPPER).method_36544(Blocks.WEATHERED_CUT_COPPER).noGenerateModels().build();
	public static final BlockFamily WEATHERED_CUT_COPPER = register(Blocks.WEATHERED_CUT_COPPER)
		.slab(Blocks.WEATHERED_CUT_COPPER_SLAB)
		.stairs(Blocks.WEATHERED_CUT_COPPER_STAIRS)
		.noGenerateModels()
		.build();
	public static final BlockFamily field_33681 = register(Blocks.WAXED_WEATHERED_COPPER)
		.method_36544(Blocks.WAXED_WEATHERED_CUT_COPPER)
		.group("waxed_weathered_cut_copper")
		.noGenerateModels()
		.build();
	public static final BlockFamily WAXED_WEATHERED_CUT_COPPER = register(Blocks.WAXED_WEATHERED_CUT_COPPER)
		.slab(Blocks.WAXED_WEATHERED_CUT_COPPER_SLAB)
		.stairs(Blocks.WAXED_WEATHERED_CUT_COPPER_STAIRS)
		.group("waxed_weathered_cut_copper")
		.noGenerateModels()
		.build();
	public static final BlockFamily field_33682 = register(Blocks.OXIDIZED_COPPER).method_36544(Blocks.OXIDIZED_CUT_COPPER).noGenerateModels().build();
	public static final BlockFamily OXIDIZED_CUT_COPPER = register(Blocks.OXIDIZED_CUT_COPPER)
		.slab(Blocks.OXIDIZED_CUT_COPPER_SLAB)
		.stairs(Blocks.OXIDIZED_CUT_COPPER_STAIRS)
		.noGenerateModels()
		.build();
	public static final BlockFamily field_33683 = register(Blocks.WAXED_OXIDIZED_COPPER)
		.method_36544(Blocks.WAXED_OXIDIZED_CUT_COPPER)
		.group("waxed_oxidized_cut_copper")
		.noGenerateModels()
		.build();
	public static final BlockFamily WAXED_OXIDIZED_CUT_COPPER = register(Blocks.WAXED_OXIDIZED_CUT_COPPER)
		.slab(Blocks.WAXED_OXIDIZED_CUT_COPPER_SLAB)
		.stairs(Blocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS)
		.group("waxed_oxidized_cut_copper")
		.noGenerateModels()
		.build();
	public static final BlockFamily COBBLESTONE = register(Blocks.COBBLESTONE)
		.wall(Blocks.COBBLESTONE_WALL)
		.stairs(Blocks.COBBLESTONE_STAIRS)
		.slab(Blocks.COBBLESTONE_SLAB)
		.build();
	public static final BlockFamily MOSSY_COBBLESTONE = register(Blocks.MOSSY_COBBLESTONE)
		.wall(Blocks.MOSSY_COBBLESTONE_WALL)
		.stairs(Blocks.MOSSY_COBBLESTONE_STAIRS)
		.slab(Blocks.MOSSY_COBBLESTONE_SLAB)
		.build();
	public static final BlockFamily DIORITE = register(Blocks.DIORITE)
		.wall(Blocks.DIORITE_WALL)
		.stairs(Blocks.DIORITE_STAIRS)
		.slab(Blocks.DIORITE_SLAB)
		.polished(Blocks.POLISHED_DIORITE)
		.build();
	public static final BlockFamily POLISHED_DIORITE = register(Blocks.POLISHED_DIORITE)
		.stairs(Blocks.POLISHED_DIORITE_STAIRS)
		.slab(Blocks.POLISHED_DIORITE_SLAB)
		.build();
	public static final BlockFamily GRANITE = register(Blocks.GRANITE)
		.wall(Blocks.GRANITE_WALL)
		.stairs(Blocks.GRANITE_STAIRS)
		.slab(Blocks.GRANITE_SLAB)
		.polished(Blocks.POLISHED_GRANITE)
		.build();
	public static final BlockFamily POLISHED_GRANITE = register(Blocks.POLISHED_GRANITE)
		.stairs(Blocks.POLISHED_GRANITE_STAIRS)
		.slab(Blocks.POLISHED_GRANITE_SLAB)
		.build();
	public static final BlockFamily NETHER_BRICK = register(Blocks.NETHER_BRICKS)
		.fence(Blocks.NETHER_BRICK_FENCE)
		.wall(Blocks.NETHER_BRICK_WALL)
		.stairs(Blocks.NETHER_BRICK_STAIRS)
		.slab(Blocks.NETHER_BRICK_SLAB)
		.chiseled(Blocks.CHISELED_NETHER_BRICKS)
		.cracked(Blocks.CRACKED_NETHER_BRICKS)
		.build();
	public static final BlockFamily RED_NETHER_BRICK = register(Blocks.RED_NETHER_BRICKS)
		.slab(Blocks.RED_NETHER_BRICK_SLAB)
		.stairs(Blocks.RED_NETHER_BRICK_STAIRS)
		.wall(Blocks.RED_NETHER_BRICK_WALL)
		.build();
	public static final BlockFamily PRISMARINE = register(Blocks.PRISMARINE)
		.wall(Blocks.PRISMARINE_WALL)
		.stairs(Blocks.PRISMARINE_STAIRS)
		.slab(Blocks.PRISMARINE_SLAB)
		.build();
	public static final BlockFamily PURPUR = register(Blocks.PURPUR_BLOCK).stairs(Blocks.PURPUR_STAIRS).slab(Blocks.PURPUR_SLAB).noGenerateRecipes().build();
	public static final BlockFamily PRISMARINE_BRICK = register(Blocks.PRISMARINE_BRICKS)
		.stairs(Blocks.PRISMARINE_BRICK_STAIRS)
		.slab(Blocks.PRISMARINE_BRICK_SLAB)
		.build();
	public static final BlockFamily DARK_PRISMARINE = register(Blocks.DARK_PRISMARINE)
		.stairs(Blocks.DARK_PRISMARINE_STAIRS)
		.slab(Blocks.DARK_PRISMARINE_SLAB)
		.build();
	public static final BlockFamily QUARTZ_BLOCK = register(Blocks.QUARTZ_BLOCK)
		.stairs(Blocks.QUARTZ_STAIRS)
		.slab(Blocks.QUARTZ_SLAB)
		.chiseled(Blocks.CHISELED_QUARTZ_BLOCK)
		.noGenerateRecipes()
		.build();
	public static final BlockFamily SMOOTH_QUARTZ = register(Blocks.SMOOTH_QUARTZ).stairs(Blocks.SMOOTH_QUARTZ_STAIRS).slab(Blocks.SMOOTH_QUARTZ_SLAB).build();
	public static final BlockFamily SANDSTONE = register(Blocks.SANDSTONE)
		.wall(Blocks.SANDSTONE_WALL)
		.stairs(Blocks.SANDSTONE_STAIRS)
		.slab(Blocks.SANDSTONE_SLAB)
		.chiseled(Blocks.CHISELED_SANDSTONE)
		.method_36544(Blocks.CUT_SANDSTONE)
		.noGenerateRecipes()
		.build();
	public static final BlockFamily CUT_SANDSTONE = register(Blocks.CUT_SANDSTONE).slab(Blocks.CUT_SANDSTONE_SLAB).build();
	public static final BlockFamily SMOOTH_SANDSTONE = register(Blocks.SMOOTH_SANDSTONE)
		.slab(Blocks.SMOOTH_SANDSTONE_SLAB)
		.stairs(Blocks.SMOOTH_SANDSTONE_STAIRS)
		.build();
	public static final BlockFamily RED_SANDSTONE = register(Blocks.RED_SANDSTONE)
		.wall(Blocks.RED_SANDSTONE_WALL)
		.stairs(Blocks.RED_SANDSTONE_STAIRS)
		.slab(Blocks.RED_SANDSTONE_SLAB)
		.chiseled(Blocks.CHISELED_RED_SANDSTONE)
		.method_36544(Blocks.CUT_RED_SANDSTONE)
		.noGenerateRecipes()
		.build();
	public static final BlockFamily CUT_RED_SANDSTONE = register(Blocks.CUT_RED_SANDSTONE).slab(Blocks.CUT_RED_SANDSTONE_SLAB).build();
	public static final BlockFamily SMOOTH_RED_SANDSTONE = register(Blocks.SMOOTH_RED_SANDSTONE)
		.slab(Blocks.SMOOTH_RED_SANDSTONE_SLAB)
		.stairs(Blocks.SMOOTH_RED_SANDSTONE_STAIRS)
		.build();
	public static final BlockFamily STONE = register(Blocks.STONE)
		.slab(Blocks.STONE_SLAB)
		.pressurePlate(Blocks.STONE_PRESSURE_PLATE)
		.button(Blocks.STONE_BUTTON)
		.stairs(Blocks.STONE_STAIRS)
		.build();
	public static final BlockFamily STONE_BRICK = register(Blocks.STONE_BRICKS)
		.wall(Blocks.STONE_BRICK_WALL)
		.stairs(Blocks.STONE_BRICK_STAIRS)
		.slab(Blocks.STONE_BRICK_SLAB)
		.chiseled(Blocks.CHISELED_STONE_BRICKS)
		.cracked(Blocks.CRACKED_STONE_BRICKS)
		.noGenerateRecipes()
		.build();
	public static final BlockFamily DEEPSLATE = register(Blocks.DEEPSLATE).build();
	public static final BlockFamily COBBLED_DEEPSLATE = register(Blocks.COBBLED_DEEPSLATE)
		.slab(Blocks.COBBLED_DEEPSLATE_SLAB)
		.stairs(Blocks.COBBLED_DEEPSLATE_STAIRS)
		.wall(Blocks.COBBLED_DEEPSLATE_WALL)
		.chiseled(Blocks.CHISELED_DEEPSLATE)
		.polished(Blocks.POLISHED_DEEPSLATE)
		.build();
	public static final BlockFamily POLISHED_DEEPSLATE = register(Blocks.POLISHED_DEEPSLATE)
		.slab(Blocks.POLISHED_DEEPSLATE_SLAB)
		.stairs(Blocks.POLISHED_DEEPSLATE_STAIRS)
		.wall(Blocks.POLISHED_DEEPSLATE_WALL)
		.build();
	public static final BlockFamily DEEPSLATE_BRICK = register(Blocks.DEEPSLATE_BRICKS)
		.slab(Blocks.DEEPSLATE_BRICK_SLAB)
		.stairs(Blocks.DEEPSLATE_BRICK_STAIRS)
		.wall(Blocks.DEEPSLATE_BRICK_WALL)
		.cracked(Blocks.CRACKED_DEEPSLATE_BRICKS)
		.build();
	public static final BlockFamily DEEPSLATE_TILE = register(Blocks.DEEPSLATE_TILES)
		.slab(Blocks.DEEPSLATE_TILE_SLAB)
		.stairs(Blocks.DEEPSLATE_TILE_STAIRS)
		.wall(Blocks.DEEPSLATE_TILE_WALL)
		.cracked(Blocks.CRACKED_DEEPSLATE_TILES)
		.build();

	private static BlockFamily.Builder register(Block baseBlock) {
		BlockFamily.Builder builder = new BlockFamily.Builder(baseBlock);
		BlockFamily blockFamily = (BlockFamily)BASE_BLOCKS_TO_FAMILIES.put(baseBlock, builder.build());
		if (blockFamily != null) {
			throw new IllegalStateException("Duplicate family definition for " + Registry.BLOCK.getId(baseBlock));
		} else {
			return builder;
		}
	}

	public static Stream<BlockFamily> getFamilies() {
		return BASE_BLOCKS_TO_FAMILIES.values().stream();
	}
}

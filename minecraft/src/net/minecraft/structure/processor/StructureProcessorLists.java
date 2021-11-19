package net.minecraft.structure.processor;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.PaneBlock;
import net.minecraft.structure.rule.AlwaysTrueRuleTest;
import net.minecraft.structure.rule.AxisAlignedLinearPosRuleTest;
import net.minecraft.structure.rule.BlockMatchRuleTest;
import net.minecraft.structure.rule.BlockStateMatchRuleTest;
import net.minecraft.structure.rule.RandomBlockMatchRuleTest;
import net.minecraft.structure.rule.TagMatchRuleTest;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.BuiltinRegistries;

public class StructureProcessorLists {
	private static final StructureProcessorRule BLACKSTONE_TO_GILDED_BLACKSTONE = new StructureProcessorRule(
		new RandomBlockMatchRuleTest(Blocks.BLACKSTONE, 0.01F), AlwaysTrueRuleTest.INSTANCE, Blocks.GILDED_BLACKSTONE.getDefaultState()
	);
	private static final StructureProcessorRule GILDED_BLACKSTONE_TO_BLACKSTONE = new StructureProcessorRule(
		new RandomBlockMatchRuleTest(Blocks.GILDED_BLACKSTONE, 0.5F), AlwaysTrueRuleTest.INSTANCE, Blocks.BLACKSTONE.getDefaultState()
	);
	public static final StructureProcessorList EMPTY = register("empty", ImmutableList.of());
	public static final StructureProcessorList ZOMBIE_PLAINS = register(
		"zombie_plains",
		ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.COBBLESTONE, 0.8F), AlwaysTrueRuleTest.INSTANCE, Blocks.MOSSY_COBBLESTONE.getDefaultState()),
					new StructureProcessorRule(new TagMatchRuleTest(BlockTags.DOORS), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()),
					new StructureProcessorRule(new BlockMatchRuleTest(Blocks.TORCH), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()),
					new StructureProcessorRule(new BlockMatchRuleTest(Blocks.WALL_TORCH), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.COBBLESTONE, 0.07F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.MOSSY_COBBLESTONE, 0.07F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.WHITE_TERRACOTTA, 0.07F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.OAK_LOG, 0.05F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.OAK_PLANKS, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.OAK_STAIRS, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.STRIPPED_OAK_LOG, 0.02F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.GLASS_PANE, 0.5F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()),
					new StructureProcessorRule(
						new BlockStateMatchRuleTest(Blocks.GLASS_PANE.getDefaultState().with(PaneBlock.NORTH, Boolean.valueOf(true)).with(PaneBlock.SOUTH, Boolean.valueOf(true))),
						AlwaysTrueRuleTest.INSTANCE,
						Blocks.BROWN_STAINED_GLASS_PANE.getDefaultState().with(PaneBlock.NORTH, Boolean.valueOf(true)).with(PaneBlock.SOUTH, Boolean.valueOf(true))
					),
					new StructureProcessorRule(
						new BlockStateMatchRuleTest(Blocks.GLASS_PANE.getDefaultState().with(PaneBlock.EAST, Boolean.valueOf(true)).with(PaneBlock.WEST, Boolean.valueOf(true))),
						AlwaysTrueRuleTest.INSTANCE,
						Blocks.BROWN_STAINED_GLASS_PANE.getDefaultState().with(PaneBlock.EAST, Boolean.valueOf(true)).with(PaneBlock.WEST, Boolean.valueOf(true))
					),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.3F), AlwaysTrueRuleTest.INSTANCE, Blocks.CARROTS.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.2F), AlwaysTrueRuleTest.INSTANCE, Blocks.POTATOES.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.BEETROOTS.getDefaultState())
				)
			)
		)
	);
	public static final StructureProcessorList ZOMBIE_SAVANNA = register(
		"zombie_savanna",
		ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(new TagMatchRuleTest(BlockTags.DOORS), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()),
					new StructureProcessorRule(new BlockMatchRuleTest(Blocks.TORCH), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()),
					new StructureProcessorRule(new BlockMatchRuleTest(Blocks.WALL_TORCH), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.ACACIA_PLANKS, 0.2F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.ACACIA_STAIRS, 0.2F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.ACACIA_LOG, 0.05F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.ACACIA_WOOD, 0.05F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.ORANGE_TERRACOTTA, 0.05F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.YELLOW_TERRACOTTA, 0.05F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.RED_TERRACOTTA, 0.05F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.GLASS_PANE, 0.5F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()),
					new StructureProcessorRule(
						new BlockStateMatchRuleTest(Blocks.GLASS_PANE.getDefaultState().with(PaneBlock.NORTH, Boolean.valueOf(true)).with(PaneBlock.SOUTH, Boolean.valueOf(true))),
						AlwaysTrueRuleTest.INSTANCE,
						Blocks.BROWN_STAINED_GLASS_PANE.getDefaultState().with(PaneBlock.NORTH, Boolean.valueOf(true)).with(PaneBlock.SOUTH, Boolean.valueOf(true))
					),
					new StructureProcessorRule(
						new BlockStateMatchRuleTest(Blocks.GLASS_PANE.getDefaultState().with(PaneBlock.EAST, Boolean.valueOf(true)).with(PaneBlock.WEST, Boolean.valueOf(true))),
						AlwaysTrueRuleTest.INSTANCE,
						Blocks.BROWN_STAINED_GLASS_PANE.getDefaultState().with(PaneBlock.EAST, Boolean.valueOf(true)).with(PaneBlock.WEST, Boolean.valueOf(true))
					),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.MELON_STEM.getDefaultState())
				)
			)
		)
	);
	public static final StructureProcessorList ZOMBIE_SNOWY = register(
		"zombie_snowy",
		ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(new TagMatchRuleTest(BlockTags.DOORS), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()),
					new StructureProcessorRule(new BlockMatchRuleTest(Blocks.TORCH), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()),
					new StructureProcessorRule(new BlockMatchRuleTest(Blocks.WALL_TORCH), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()),
					new StructureProcessorRule(new BlockMatchRuleTest(Blocks.LANTERN), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.SPRUCE_PLANKS, 0.2F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.SPRUCE_SLAB, 0.4F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.STRIPPED_SPRUCE_LOG, 0.05F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.STRIPPED_SPRUCE_WOOD, 0.05F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.GLASS_PANE, 0.5F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()),
					new StructureProcessorRule(
						new BlockStateMatchRuleTest(Blocks.GLASS_PANE.getDefaultState().with(PaneBlock.NORTH, Boolean.valueOf(true)).with(PaneBlock.SOUTH, Boolean.valueOf(true))),
						AlwaysTrueRuleTest.INSTANCE,
						Blocks.BROWN_STAINED_GLASS_PANE.getDefaultState().with(PaneBlock.NORTH, Boolean.valueOf(true)).with(PaneBlock.SOUTH, Boolean.valueOf(true))
					),
					new StructureProcessorRule(
						new BlockStateMatchRuleTest(Blocks.GLASS_PANE.getDefaultState().with(PaneBlock.EAST, Boolean.valueOf(true)).with(PaneBlock.WEST, Boolean.valueOf(true))),
						AlwaysTrueRuleTest.INSTANCE,
						Blocks.BROWN_STAINED_GLASS_PANE.getDefaultState().with(PaneBlock.EAST, Boolean.valueOf(true)).with(PaneBlock.WEST, Boolean.valueOf(true))
					),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.CARROTS.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.8F), AlwaysTrueRuleTest.INSTANCE, Blocks.POTATOES.getDefaultState())
				)
			)
		)
	);
	public static final StructureProcessorList ZOMBIE_TAIGA = register(
		"zombie_taiga",
		ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.COBBLESTONE, 0.8F), AlwaysTrueRuleTest.INSTANCE, Blocks.MOSSY_COBBLESTONE.getDefaultState()),
					new StructureProcessorRule(new TagMatchRuleTest(BlockTags.DOORS), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()),
					new StructureProcessorRule(new BlockMatchRuleTest(Blocks.TORCH), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()),
					new StructureProcessorRule(new BlockMatchRuleTest(Blocks.WALL_TORCH), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()),
					new StructureProcessorRule(
						new BlockMatchRuleTest(Blocks.CAMPFIRE), AlwaysTrueRuleTest.INSTANCE, Blocks.CAMPFIRE.getDefaultState().with(CampfireBlock.LIT, Boolean.valueOf(false))
					),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.COBBLESTONE, 0.08F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.SPRUCE_LOG, 0.08F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.GLASS_PANE, 0.5F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()),
					new StructureProcessorRule(
						new BlockStateMatchRuleTest(Blocks.GLASS_PANE.getDefaultState().with(PaneBlock.NORTH, Boolean.valueOf(true)).with(PaneBlock.SOUTH, Boolean.valueOf(true))),
						AlwaysTrueRuleTest.INSTANCE,
						Blocks.BROWN_STAINED_GLASS_PANE.getDefaultState().with(PaneBlock.NORTH, Boolean.valueOf(true)).with(PaneBlock.SOUTH, Boolean.valueOf(true))
					),
					new StructureProcessorRule(
						new BlockStateMatchRuleTest(Blocks.GLASS_PANE.getDefaultState().with(PaneBlock.EAST, Boolean.valueOf(true)).with(PaneBlock.WEST, Boolean.valueOf(true))),
						AlwaysTrueRuleTest.INSTANCE,
						Blocks.BROWN_STAINED_GLASS_PANE.getDefaultState().with(PaneBlock.EAST, Boolean.valueOf(true)).with(PaneBlock.WEST, Boolean.valueOf(true))
					),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.3F), AlwaysTrueRuleTest.INSTANCE, Blocks.PUMPKIN_STEM.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.2F), AlwaysTrueRuleTest.INSTANCE, Blocks.POTATOES.getDefaultState())
				)
			)
		)
	);
	public static final StructureProcessorList ZOMBIE_DESERT = register(
		"zombie_desert",
		ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(new TagMatchRuleTest(BlockTags.DOORS), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()),
					new StructureProcessorRule(new BlockMatchRuleTest(Blocks.TORCH), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()),
					new StructureProcessorRule(new BlockMatchRuleTest(Blocks.WALL_TORCH), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.SMOOTH_SANDSTONE, 0.08F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.CUT_SANDSTONE, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.TERRACOTTA, 0.08F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()),
					new StructureProcessorRule(
						new RandomBlockMatchRuleTest(Blocks.SMOOTH_SANDSTONE_STAIRS, 0.08F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()
					),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.SMOOTH_SANDSTONE_SLAB, 0.08F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.2F), AlwaysTrueRuleTest.INSTANCE, Blocks.BEETROOTS.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.MELON_STEM.getDefaultState())
				)
			)
		)
	);
	public static final StructureProcessorList MOSSIFY_10_PERCENT = register(
		"mossify_10_percent",
		ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.COBBLESTONE, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.MOSSY_COBBLESTONE.getDefaultState())
				)
			)
		)
	);
	public static final StructureProcessorList MOSSIFY_20_PERCENT = register(
		"mossify_20_percent",
		ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.COBBLESTONE, 0.2F), AlwaysTrueRuleTest.INSTANCE, Blocks.MOSSY_COBBLESTONE.getDefaultState())
				)
			)
		)
	);
	public static final StructureProcessorList MOSSIFY_70_PERCENT = register(
		"mossify_70_percent",
		ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.COBBLESTONE, 0.7F), AlwaysTrueRuleTest.INSTANCE, Blocks.MOSSY_COBBLESTONE.getDefaultState())
				)
			)
		)
	);
	public static final StructureProcessorList STREET_PLAINS = register(
		"street_plains",
		ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(new BlockMatchRuleTest(Blocks.DIRT_PATH), new BlockMatchRuleTest(Blocks.WATER), Blocks.OAK_PLANKS.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.DIRT_PATH, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.GRASS_BLOCK.getDefaultState()),
					new StructureProcessorRule(new BlockMatchRuleTest(Blocks.GRASS_BLOCK), new BlockMatchRuleTest(Blocks.WATER), Blocks.WATER.getDefaultState()),
					new StructureProcessorRule(new BlockMatchRuleTest(Blocks.DIRT), new BlockMatchRuleTest(Blocks.WATER), Blocks.WATER.getDefaultState())
				)
			)
		)
	);
	public static final StructureProcessorList STREET_SAVANNA = register(
		"street_savanna",
		ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(new BlockMatchRuleTest(Blocks.DIRT_PATH), new BlockMatchRuleTest(Blocks.WATER), Blocks.ACACIA_PLANKS.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.DIRT_PATH, 0.2F), AlwaysTrueRuleTest.INSTANCE, Blocks.GRASS_BLOCK.getDefaultState()),
					new StructureProcessorRule(new BlockMatchRuleTest(Blocks.GRASS_BLOCK), new BlockMatchRuleTest(Blocks.WATER), Blocks.WATER.getDefaultState()),
					new StructureProcessorRule(new BlockMatchRuleTest(Blocks.DIRT), new BlockMatchRuleTest(Blocks.WATER), Blocks.WATER.getDefaultState())
				)
			)
		)
	);
	public static final StructureProcessorList STREET_SNOWY_OR_TAIGA = register(
		"street_snowy_or_taiga",
		ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(new BlockMatchRuleTest(Blocks.DIRT_PATH), new BlockMatchRuleTest(Blocks.WATER), Blocks.SPRUCE_PLANKS.getDefaultState()),
					new StructureProcessorRule(new BlockMatchRuleTest(Blocks.DIRT_PATH), new BlockMatchRuleTest(Blocks.ICE), Blocks.SPRUCE_PLANKS.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.DIRT_PATH, 0.2F), AlwaysTrueRuleTest.INSTANCE, Blocks.GRASS_BLOCK.getDefaultState()),
					new StructureProcessorRule(new BlockMatchRuleTest(Blocks.GRASS_BLOCK), new BlockMatchRuleTest(Blocks.WATER), Blocks.WATER.getDefaultState()),
					new StructureProcessorRule(new BlockMatchRuleTest(Blocks.DIRT), new BlockMatchRuleTest(Blocks.WATER), Blocks.WATER.getDefaultState())
				)
			)
		)
	);
	public static final StructureProcessorList FARM_PLAINS = register(
		"farm_plains",
		ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.3F), AlwaysTrueRuleTest.INSTANCE, Blocks.CARROTS.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.2F), AlwaysTrueRuleTest.INSTANCE, Blocks.POTATOES.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.BEETROOTS.getDefaultState())
				)
			)
		)
	);
	public static final StructureProcessorList FARM_SAVANNA = register(
		"farm_savanna",
		ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.MELON_STEM.getDefaultState())
				)
			)
		)
	);
	public static final StructureProcessorList FARM_SNOWY = register(
		"farm_snowy",
		ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.CARROTS.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.8F), AlwaysTrueRuleTest.INSTANCE, Blocks.POTATOES.getDefaultState())
				)
			)
		)
	);
	public static final StructureProcessorList FARM_TAIGA = register(
		"farm_taiga",
		ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.3F), AlwaysTrueRuleTest.INSTANCE, Blocks.PUMPKIN_STEM.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.2F), AlwaysTrueRuleTest.INSTANCE, Blocks.POTATOES.getDefaultState())
				)
			)
		)
	);
	public static final StructureProcessorList FARM_DESERT = register(
		"farm_desert",
		ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.2F), AlwaysTrueRuleTest.INSTANCE, Blocks.BEETROOTS.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.MELON_STEM.getDefaultState())
				)
			)
		)
	);
	public static final StructureProcessorList OUTPOST_ROT = register("outpost_rot", ImmutableList.of(new BlockRotStructureProcessor(0.05F)));
	public static final StructureProcessorList BOTTOM_RAMPART = register(
		"bottom_rampart",
		ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(
						new RandomBlockMatchRuleTest(Blocks.MAGMA_BLOCK, 0.75F), AlwaysTrueRuleTest.INSTANCE, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.getDefaultState()
					),
					new StructureProcessorRule(
						new RandomBlockMatchRuleTest(Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS, 0.15F),
						AlwaysTrueRuleTest.INSTANCE,
						Blocks.POLISHED_BLACKSTONE_BRICKS.getDefaultState()
					),
					GILDED_BLACKSTONE_TO_BLACKSTONE,
					BLACKSTONE_TO_GILDED_BLACKSTONE
				)
			)
		)
	);
	public static final StructureProcessorList TREASURE_ROOMS = register(
		"treasure_rooms",
		ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(
						new RandomBlockMatchRuleTest(Blocks.POLISHED_BLACKSTONE_BRICKS, 0.35F),
						AlwaysTrueRuleTest.INSTANCE,
						Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.getDefaultState()
					),
					new StructureProcessorRule(
						new RandomBlockMatchRuleTest(Blocks.CHISELED_POLISHED_BLACKSTONE, 0.1F),
						AlwaysTrueRuleTest.INSTANCE,
						Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.getDefaultState()
					),
					GILDED_BLACKSTONE_TO_BLACKSTONE,
					BLACKSTONE_TO_GILDED_BLACKSTONE
				)
			)
		)
	);
	public static final StructureProcessorList HOUSING = register(
		"housing",
		ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(
						new RandomBlockMatchRuleTest(Blocks.POLISHED_BLACKSTONE_BRICKS, 0.3F),
						AlwaysTrueRuleTest.INSTANCE,
						Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.getDefaultState()
					),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.BLACKSTONE, 1.0E-4F), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()),
					GILDED_BLACKSTONE_TO_BLACKSTONE,
					BLACKSTONE_TO_GILDED_BLACKSTONE
				)
			)
		)
	);
	public static final StructureProcessorList SIDE_WALL_DEGRADATION = register(
		"side_wall_degradation",
		ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(
						new RandomBlockMatchRuleTest(Blocks.CHISELED_POLISHED_BLACKSTONE, 0.5F), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()
					),
					new StructureProcessorRule(
						new RandomBlockMatchRuleTest(Blocks.GOLD_BLOCK, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.getDefaultState()
					),
					GILDED_BLACKSTONE_TO_BLACKSTONE,
					BLACKSTONE_TO_GILDED_BLACKSTONE
				)
			)
		)
	);
	public static final StructureProcessorList STABLE_DEGRADATION = register(
		"stable_degradation",
		ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(
						new RandomBlockMatchRuleTest(Blocks.POLISHED_BLACKSTONE_BRICKS, 0.1F),
						AlwaysTrueRuleTest.INSTANCE,
						Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.getDefaultState()
					),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.BLACKSTONE, 1.0E-4F), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()),
					GILDED_BLACKSTONE_TO_BLACKSTONE,
					BLACKSTONE_TO_GILDED_BLACKSTONE
				)
			)
		)
	);
	public static final StructureProcessorList BASTION_GENERIC_DEGRADATION = register(
		"bastion_generic_degradation",
		ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(
						new RandomBlockMatchRuleTest(Blocks.POLISHED_BLACKSTONE_BRICKS, 0.3F),
						AlwaysTrueRuleTest.INSTANCE,
						Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.getDefaultState()
					),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.BLACKSTONE, 1.0E-4F), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()),
					new StructureProcessorRule(
						new RandomBlockMatchRuleTest(Blocks.GOLD_BLOCK, 0.3F), AlwaysTrueRuleTest.INSTANCE, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.getDefaultState()
					),
					GILDED_BLACKSTONE_TO_BLACKSTONE,
					BLACKSTONE_TO_GILDED_BLACKSTONE
				)
			)
		)
	);
	public static final StructureProcessorList RAMPART_DEGRADATION = register(
		"rampart_degradation",
		ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(
						new RandomBlockMatchRuleTest(Blocks.POLISHED_BLACKSTONE_BRICKS, 0.4F),
						AlwaysTrueRuleTest.INSTANCE,
						Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.getDefaultState()
					),
					new StructureProcessorRule(
						new RandomBlockMatchRuleTest(Blocks.BLACKSTONE, 0.01F), AlwaysTrueRuleTest.INSTANCE, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.getDefaultState()
					),
					new StructureProcessorRule(
						new RandomBlockMatchRuleTest(Blocks.POLISHED_BLACKSTONE_BRICKS, 1.0E-4F), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()
					),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.BLACKSTONE, 1.0E-4F), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()),
					new StructureProcessorRule(
						new RandomBlockMatchRuleTest(Blocks.GOLD_BLOCK, 0.3F), AlwaysTrueRuleTest.INSTANCE, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.getDefaultState()
					),
					GILDED_BLACKSTONE_TO_BLACKSTONE,
					BLACKSTONE_TO_GILDED_BLACKSTONE
				)
			)
		)
	);
	public static final StructureProcessorList ENTRANCE_REPLACEMENT = register(
		"entrance_replacement",
		ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(
						new RandomBlockMatchRuleTest(Blocks.CHISELED_POLISHED_BLACKSTONE, 0.5F), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()
					),
					new StructureProcessorRule(
						new RandomBlockMatchRuleTest(Blocks.GOLD_BLOCK, 0.6F), AlwaysTrueRuleTest.INSTANCE, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.getDefaultState()
					),
					GILDED_BLACKSTONE_TO_BLACKSTONE,
					BLACKSTONE_TO_GILDED_BLACKSTONE
				)
			)
		)
	);
	public static final StructureProcessorList BRIDGE = register(
		"bridge",
		ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(
						new RandomBlockMatchRuleTest(Blocks.POLISHED_BLACKSTONE_BRICKS, 0.3F),
						AlwaysTrueRuleTest.INSTANCE,
						Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.getDefaultState()
					),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.BLACKSTONE, 1.0E-4F), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState())
				)
			)
		)
	);
	public static final StructureProcessorList ROOF = register(
		"roof",
		ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(
						new RandomBlockMatchRuleTest(Blocks.POLISHED_BLACKSTONE_BRICKS, 0.3F),
						AlwaysTrueRuleTest.INSTANCE,
						Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.getDefaultState()
					),
					new StructureProcessorRule(
						new RandomBlockMatchRuleTest(Blocks.POLISHED_BLACKSTONE_BRICKS, 0.15F), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()
					),
					new StructureProcessorRule(
						new RandomBlockMatchRuleTest(Blocks.POLISHED_BLACKSTONE_BRICKS, 0.3F), AlwaysTrueRuleTest.INSTANCE, Blocks.BLACKSTONE.getDefaultState()
					)
				)
			)
		)
	);
	public static final StructureProcessorList HIGH_WALL = register(
		"high_wall",
		ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(
						new RandomBlockMatchRuleTest(Blocks.POLISHED_BLACKSTONE_BRICKS, 0.01F), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()
					),
					new StructureProcessorRule(
						new RandomBlockMatchRuleTest(Blocks.POLISHED_BLACKSTONE_BRICKS, 0.5F),
						AlwaysTrueRuleTest.INSTANCE,
						Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.getDefaultState()
					),
					new StructureProcessorRule(
						new RandomBlockMatchRuleTest(Blocks.POLISHED_BLACKSTONE_BRICKS, 0.3F), AlwaysTrueRuleTest.INSTANCE, Blocks.BLACKSTONE.getDefaultState()
					),
					GILDED_BLACKSTONE_TO_BLACKSTONE
				)
			)
		)
	);
	public static final StructureProcessorList HIGH_RAMPART = register(
		"high_rampart",
		ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(
						new RandomBlockMatchRuleTest(Blocks.GOLD_BLOCK, 0.3F), AlwaysTrueRuleTest.INSTANCE, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.getDefaultState()
					),
					new StructureProcessorRule(
						AlwaysTrueRuleTest.INSTANCE,
						AlwaysTrueRuleTest.INSTANCE,
						new AxisAlignedLinearPosRuleTest(0.0F, 0.05F, 0, 100, Direction.Axis.Y),
						Blocks.AIR.getDefaultState()
					),
					GILDED_BLACKSTONE_TO_BLACKSTONE
				)
			)
		)
	);
	public static final StructureProcessorList FOSSIL_ROT = register(
		"fossil_rot", ImmutableList.of(new BlockRotStructureProcessor(0.9F), new ProtectedBlocksStructureProcessor(BlockTags.FEATURES_CANNOT_REPLACE.getId()))
	);
	public static final StructureProcessorList FOSSIL_COAL = register(
		"fossil_coal", ImmutableList.of(new BlockRotStructureProcessor(0.1F), new ProtectedBlocksStructureProcessor(BlockTags.FEATURES_CANNOT_REPLACE.getId()))
	);
	public static final StructureProcessorList FOSSIL_DIAMONDS = register(
		"fossil_diamonds",
		ImmutableList.of(
			new BlockRotStructureProcessor(0.1F),
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(new BlockMatchRuleTest(Blocks.COAL_ORE), AlwaysTrueRuleTest.INSTANCE, Blocks.DEEPSLATE_DIAMOND_ORE.getDefaultState())
				)
			),
			new ProtectedBlocksStructureProcessor(BlockTags.FEATURES_CANNOT_REPLACE.getId())
		)
	);

	private static StructureProcessorList register(String id, ImmutableList<StructureProcessor> processorList) {
		Identifier identifier = new Identifier(id);
		StructureProcessorList structureProcessorList = new StructureProcessorList(processorList);
		return BuiltinRegistries.add(BuiltinRegistries.STRUCTURE_PROCESSOR_LIST, identifier, structureProcessorList);
	}
}

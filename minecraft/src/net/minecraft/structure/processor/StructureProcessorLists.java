package net.minecraft.structure.processor;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.PaneBlock;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.structure.rule.AlwaysTrueRuleTest;
import net.minecraft.structure.rule.AxisAlignedLinearPosRuleTest;
import net.minecraft.structure.rule.BlockMatchRuleTest;
import net.minecraft.structure.rule.BlockStateMatchRuleTest;
import net.minecraft.structure.rule.RandomBlockMatchRuleTest;
import net.minecraft.structure.rule.TagMatchRuleTest;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class StructureProcessorLists {
	private static final RegistryKey<StructureProcessorList> EMPTY = of("empty");
	public static final RegistryKey<StructureProcessorList> ZOMBIE_PLAINS = of("zombie_plains");
	public static final RegistryKey<StructureProcessorList> ZOMBIE_SAVANNA = of("zombie_savanna");
	public static final RegistryKey<StructureProcessorList> ZOMBIE_SNOWY = of("zombie_snowy");
	public static final RegistryKey<StructureProcessorList> ZOMBIE_TAIGA = of("zombie_taiga");
	public static final RegistryKey<StructureProcessorList> ZOMBIE_DESERT = of("zombie_desert");
	public static final RegistryKey<StructureProcessorList> MOSSIFY_10_PERCENT = of("mossify_10_percent");
	public static final RegistryKey<StructureProcessorList> MOSSIFY_20_PERCENT = of("mossify_20_percent");
	public static final RegistryKey<StructureProcessorList> MOSSIFY_70_PERCENT = of("mossify_70_percent");
	public static final RegistryKey<StructureProcessorList> STREET_PLAINS = of("street_plains");
	public static final RegistryKey<StructureProcessorList> STREET_SAVANNA = of("street_savanna");
	public static final RegistryKey<StructureProcessorList> STREET_SNOWY_OR_TAIGA = of("street_snowy_or_taiga");
	public static final RegistryKey<StructureProcessorList> FARM_PLAINS = of("farm_plains");
	public static final RegistryKey<StructureProcessorList> FARM_SAVANNA = of("farm_savanna");
	public static final RegistryKey<StructureProcessorList> FARM_SNOWY = of("farm_snowy");
	public static final RegistryKey<StructureProcessorList> FARM_TAIGA = of("farm_taiga");
	public static final RegistryKey<StructureProcessorList> FARM_DESERT = of("farm_desert");
	public static final RegistryKey<StructureProcessorList> OUTPOST_ROT = of("outpost_rot");
	public static final RegistryKey<StructureProcessorList> BOTTOM_RAMPART = of("bottom_rampart");
	public static final RegistryKey<StructureProcessorList> TREASURE_ROOMS = of("treasure_rooms");
	public static final RegistryKey<StructureProcessorList> HOUSING = of("housing");
	public static final RegistryKey<StructureProcessorList> SIDE_WALL_DEGRADATION = of("side_wall_degradation");
	public static final RegistryKey<StructureProcessorList> STABLE_DEGRADATION = of("stable_degradation");
	public static final RegistryKey<StructureProcessorList> BASTION_GENERIC_DEGRADATION = of("bastion_generic_degradation");
	public static final RegistryKey<StructureProcessorList> RAMPART_DEGRADATION = of("rampart_degradation");
	public static final RegistryKey<StructureProcessorList> ENTRANCE_REPLACEMENT = of("entrance_replacement");
	public static final RegistryKey<StructureProcessorList> BRIDGE = of("bridge");
	public static final RegistryKey<StructureProcessorList> ROOF = of("roof");
	public static final RegistryKey<StructureProcessorList> HIGH_WALL = of("high_wall");
	public static final RegistryKey<StructureProcessorList> HIGH_RAMPART = of("high_rampart");
	public static final RegistryKey<StructureProcessorList> FOSSIL_ROT = of("fossil_rot");
	public static final RegistryKey<StructureProcessorList> FOSSIL_COAL = of("fossil_coal");
	public static final RegistryKey<StructureProcessorList> FOSSIL_DIAMONDS = of("fossil_diamonds");
	public static final RegistryKey<StructureProcessorList> ANCIENT_CITY_START_DEGRADATION = of("ancient_city_start_degradation");
	public static final RegistryKey<StructureProcessorList> ANCIENT_CITY_GENERIC_DEGRADATION = of("ancient_city_generic_degradation");
	public static final RegistryKey<StructureProcessorList> ANCIENT_CITY_WALLS_DEGRADATION = of("ancient_city_walls_degradation");

	private static RegistryKey<StructureProcessorList> of(String id) {
		return RegistryKey.of(RegistryKeys.PROCESSOR_LIST_WORLDGEN, new Identifier(id));
	}

	private static void register(
		Registerable<StructureProcessorList> processorListRegisterable, RegistryKey<StructureProcessorList> key, List<StructureProcessor> processors
	) {
		processorListRegisterable.register(key, new StructureProcessorList(processors));
	}

	public static void bootstrap(Registerable<StructureProcessorList> processorListRegisterable) {
		RegistryEntryLookup<Block> registryEntryLookup = processorListRegisterable.getRegistryLookup(RegistryKeys.BLOCK);
		StructureProcessorRule structureProcessorRule = new StructureProcessorRule(
			new RandomBlockMatchRuleTest(Blocks.BLACKSTONE, 0.01F), AlwaysTrueRuleTest.INSTANCE, Blocks.GILDED_BLACKSTONE.getDefaultState()
		);
		StructureProcessorRule structureProcessorRule2 = new StructureProcessorRule(
			new RandomBlockMatchRuleTest(Blocks.GILDED_BLACKSTONE, 0.5F), AlwaysTrueRuleTest.INSTANCE, Blocks.BLACKSTONE.getDefaultState()
		);
		register(processorListRegisterable, EMPTY, ImmutableList.of());
		register(
			processorListRegisterable,
			ZOMBIE_PLAINS,
			ImmutableList.of(
				new RuleStructureProcessor(
					ImmutableList.of(
						new StructureProcessorRule(
							new RandomBlockMatchRuleTest(Blocks.COBBLESTONE, 0.8F), AlwaysTrueRuleTest.INSTANCE, Blocks.MOSSY_COBBLESTONE.getDefaultState()
						),
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
							new BlockStateMatchRuleTest(
								Blocks.GLASS_PANE.getDefaultState().with(PaneBlock.NORTH, Boolean.valueOf(true)).with(PaneBlock.SOUTH, Boolean.valueOf(true))
							),
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
		register(
			processorListRegisterable,
			ZOMBIE_SAVANNA,
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
							new BlockStateMatchRuleTest(
								Blocks.GLASS_PANE.getDefaultState().with(PaneBlock.NORTH, Boolean.valueOf(true)).with(PaneBlock.SOUTH, Boolean.valueOf(true))
							),
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
		register(
			processorListRegisterable,
			ZOMBIE_SNOWY,
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
							new BlockStateMatchRuleTest(
								Blocks.GLASS_PANE.getDefaultState().with(PaneBlock.NORTH, Boolean.valueOf(true)).with(PaneBlock.SOUTH, Boolean.valueOf(true))
							),
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
		register(
			processorListRegisterable,
			ZOMBIE_TAIGA,
			ImmutableList.of(
				new RuleStructureProcessor(
					ImmutableList.of(
						new StructureProcessorRule(
							new RandomBlockMatchRuleTest(Blocks.COBBLESTONE, 0.8F), AlwaysTrueRuleTest.INSTANCE, Blocks.MOSSY_COBBLESTONE.getDefaultState()
						),
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
							new BlockStateMatchRuleTest(
								Blocks.GLASS_PANE.getDefaultState().with(PaneBlock.NORTH, Boolean.valueOf(true)).with(PaneBlock.SOUTH, Boolean.valueOf(true))
							),
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
		register(
			processorListRegisterable,
			ZOMBIE_DESERT,
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
						new StructureProcessorRule(
							new RandomBlockMatchRuleTest(Blocks.SMOOTH_SANDSTONE_SLAB, 0.08F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()
						),
						new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.2F), AlwaysTrueRuleTest.INSTANCE, Blocks.BEETROOTS.getDefaultState()),
						new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.MELON_STEM.getDefaultState())
					)
				)
			)
		);
		register(
			processorListRegisterable,
			MOSSIFY_10_PERCENT,
			ImmutableList.of(
				new RuleStructureProcessor(
					ImmutableList.of(
						new StructureProcessorRule(
							new RandomBlockMatchRuleTest(Blocks.COBBLESTONE, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.MOSSY_COBBLESTONE.getDefaultState()
						)
					)
				)
			)
		);
		register(
			processorListRegisterable,
			MOSSIFY_20_PERCENT,
			ImmutableList.of(
				new RuleStructureProcessor(
					ImmutableList.of(
						new StructureProcessorRule(
							new RandomBlockMatchRuleTest(Blocks.COBBLESTONE, 0.2F), AlwaysTrueRuleTest.INSTANCE, Blocks.MOSSY_COBBLESTONE.getDefaultState()
						)
					)
				)
			)
		);
		register(
			processorListRegisterable,
			MOSSIFY_70_PERCENT,
			ImmutableList.of(
				new RuleStructureProcessor(
					ImmutableList.of(
						new StructureProcessorRule(
							new RandomBlockMatchRuleTest(Blocks.COBBLESTONE, 0.7F), AlwaysTrueRuleTest.INSTANCE, Blocks.MOSSY_COBBLESTONE.getDefaultState()
						)
					)
				)
			)
		);
		register(
			processorListRegisterable,
			STREET_PLAINS,
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
		register(
			processorListRegisterable,
			STREET_SAVANNA,
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
		register(
			processorListRegisterable,
			STREET_SNOWY_OR_TAIGA,
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
		register(
			processorListRegisterable,
			FARM_PLAINS,
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
		register(
			processorListRegisterable,
			FARM_SAVANNA,
			ImmutableList.of(
				new RuleStructureProcessor(
					ImmutableList.of(
						new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.MELON_STEM.getDefaultState())
					)
				)
			)
		);
		register(
			processorListRegisterable,
			FARM_SNOWY,
			ImmutableList.of(
				new RuleStructureProcessor(
					ImmutableList.of(
						new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.CARROTS.getDefaultState()),
						new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.8F), AlwaysTrueRuleTest.INSTANCE, Blocks.POTATOES.getDefaultState())
					)
				)
			)
		);
		register(
			processorListRegisterable,
			FARM_TAIGA,
			ImmutableList.of(
				new RuleStructureProcessor(
					ImmutableList.of(
						new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.3F), AlwaysTrueRuleTest.INSTANCE, Blocks.PUMPKIN_STEM.getDefaultState()),
						new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.2F), AlwaysTrueRuleTest.INSTANCE, Blocks.POTATOES.getDefaultState())
					)
				)
			)
		);
		register(
			processorListRegisterable,
			FARM_DESERT,
			ImmutableList.of(
				new RuleStructureProcessor(
					ImmutableList.of(
						new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.2F), AlwaysTrueRuleTest.INSTANCE, Blocks.BEETROOTS.getDefaultState()),
						new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.MELON_STEM.getDefaultState())
					)
				)
			)
		);
		register(processorListRegisterable, OUTPOST_ROT, ImmutableList.of(new BlockRotStructureProcessor(0.05F)));
		register(
			processorListRegisterable,
			BOTTOM_RAMPART,
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
						structureProcessorRule2,
						structureProcessorRule
					)
				)
			)
		);
		register(
			processorListRegisterable,
			TREASURE_ROOMS,
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
						structureProcessorRule2,
						structureProcessorRule
					)
				)
			)
		);
		register(
			processorListRegisterable,
			HOUSING,
			ImmutableList.of(
				new RuleStructureProcessor(
					ImmutableList.of(
						new StructureProcessorRule(
							new RandomBlockMatchRuleTest(Blocks.POLISHED_BLACKSTONE_BRICKS, 0.3F),
							AlwaysTrueRuleTest.INSTANCE,
							Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.getDefaultState()
						),
						new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.BLACKSTONE, 1.0E-4F), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()),
						structureProcessorRule2,
						structureProcessorRule
					)
				)
			)
		);
		register(
			processorListRegisterable,
			SIDE_WALL_DEGRADATION,
			ImmutableList.of(
				new RuleStructureProcessor(
					ImmutableList.of(
						new StructureProcessorRule(
							new RandomBlockMatchRuleTest(Blocks.CHISELED_POLISHED_BLACKSTONE, 0.5F), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()
						),
						new StructureProcessorRule(
							new RandomBlockMatchRuleTest(Blocks.GOLD_BLOCK, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.getDefaultState()
						),
						structureProcessorRule2,
						structureProcessorRule
					)
				)
			)
		);
		register(
			processorListRegisterable,
			STABLE_DEGRADATION,
			ImmutableList.of(
				new RuleStructureProcessor(
					ImmutableList.of(
						new StructureProcessorRule(
							new RandomBlockMatchRuleTest(Blocks.POLISHED_BLACKSTONE_BRICKS, 0.1F),
							AlwaysTrueRuleTest.INSTANCE,
							Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.getDefaultState()
						),
						new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.BLACKSTONE, 1.0E-4F), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()),
						structureProcessorRule2,
						structureProcessorRule
					)
				)
			)
		);
		register(
			processorListRegisterable,
			BASTION_GENERIC_DEGRADATION,
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
						structureProcessorRule2,
						structureProcessorRule
					)
				)
			)
		);
		register(
			processorListRegisterable,
			RAMPART_DEGRADATION,
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
						structureProcessorRule2,
						structureProcessorRule
					)
				)
			)
		);
		register(
			processorListRegisterable,
			ENTRANCE_REPLACEMENT,
			ImmutableList.of(
				new RuleStructureProcessor(
					ImmutableList.of(
						new StructureProcessorRule(
							new RandomBlockMatchRuleTest(Blocks.CHISELED_POLISHED_BLACKSTONE, 0.5F), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()
						),
						new StructureProcessorRule(
							new RandomBlockMatchRuleTest(Blocks.GOLD_BLOCK, 0.6F), AlwaysTrueRuleTest.INSTANCE, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.getDefaultState()
						),
						structureProcessorRule2,
						structureProcessorRule
					)
				)
			)
		);
		register(
			processorListRegisterable,
			BRIDGE,
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
		register(
			processorListRegisterable,
			ROOF,
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
		register(
			processorListRegisterable,
			HIGH_WALL,
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
						structureProcessorRule2
					)
				)
			)
		);
		register(
			processorListRegisterable,
			HIGH_RAMPART,
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
						structureProcessorRule2
					)
				)
			)
		);
		register(
			processorListRegisterable,
			FOSSIL_ROT,
			ImmutableList.of(new BlockRotStructureProcessor(0.9F), new ProtectedBlocksStructureProcessor(BlockTags.FEATURES_CANNOT_REPLACE))
		);
		register(
			processorListRegisterable,
			FOSSIL_COAL,
			ImmutableList.of(new BlockRotStructureProcessor(0.1F), new ProtectedBlocksStructureProcessor(BlockTags.FEATURES_CANNOT_REPLACE))
		);
		register(
			processorListRegisterable,
			FOSSIL_DIAMONDS,
			ImmutableList.of(
				new BlockRotStructureProcessor(0.1F),
				new RuleStructureProcessor(
					ImmutableList.of(
						new StructureProcessorRule(new BlockMatchRuleTest(Blocks.COAL_ORE), AlwaysTrueRuleTest.INSTANCE, Blocks.DEEPSLATE_DIAMOND_ORE.getDefaultState())
					)
				),
				new ProtectedBlocksStructureProcessor(BlockTags.FEATURES_CANNOT_REPLACE)
			)
		);
		register(
			processorListRegisterable,
			ANCIENT_CITY_START_DEGRADATION,
			ImmutableList.of(
				new RuleStructureProcessor(
					ImmutableList.of(
						new StructureProcessorRule(
							new RandomBlockMatchRuleTest(Blocks.DEEPSLATE_BRICKS, 0.3F), AlwaysTrueRuleTest.INSTANCE, Blocks.CRACKED_DEEPSLATE_BRICKS.getDefaultState()
						),
						new StructureProcessorRule(
							new RandomBlockMatchRuleTest(Blocks.DEEPSLATE_TILES, 0.3F), AlwaysTrueRuleTest.INSTANCE, Blocks.CRACKED_DEEPSLATE_TILES.getDefaultState()
						),
						new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.SOUL_LANTERN, 0.05F), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState())
					)
				),
				new ProtectedBlocksStructureProcessor(BlockTags.FEATURES_CANNOT_REPLACE)
			)
		);
		register(
			processorListRegisterable,
			ANCIENT_CITY_GENERIC_DEGRADATION,
			ImmutableList.of(
				new BlockRotStructureProcessor(registryEntryLookup.getOrThrow(BlockTags.ANCIENT_CITY_REPLACEABLE), 0.95F),
				new RuleStructureProcessor(
					ImmutableList.of(
						new StructureProcessorRule(
							new RandomBlockMatchRuleTest(Blocks.DEEPSLATE_BRICKS, 0.3F), AlwaysTrueRuleTest.INSTANCE, Blocks.CRACKED_DEEPSLATE_BRICKS.getDefaultState()
						),
						new StructureProcessorRule(
							new RandomBlockMatchRuleTest(Blocks.DEEPSLATE_TILES, 0.3F), AlwaysTrueRuleTest.INSTANCE, Blocks.CRACKED_DEEPSLATE_TILES.getDefaultState()
						),
						new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.SOUL_LANTERN, 0.05F), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState())
					)
				),
				new ProtectedBlocksStructureProcessor(BlockTags.FEATURES_CANNOT_REPLACE)
			)
		);
		register(
			processorListRegisterable,
			ANCIENT_CITY_WALLS_DEGRADATION,
			ImmutableList.of(
				new BlockRotStructureProcessor(registryEntryLookup.getOrThrow(BlockTags.ANCIENT_CITY_REPLACEABLE), 0.95F),
				new RuleStructureProcessor(
					ImmutableList.of(
						new StructureProcessorRule(
							new RandomBlockMatchRuleTest(Blocks.DEEPSLATE_BRICKS, 0.3F), AlwaysTrueRuleTest.INSTANCE, Blocks.CRACKED_DEEPSLATE_BRICKS.getDefaultState()
						),
						new StructureProcessorRule(
							new RandomBlockMatchRuleTest(Blocks.DEEPSLATE_TILES, 0.3F), AlwaysTrueRuleTest.INSTANCE, Blocks.CRACKED_DEEPSLATE_TILES.getDefaultState()
						),
						new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.DEEPSLATE_TILE_SLAB, 0.3F), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()),
						new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.SOUL_LANTERN, 0.05F), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState())
					)
				),
				new ProtectedBlocksStructureProcessor(BlockTags.FEATURES_CANNOT_REPLACE)
			)
		);
	}
}

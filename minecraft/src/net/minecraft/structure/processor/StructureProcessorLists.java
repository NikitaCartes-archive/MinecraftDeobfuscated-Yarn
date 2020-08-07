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
	private static final StructureProcessorRule field_26621 = new StructureProcessorRule(
		new RandomBlockMatchRuleTest(Blocks.field_23869, 0.01F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_23880.getDefaultState()
	);
	private static final StructureProcessorRule field_26622 = new StructureProcessorRule(
		new RandomBlockMatchRuleTest(Blocks.field_23880, 0.5F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_23869.getDefaultState()
	);
	public static final StructureProcessorList field_26688 = register("empty", ImmutableList.of());
	public static final StructureProcessorList field_26259 = register(
		"zombie_plains",
		ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10445, 0.8F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_9989.getDefaultState()),
					new StructureProcessorRule(new TagMatchRuleTest(BlockTags.field_15495), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10124.getDefaultState()),
					new StructureProcessorRule(new BlockMatchRuleTest(Blocks.field_10336), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10124.getDefaultState()),
					new StructureProcessorRule(new BlockMatchRuleTest(Blocks.field_10099), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10124.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10445, 0.07F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10343.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_9989, 0.07F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10343.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10611, 0.07F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10343.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10431, 0.05F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10343.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10161, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10343.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10563, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10343.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10519, 0.02F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10343.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10285, 0.5F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10343.getDefaultState()),
					new StructureProcessorRule(
						new BlockStateMatchRuleTest(
							Blocks.field_10285.getDefaultState().with(PaneBlock.NORTH, Boolean.valueOf(true)).with(PaneBlock.SOUTH, Boolean.valueOf(true))
						),
						AlwaysTrueRuleTest.INSTANCE,
						Blocks.field_10163.getDefaultState().with(PaneBlock.NORTH, Boolean.valueOf(true)).with(PaneBlock.SOUTH, Boolean.valueOf(true))
					),
					new StructureProcessorRule(
						new BlockStateMatchRuleTest(Blocks.field_10285.getDefaultState().with(PaneBlock.EAST, Boolean.valueOf(true)).with(PaneBlock.WEST, Boolean.valueOf(true))),
						AlwaysTrueRuleTest.INSTANCE,
						Blocks.field_10163.getDefaultState().with(PaneBlock.EAST, Boolean.valueOf(true)).with(PaneBlock.WEST, Boolean.valueOf(true))
					),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10293, 0.3F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10609.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10293, 0.2F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10247.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10293, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10341.getDefaultState())
				)
			)
		)
	);
	public static final StructureProcessorList field_26260 = register(
		"zombie_savanna",
		ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(new TagMatchRuleTest(BlockTags.field_15495), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10124.getDefaultState()),
					new StructureProcessorRule(new BlockMatchRuleTest(Blocks.field_10336), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10124.getDefaultState()),
					new StructureProcessorRule(new BlockMatchRuleTest(Blocks.field_10099), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10124.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10218, 0.2F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10343.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10256, 0.2F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10343.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10533, 0.05F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10343.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_9999, 0.05F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10343.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10184, 0.05F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10343.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10143, 0.05F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10343.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10328, 0.05F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10343.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10285, 0.5F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10343.getDefaultState()),
					new StructureProcessorRule(
						new BlockStateMatchRuleTest(
							Blocks.field_10285.getDefaultState().with(PaneBlock.NORTH, Boolean.valueOf(true)).with(PaneBlock.SOUTH, Boolean.valueOf(true))
						),
						AlwaysTrueRuleTest.INSTANCE,
						Blocks.field_10163.getDefaultState().with(PaneBlock.NORTH, Boolean.valueOf(true)).with(PaneBlock.SOUTH, Boolean.valueOf(true))
					),
					new StructureProcessorRule(
						new BlockStateMatchRuleTest(Blocks.field_10285.getDefaultState().with(PaneBlock.EAST, Boolean.valueOf(true)).with(PaneBlock.WEST, Boolean.valueOf(true))),
						AlwaysTrueRuleTest.INSTANCE,
						Blocks.field_10163.getDefaultState().with(PaneBlock.EAST, Boolean.valueOf(true)).with(PaneBlock.WEST, Boolean.valueOf(true))
					),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10293, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10168.getDefaultState())
				)
			)
		)
	);
	public static final StructureProcessorList field_26261 = register(
		"zombie_snowy",
		ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(new TagMatchRuleTest(BlockTags.field_15495), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10124.getDefaultState()),
					new StructureProcessorRule(new BlockMatchRuleTest(Blocks.field_10336), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10124.getDefaultState()),
					new StructureProcessorRule(new BlockMatchRuleTest(Blocks.field_10099), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10124.getDefaultState()),
					new StructureProcessorRule(new BlockMatchRuleTest(Blocks.field_16541), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10124.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_9975, 0.2F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10343.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10071, 0.4F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10343.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10436, 0.05F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10343.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10558, 0.05F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10343.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10285, 0.5F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10343.getDefaultState()),
					new StructureProcessorRule(
						new BlockStateMatchRuleTest(
							Blocks.field_10285.getDefaultState().with(PaneBlock.NORTH, Boolean.valueOf(true)).with(PaneBlock.SOUTH, Boolean.valueOf(true))
						),
						AlwaysTrueRuleTest.INSTANCE,
						Blocks.field_10163.getDefaultState().with(PaneBlock.NORTH, Boolean.valueOf(true)).with(PaneBlock.SOUTH, Boolean.valueOf(true))
					),
					new StructureProcessorRule(
						new BlockStateMatchRuleTest(Blocks.field_10285.getDefaultState().with(PaneBlock.EAST, Boolean.valueOf(true)).with(PaneBlock.WEST, Boolean.valueOf(true))),
						AlwaysTrueRuleTest.INSTANCE,
						Blocks.field_10163.getDefaultState().with(PaneBlock.EAST, Boolean.valueOf(true)).with(PaneBlock.WEST, Boolean.valueOf(true))
					),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10293, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10609.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10293, 0.8F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10247.getDefaultState())
				)
			)
		)
	);
	public static final StructureProcessorList field_26262 = register(
		"zombie_taiga",
		ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10445, 0.8F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_9989.getDefaultState()),
					new StructureProcessorRule(new TagMatchRuleTest(BlockTags.field_15495), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10124.getDefaultState()),
					new StructureProcessorRule(new BlockMatchRuleTest(Blocks.field_10336), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10124.getDefaultState()),
					new StructureProcessorRule(new BlockMatchRuleTest(Blocks.field_10099), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10124.getDefaultState()),
					new StructureProcessorRule(
						new BlockMatchRuleTest(Blocks.field_17350),
						AlwaysTrueRuleTest.INSTANCE,
						Blocks.field_17350.getDefaultState().with(CampfireBlock.LIT, Boolean.valueOf(false))
					),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10445, 0.08F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10343.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10037, 0.08F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10343.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10285, 0.5F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10343.getDefaultState()),
					new StructureProcessorRule(
						new BlockStateMatchRuleTest(
							Blocks.field_10285.getDefaultState().with(PaneBlock.NORTH, Boolean.valueOf(true)).with(PaneBlock.SOUTH, Boolean.valueOf(true))
						),
						AlwaysTrueRuleTest.INSTANCE,
						Blocks.field_10163.getDefaultState().with(PaneBlock.NORTH, Boolean.valueOf(true)).with(PaneBlock.SOUTH, Boolean.valueOf(true))
					),
					new StructureProcessorRule(
						new BlockStateMatchRuleTest(Blocks.field_10285.getDefaultState().with(PaneBlock.EAST, Boolean.valueOf(true)).with(PaneBlock.WEST, Boolean.valueOf(true))),
						AlwaysTrueRuleTest.INSTANCE,
						Blocks.field_10163.getDefaultState().with(PaneBlock.EAST, Boolean.valueOf(true)).with(PaneBlock.WEST, Boolean.valueOf(true))
					),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10293, 0.3F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_9984.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10293, 0.2F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10247.getDefaultState())
				)
			)
		)
	);
	public static final StructureProcessorList field_26263 = register(
		"zombie_desert",
		ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(new TagMatchRuleTest(BlockTags.field_15495), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10124.getDefaultState()),
					new StructureProcessorRule(new BlockMatchRuleTest(Blocks.field_10336), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10124.getDefaultState()),
					new StructureProcessorRule(new BlockMatchRuleTest(Blocks.field_10099), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10124.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10467, 0.08F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10343.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10361, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10343.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10415, 0.08F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10343.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10549, 0.08F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10343.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10262, 0.08F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10343.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10293, 0.2F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10341.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10293, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10168.getDefaultState())
				)
			)
		)
	);
	public static final StructureProcessorList field_26264 = register(
		"mossify_10_percent",
		ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10445, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_9989.getDefaultState())
				)
			)
		)
	);
	public static final StructureProcessorList field_26265 = register(
		"mossify_20_percent",
		ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10445, 0.2F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_9989.getDefaultState())
				)
			)
		)
	);
	public static final StructureProcessorList field_26266 = register(
		"mossify_70_percent",
		ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10445, 0.7F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_9989.getDefaultState())
				)
			)
		)
	);
	public static final StructureProcessorList field_26267 = register(
		"street_plains",
		ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(new BlockMatchRuleTest(Blocks.field_10194), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10161.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10194, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10219.getDefaultState()),
					new StructureProcessorRule(new BlockMatchRuleTest(Blocks.field_10219), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10382.getDefaultState()),
					new StructureProcessorRule(new BlockMatchRuleTest(Blocks.field_10566), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10382.getDefaultState())
				)
			)
		)
	);
	public static final StructureProcessorList field_26268 = register(
		"street_savanna",
		ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(new BlockMatchRuleTest(Blocks.field_10194), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10218.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10194, 0.2F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10219.getDefaultState()),
					new StructureProcessorRule(new BlockMatchRuleTest(Blocks.field_10219), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10382.getDefaultState()),
					new StructureProcessorRule(new BlockMatchRuleTest(Blocks.field_10566), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10382.getDefaultState())
				)
			)
		)
	);
	public static final StructureProcessorList field_26269 = register(
		"street_snowy_or_taiga",
		ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(new BlockMatchRuleTest(Blocks.field_10194), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_9975.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10194, 0.2F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10219.getDefaultState()),
					new StructureProcessorRule(new BlockMatchRuleTest(Blocks.field_10219), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10382.getDefaultState()),
					new StructureProcessorRule(new BlockMatchRuleTest(Blocks.field_10566), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10382.getDefaultState())
				)
			)
		)
	);
	public static final StructureProcessorList field_26270 = register(
		"farm_plains",
		ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10293, 0.3F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10609.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10293, 0.2F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10247.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10293, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10341.getDefaultState())
				)
			)
		)
	);
	public static final StructureProcessorList field_26271 = register(
		"farm_savanna",
		ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10293, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10168.getDefaultState())
				)
			)
		)
	);
	public static final StructureProcessorList field_26272 = register(
		"farm_snowy",
		ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10293, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10609.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10293, 0.8F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10247.getDefaultState())
				)
			)
		)
	);
	public static final StructureProcessorList field_26273 = register(
		"farm_taiga",
		ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10293, 0.3F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_9984.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10293, 0.2F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10247.getDefaultState())
				)
			)
		)
	);
	public static final StructureProcessorList field_26274 = register(
		"farm_desert",
		ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10293, 0.2F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10341.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10293, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10168.getDefaultState())
				)
			)
		)
	);
	public static final StructureProcessorList field_26689 = register("outpost_rot", ImmutableList.of(new BlockRotStructureProcessor(0.05F)));
	public static final StructureProcessorList field_26275 = register(
		"bottom_rampart",
		ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10092, 0.75F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_23875.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_23875, 0.15F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_23874.getDefaultState()),
					field_26622,
					field_26621
				)
			)
		)
	);
	public static final StructureProcessorList field_26276 = register(
		"treasure_rooms",
		ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_23874, 0.35F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_23875.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_23876, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_23875.getDefaultState()),
					field_26622,
					field_26621
				)
			)
		)
	);
	public static final StructureProcessorList field_26277 = register(
		"housing",
		ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_23874, 0.3F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_23875.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_23869, 1.0E-4F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10124.getDefaultState()),
					field_26622,
					field_26621
				)
			)
		)
	);
	public static final StructureProcessorList field_26278 = register(
		"side_wall_degradation",
		ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_23876, 0.5F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10124.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10205, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_23875.getDefaultState()),
					field_26622,
					field_26621
				)
			)
		)
	);
	public static final StructureProcessorList field_26279 = register(
		"stable_degradation",
		ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_23874, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_23875.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_23869, 1.0E-4F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10124.getDefaultState()),
					field_26622,
					field_26621
				)
			)
		)
	);
	public static final StructureProcessorList field_26280 = register(
		"bastion_generic_degradation",
		ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_23874, 0.3F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_23875.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_23869, 1.0E-4F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10124.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10205, 0.3F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_23875.getDefaultState()),
					field_26622,
					field_26621
				)
			)
		)
	);
	public static final StructureProcessorList field_26281 = register(
		"rampart_degradation",
		ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_23874, 0.4F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_23875.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_23869, 0.01F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_23875.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_23874, 1.0E-4F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10124.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_23869, 1.0E-4F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10124.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10205, 0.3F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_23875.getDefaultState()),
					field_26622,
					field_26621
				)
			)
		)
	);
	public static final StructureProcessorList field_26282 = register(
		"entrance_replacement",
		ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_23876, 0.5F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10124.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10205, 0.6F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_23875.getDefaultState()),
					field_26622,
					field_26621
				)
			)
		)
	);
	public static final StructureProcessorList field_26283 = register(
		"bridge",
		ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_23874, 0.3F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_23875.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_23869, 1.0E-4F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10124.getDefaultState())
				)
			)
		)
	);
	public static final StructureProcessorList field_26284 = register(
		"roof",
		ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_23874, 0.3F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_23875.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_23874, 0.15F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10124.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_23874, 0.3F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_23869.getDefaultState())
				)
			)
		)
	);
	public static final StructureProcessorList field_26256 = register(
		"high_wall",
		ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_23874, 0.01F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10124.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_23874, 0.5F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_23875.getDefaultState()),
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_23874, 0.3F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_23869.getDefaultState()),
					field_26622
				)
			)
		)
	);
	public static final StructureProcessorList field_26257 = register(
		"high_rampart",
		ImmutableList.of(
			new RuleStructureProcessor(
				ImmutableList.of(
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.field_10205, 0.3F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_23875.getDefaultState()),
					new StructureProcessorRule(
						AlwaysTrueRuleTest.INSTANCE,
						AlwaysTrueRuleTest.INSTANCE,
						new AxisAlignedLinearPosRuleTest(0.0F, 0.05F, 0, 100, Direction.Axis.field_11052),
						Blocks.field_10124.getDefaultState()
					),
					field_26622
				)
			)
		)
	);

	private static StructureProcessorList register(String id, ImmutableList<StructureProcessor> processorList) {
		Identifier identifier = new Identifier(id);
		StructureProcessorList structureProcessorList = new StructureProcessorList(processorList);
		return BuiltinRegistries.add(BuiltinRegistries.STRUCTURE_PROCESSOR_LIST, identifier, structureProcessorList);
	}
}

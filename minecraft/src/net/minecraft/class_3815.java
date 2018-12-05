package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Blocks;
import net.minecraft.sortme.rule.AlwaysTrueRuleTest;
import net.minecraft.sortme.rule.BlockMatchRuleTest;
import net.minecraft.sortme.rule.RandomBlockMatchRuleTest;
import net.minecraft.sortme.structures.processor.RuleStructureProcessor;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.config.feature.FeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;

public class class_3815 {
	public static void method_16754() {
	}

	static {
		class_3778.field_16666
			.method_16640(
				new class_3785(
					new Identifier("village/plains/town_centers"),
					new Identifier("empty"),
					ImmutableList.of(
						new Pair<>(
							new class_3781(
								"village/plains/town_centers/plains_fountain_01",
								ImmutableList.of(
									new RuleStructureProcessor(
										ImmutableList.of(
											new class_3821(new RandomBlockMatchRuleTest(Blocks.field_10445, 0.2F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_9989.getDefaultState())
										)
									)
								)
							),
							1
						),
						new Pair<>(
							new class_3781(
								"village/plains/town_centers/plains_meeting_point_1",
								ImmutableList.of(
									new RuleStructureProcessor(
										ImmutableList.of(
											new class_3821(new RandomBlockMatchRuleTest(Blocks.field_10445, 0.2F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_9989.getDefaultState())
										)
									)
								)
							),
							1
						),
						new Pair<>(new class_3781("village/plains/town_centers/plains_meeting_point_2"), 1),
						new Pair<>(
							new class_3781(
								"village/plains/town_centers/plains_meeting_point_3",
								ImmutableList.of(
									new RuleStructureProcessor(
										ImmutableList.of(
											new class_3821(new RandomBlockMatchRuleTest(Blocks.field_10445, 0.7F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_9989.getDefaultState())
										)
									)
								)
							),
							1
						)
					),
					class_3785.Projection.RIGID
				)
			);
		class_3778.field_16666
			.method_16640(
				new class_3785(
					new Identifier("village/plains/streets"),
					new Identifier("village/plains/terminators"),
					ImmutableList.of(
						new Pair<>(
							new class_3781(
								"village/plains/streets/corner_01",
								ImmutableList.of(
									new RuleStructureProcessor(
										ImmutableList.of(
											new class_3821(new BlockMatchRuleTest(Blocks.field_10194), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10161.getDefaultState()),
											new class_3821(new RandomBlockMatchRuleTest(Blocks.field_10194, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10219.getDefaultState()),
											new class_3821(new BlockMatchRuleTest(Blocks.field_10219), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10382.getDefaultState()),
											new class_3821(new BlockMatchRuleTest(Blocks.field_10566), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10382.getDefaultState())
										)
									)
								)
							),
							2
						),
						new Pair<>(
							new class_3781(
								"village/plains/streets/corner_02",
								ImmutableList.of(
									new RuleStructureProcessor(
										ImmutableList.of(
											new class_3821(new BlockMatchRuleTest(Blocks.field_10194), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10161.getDefaultState()),
											new class_3821(new RandomBlockMatchRuleTest(Blocks.field_10194, 0.15F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10219.getDefaultState()),
											new class_3821(new BlockMatchRuleTest(Blocks.field_10219), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10382.getDefaultState()),
											new class_3821(new BlockMatchRuleTest(Blocks.field_10566), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10382.getDefaultState())
										)
									)
								)
							),
							2
						),
						new Pair<>(
							new class_3781(
								"village/plains/streets/corner_03",
								ImmutableList.of(
									new RuleStructureProcessor(
										ImmutableList.of(
											new class_3821(new BlockMatchRuleTest(Blocks.field_10194), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10161.getDefaultState()),
											new class_3821(new RandomBlockMatchRuleTest(Blocks.field_10194, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10219.getDefaultState()),
											new class_3821(new BlockMatchRuleTest(Blocks.field_10219), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10382.getDefaultState()),
											new class_3821(new BlockMatchRuleTest(Blocks.field_10566), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10382.getDefaultState())
										)
									)
								)
							),
							2
						),
						new Pair<>(
							new class_3781(
								"village/plains/streets/straight_01",
								ImmutableList.of(
									new RuleStructureProcessor(
										ImmutableList.of(
											new class_3821(new BlockMatchRuleTest(Blocks.field_10194), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10161.getDefaultState()),
											new class_3821(new RandomBlockMatchRuleTest(Blocks.field_10194, 0.15F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10219.getDefaultState()),
											new class_3821(new BlockMatchRuleTest(Blocks.field_10219), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10382.getDefaultState()),
											new class_3821(new BlockMatchRuleTest(Blocks.field_10566), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10382.getDefaultState())
										)
									)
								)
							),
							4
						),
						new Pair<>(
							new class_3781(
								"village/plains/streets/straight_02",
								ImmutableList.of(
									new RuleStructureProcessor(
										ImmutableList.of(
											new class_3821(new BlockMatchRuleTest(Blocks.field_10194), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10161.getDefaultState()),
											new class_3821(new RandomBlockMatchRuleTest(Blocks.field_10194, 0.15F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10219.getDefaultState()),
											new class_3821(new BlockMatchRuleTest(Blocks.field_10219), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10382.getDefaultState()),
											new class_3821(new BlockMatchRuleTest(Blocks.field_10566), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10382.getDefaultState())
										)
									)
								)
							),
							4
						),
						new Pair<>(
							new class_3781(
								"village/plains/streets/straight_03",
								ImmutableList.of(
									new RuleStructureProcessor(
										ImmutableList.of(
											new class_3821(new BlockMatchRuleTest(Blocks.field_10194), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10161.getDefaultState()),
											new class_3821(new RandomBlockMatchRuleTest(Blocks.field_10194, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10219.getDefaultState()),
											new class_3821(new BlockMatchRuleTest(Blocks.field_10219), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10382.getDefaultState()),
											new class_3821(new BlockMatchRuleTest(Blocks.field_10566), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10382.getDefaultState())
										)
									)
								)
							),
							7
						),
						new Pair<>(
							new class_3781(
								"village/plains/streets/straight_04",
								ImmutableList.of(
									new RuleStructureProcessor(
										ImmutableList.of(
											new class_3821(new BlockMatchRuleTest(Blocks.field_10194), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10161.getDefaultState()),
											new class_3821(new RandomBlockMatchRuleTest(Blocks.field_10194, 0.15F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10219.getDefaultState()),
											new class_3821(new BlockMatchRuleTest(Blocks.field_10219), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10382.getDefaultState()),
											new class_3821(new BlockMatchRuleTest(Blocks.field_10566), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10382.getDefaultState())
										)
									)
								)
							),
							7
						),
						new Pair<>(
							new class_3781(
								"village/plains/streets/straight_05",
								ImmutableList.of(
									new RuleStructureProcessor(
										ImmutableList.of(
											new class_3821(new BlockMatchRuleTest(Blocks.field_10194), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10161.getDefaultState()),
											new class_3821(new RandomBlockMatchRuleTest(Blocks.field_10194, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10219.getDefaultState()),
											new class_3821(new BlockMatchRuleTest(Blocks.field_10219), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10382.getDefaultState()),
											new class_3821(new BlockMatchRuleTest(Blocks.field_10566), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10382.getDefaultState())
										)
									)
								)
							),
							3
						),
						new Pair<>(
							new class_3781(
								"village/plains/streets/straight_06",
								ImmutableList.of(
									new RuleStructureProcessor(
										ImmutableList.of(
											new class_3821(new BlockMatchRuleTest(Blocks.field_10194), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10161.getDefaultState()),
											new class_3821(new RandomBlockMatchRuleTest(Blocks.field_10194, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10219.getDefaultState()),
											new class_3821(new BlockMatchRuleTest(Blocks.field_10219), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10382.getDefaultState()),
											new class_3821(new BlockMatchRuleTest(Blocks.field_10566), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10382.getDefaultState())
										)
									)
								)
							),
							4
						),
						new Pair<>(
							new class_3781(
								"village/plains/streets/crossroad_01",
								ImmutableList.of(
									new RuleStructureProcessor(
										ImmutableList.of(
											new class_3821(new BlockMatchRuleTest(Blocks.field_10194), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10161.getDefaultState()),
											new class_3821(new RandomBlockMatchRuleTest(Blocks.field_10194, 0.15F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10219.getDefaultState()),
											new class_3821(new BlockMatchRuleTest(Blocks.field_10219), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10382.getDefaultState()),
											new class_3821(new BlockMatchRuleTest(Blocks.field_10566), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10382.getDefaultState())
										)
									)
								)
							),
							2
						),
						new Pair<>(
							new class_3781(
								"village/plains/streets/crossroad_02",
								ImmutableList.of(
									new RuleStructureProcessor(
										ImmutableList.of(
											new class_3821(new BlockMatchRuleTest(Blocks.field_10194), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10161.getDefaultState()),
											new class_3821(new RandomBlockMatchRuleTest(Blocks.field_10194, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10219.getDefaultState()),
											new class_3821(new BlockMatchRuleTest(Blocks.field_10219), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10382.getDefaultState()),
											new class_3821(new BlockMatchRuleTest(Blocks.field_10566), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10382.getDefaultState())
										)
									)
								)
							),
							1
						),
						new Pair<>(
							new class_3781(
								"village/plains/streets/crossroad_03",
								ImmutableList.of(
									new RuleStructureProcessor(
										ImmutableList.of(
											new class_3821(new BlockMatchRuleTest(Blocks.field_10194), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10161.getDefaultState()),
											new class_3821(new RandomBlockMatchRuleTest(Blocks.field_10194, 0.15F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10219.getDefaultState()),
											new class_3821(new BlockMatchRuleTest(Blocks.field_10219), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10382.getDefaultState()),
											new class_3821(new BlockMatchRuleTest(Blocks.field_10566), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10382.getDefaultState())
										)
									)
								)
							),
							2
						),
						new Pair<>(
							new class_3781(
								"village/plains/streets/crossroad_04",
								ImmutableList.of(
									new RuleStructureProcessor(
										ImmutableList.of(
											new class_3821(new BlockMatchRuleTest(Blocks.field_10194), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10161.getDefaultState()),
											new class_3821(new RandomBlockMatchRuleTest(Blocks.field_10194, 0.15F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10219.getDefaultState()),
											new class_3821(new BlockMatchRuleTest(Blocks.field_10219), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10382.getDefaultState()),
											new class_3821(new BlockMatchRuleTest(Blocks.field_10566), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10382.getDefaultState())
										)
									)
								)
							),
							2
						),
						new Pair<>(
							new class_3781(
								"village/plains/streets/crossroad_05",
								ImmutableList.of(
									new RuleStructureProcessor(
										ImmutableList.of(
											new class_3821(new BlockMatchRuleTest(Blocks.field_10194), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10161.getDefaultState()),
											new class_3821(new RandomBlockMatchRuleTest(Blocks.field_10194, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10219.getDefaultState()),
											new class_3821(new BlockMatchRuleTest(Blocks.field_10219), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10382.getDefaultState()),
											new class_3821(new BlockMatchRuleTest(Blocks.field_10566), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10382.getDefaultState())
										)
									)
								)
							),
							2
						),
						new Pair<>(
							new class_3781(
								"village/plains/streets/crossroad_06",
								ImmutableList.of(
									new RuleStructureProcessor(
										ImmutableList.of(
											new class_3821(new BlockMatchRuleTest(Blocks.field_10194), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10161.getDefaultState()),
											new class_3821(new RandomBlockMatchRuleTest(Blocks.field_10194, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10219.getDefaultState()),
											new class_3821(new BlockMatchRuleTest(Blocks.field_10219), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10382.getDefaultState()),
											new class_3821(new BlockMatchRuleTest(Blocks.field_10566), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10382.getDefaultState())
										)
									)
								)
							),
							2
						),
						new Pair<>(
							new class_3781(
								"village/plains/streets/turn_01",
								ImmutableList.of(
									new RuleStructureProcessor(
										ImmutableList.of(
											new class_3821(new BlockMatchRuleTest(Blocks.field_10194), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10161.getDefaultState()),
											new class_3821(new RandomBlockMatchRuleTest(Blocks.field_10194, 0.15F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10219.getDefaultState()),
											new class_3821(new BlockMatchRuleTest(Blocks.field_10219), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10382.getDefaultState()),
											new class_3821(new BlockMatchRuleTest(Blocks.field_10566), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10382.getDefaultState())
										)
									)
								)
							),
							3
						)
					),
					class_3785.Projection.TERRAIN_MATCHING
				)
			);
		class_3778.field_16666
			.method_16640(
				new class_3785(
					new Identifier("village/plains/houses"),
					new Identifier("village/plains/terminators"),
					ImmutableList.of(
						new Pair<>(
							new class_3781(
								"village/plains/houses/plains_small_house_1",
								ImmutableList.of(
									new RuleStructureProcessor(
										ImmutableList.of(
											new class_3821(new RandomBlockMatchRuleTest(Blocks.field_10445, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_9989.getDefaultState())
										)
									)
								)
							),
							2
						),
						new Pair<>(
							new class_3781(
								"village/plains/houses/plains_small_house_2",
								ImmutableList.of(
									new RuleStructureProcessor(
										ImmutableList.of(
											new class_3821(new RandomBlockMatchRuleTest(Blocks.field_10445, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_9989.getDefaultState())
										)
									)
								)
							),
							2
						),
						new Pair<>(
							new class_3781(
								"village/plains/houses/plains_small_house_3",
								ImmutableList.of(
									new RuleStructureProcessor(
										ImmutableList.of(
											new class_3821(new RandomBlockMatchRuleTest(Blocks.field_10445, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_9989.getDefaultState())
										)
									)
								)
							),
							2
						),
						new Pair<>(
							new class_3781(
								"village/plains/houses/plains_small_house_4",
								ImmutableList.of(
									new RuleStructureProcessor(
										ImmutableList.of(
											new class_3821(new RandomBlockMatchRuleTest(Blocks.field_10445, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_9989.getDefaultState())
										)
									)
								)
							),
							2
						),
						new Pair<>(
							new class_3781(
								"village/plains/houses/plains_small_house_5",
								ImmutableList.of(
									new RuleStructureProcessor(
										ImmutableList.of(
											new class_3821(new RandomBlockMatchRuleTest(Blocks.field_10445, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_9989.getDefaultState())
										)
									)
								)
							),
							2
						),
						new Pair<>(
							new class_3781(
								"village/plains/houses/plains_small_house_6",
								ImmutableList.of(
									new RuleStructureProcessor(
										ImmutableList.of(
											new class_3821(new RandomBlockMatchRuleTest(Blocks.field_10445, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_9989.getDefaultState())
										)
									)
								)
							),
							1
						),
						new Pair<>(
							new class_3781(
								"village/plains/houses/plains_small_house_7",
								ImmutableList.of(
									new RuleStructureProcessor(
										ImmutableList.of(
											new class_3821(new RandomBlockMatchRuleTest(Blocks.field_10445, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_9989.getDefaultState())
										)
									)
								)
							),
							2
						),
						new Pair<>(
							new class_3781(
								"village/plains/houses/plains_small_house_8",
								ImmutableList.of(
									new RuleStructureProcessor(
										ImmutableList.of(
											new class_3821(new RandomBlockMatchRuleTest(Blocks.field_10445, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_9989.getDefaultState())
										)
									)
								)
							),
							2
						),
						new Pair<>(
							new class_3781(
								"village/plains/houses/plains_medium_house_1",
								ImmutableList.of(
									new RuleStructureProcessor(
										ImmutableList.of(
											new class_3821(new RandomBlockMatchRuleTest(Blocks.field_10445, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_9989.getDefaultState())
										)
									)
								)
							),
							2
						),
						new Pair<>(
							new class_3781(
								"village/plains/houses/plains_medium_house_2",
								ImmutableList.of(
									new RuleStructureProcessor(
										ImmutableList.of(
											new class_3821(new RandomBlockMatchRuleTest(Blocks.field_10445, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_9989.getDefaultState())
										)
									)
								)
							),
							2
						),
						new Pair<>(
							new class_3781(
								"village/plains/houses/plains_big_house_1",
								ImmutableList.of(
									new RuleStructureProcessor(
										ImmutableList.of(
											new class_3821(new RandomBlockMatchRuleTest(Blocks.field_10445, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_9989.getDefaultState())
										)
									)
								)
							),
							2
						),
						new Pair<>(
							new class_3781(
								"village/plains/houses/plains_butcher_shop_1",
								ImmutableList.of(
									new RuleStructureProcessor(
										ImmutableList.of(
											new class_3821(new RandomBlockMatchRuleTest(Blocks.field_10445, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_9989.getDefaultState())
										)
									)
								)
							),
							2
						),
						new Pair<>(
							new class_3781(
								"village/plains/houses/plains_butcher_shop_2",
								ImmutableList.of(
									new RuleStructureProcessor(
										ImmutableList.of(
											new class_3821(new RandomBlockMatchRuleTest(Blocks.field_10445, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_9989.getDefaultState())
										)
									)
								)
							),
							2
						),
						new Pair<>(
							new class_3781(
								"village/plains/houses/plains_tool_smith_1",
								ImmutableList.of(
									new RuleStructureProcessor(
										ImmutableList.of(
											new class_3821(new RandomBlockMatchRuleTest(Blocks.field_10445, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_9989.getDefaultState())
										)
									)
								)
							),
							2
						),
						new Pair<>(
							new class_3781(
								"village/plains/houses/plains_fletcher_house_1",
								ImmutableList.of(
									new RuleStructureProcessor(
										ImmutableList.of(
											new class_3821(new RandomBlockMatchRuleTest(Blocks.field_10445, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_9989.getDefaultState())
										)
									)
								)
							),
							2
						),
						new Pair<>(new class_3781("village/plains/houses/plains_shepherds_house_1"), 2),
						new Pair<>(
							new class_3781(
								"village/plains/houses/plains_armorer_house_1",
								ImmutableList.of(
									new RuleStructureProcessor(
										ImmutableList.of(
											new class_3821(new RandomBlockMatchRuleTest(Blocks.field_10445, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_9989.getDefaultState())
										)
									)
								)
							),
							2
						),
						new Pair<>(
							new class_3781(
								"village/plains/houses/plains_fisher_cottage_1",
								ImmutableList.of(
									new RuleStructureProcessor(
										ImmutableList.of(
											new class_3821(new RandomBlockMatchRuleTest(Blocks.field_10445, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_9989.getDefaultState())
										)
									)
								)
							),
							2
						),
						new Pair<>(
							new class_3781(
								"village/plains/houses/plains_tannery_1",
								ImmutableList.of(
									new RuleStructureProcessor(
										ImmutableList.of(
											new class_3821(new RandomBlockMatchRuleTest(Blocks.field_10445, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_9989.getDefaultState())
										)
									)
								)
							),
							2
						),
						new Pair<>(
							new class_3781(
								"village/plains/houses/plains_cartographer_1",
								ImmutableList.of(
									new RuleStructureProcessor(
										ImmutableList.of(
											new class_3821(new RandomBlockMatchRuleTest(Blocks.field_10445, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_9989.getDefaultState())
										)
									)
								)
							),
							1
						),
						new Pair<>(
							new class_3781(
								"village/plains/houses/plains_library_1",
								ImmutableList.of(
									new RuleStructureProcessor(
										ImmutableList.of(
											new class_3821(new RandomBlockMatchRuleTest(Blocks.field_10445, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_9989.getDefaultState())
										)
									)
								)
							),
							3
						),
						new Pair<>(
							new class_3781(
								"village/plains/houses/plains_library_2",
								ImmutableList.of(
									new RuleStructureProcessor(
										ImmutableList.of(
											new class_3821(new RandomBlockMatchRuleTest(Blocks.field_10445, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_9989.getDefaultState())
										)
									)
								)
							),
							1
						),
						new Pair<>(
							new class_3781(
								"village/plains/houses/plains_masons_house_1",
								ImmutableList.of(
									new RuleStructureProcessor(
										ImmutableList.of(
											new class_3821(new RandomBlockMatchRuleTest(Blocks.field_10445, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_9989.getDefaultState())
										)
									)
								)
							),
							2
						),
						new Pair<>(
							new class_3781(
								"village/plains/houses/plains_weaponsmith_1",
								ImmutableList.of(
									new RuleStructureProcessor(
										ImmutableList.of(
											new class_3821(new RandomBlockMatchRuleTest(Blocks.field_10445, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_9989.getDefaultState())
										)
									)
								)
							),
							2
						),
						new Pair<>(
							new class_3781(
								"village/plains/houses/plains_temple_3",
								ImmutableList.of(
									new RuleStructureProcessor(
										ImmutableList.of(
											new class_3821(new RandomBlockMatchRuleTest(Blocks.field_10445, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_9989.getDefaultState())
										)
									)
								)
							),
							2
						),
						new Pair<>(
							new class_3781(
								"village/plains/houses/plains_temple_4",
								ImmutableList.of(
									new RuleStructureProcessor(
										ImmutableList.of(
											new class_3821(new RandomBlockMatchRuleTest(Blocks.field_10445, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_9989.getDefaultState())
										)
									)
								)
							),
							2
						),
						new Pair<>(
							new class_3781(
								"village/plains/houses/plains_stable_1",
								ImmutableList.of(
									new RuleStructureProcessor(
										ImmutableList.of(
											new class_3821(new RandomBlockMatchRuleTest(Blocks.field_10445, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_9989.getDefaultState())
										)
									)
								)
							),
							2
						),
						new Pair<>(new class_3781("village/plains/houses/plains_stable_2"), 2),
						new Pair<>(new class_3781("village/plains/houses/plains_large_farm_1"), 4),
						new Pair<>(new class_3781("village/plains/houses/plains_small_farm_1"), 4),
						new Pair<>(new class_3781("village/plains/houses/plains_animal_pen_1"), 1),
						new Pair<>(new class_3781("village/plains/houses/plains_animal_pen_2"), 1),
						new Pair<>(new class_3781("village/plains/houses/plains_animal_pen_3"), 5),
						new Pair<>(new class_3781("village/plains/houses/plains_accessory_1"), 1),
						new Pair<>(
							new class_3781(
								"village/plains/houses/plains_meeting_point_4",
								ImmutableList.of(
									new RuleStructureProcessor(
										ImmutableList.of(
											new class_3821(new RandomBlockMatchRuleTest(Blocks.field_10445, 0.7F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_9989.getDefaultState())
										)
									)
								)
							),
							3
						),
						new Pair<>(new class_3781("village/plains/houses/plains_meeting_point_5"), 1),
						Pair.of(class_3777.field_16663, 10)
					),
					class_3785.Projection.RIGID
				)
			);
		class_3778.field_16666
			.method_16640(
				new class_3785(
					new Identifier("village/plains/terminators"),
					new Identifier("empty"),
					ImmutableList.of(
						new Pair<>(
							new class_3781(
								"village/plains/terminators/terminator_01",
								ImmutableList.of(
									new RuleStructureProcessor(
										ImmutableList.of(
											new class_3821(new BlockMatchRuleTest(Blocks.field_10194), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10161.getDefaultState()),
											new class_3821(new RandomBlockMatchRuleTest(Blocks.field_10194, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10219.getDefaultState()),
											new class_3821(new BlockMatchRuleTest(Blocks.field_10219), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10382.getDefaultState()),
											new class_3821(new BlockMatchRuleTest(Blocks.field_10566), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10382.getDefaultState())
										)
									)
								)
							),
							1
						),
						new Pair<>(
							new class_3781(
								"village/plains/terminators/terminator_02",
								ImmutableList.of(
									new RuleStructureProcessor(
										ImmutableList.of(
											new class_3821(new BlockMatchRuleTest(Blocks.field_10194), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10161.getDefaultState()),
											new class_3821(new RandomBlockMatchRuleTest(Blocks.field_10194, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10219.getDefaultState()),
											new class_3821(new BlockMatchRuleTest(Blocks.field_10219), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10382.getDefaultState()),
											new class_3821(new BlockMatchRuleTest(Blocks.field_10566), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10382.getDefaultState())
										)
									)
								)
							),
							1
						),
						new Pair<>(
							new class_3781(
								"village/plains/terminators/terminator_03",
								ImmutableList.of(
									new RuleStructureProcessor(
										ImmutableList.of(
											new class_3821(new BlockMatchRuleTest(Blocks.field_10194), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10161.getDefaultState()),
											new class_3821(new RandomBlockMatchRuleTest(Blocks.field_10194, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10219.getDefaultState()),
											new class_3821(new BlockMatchRuleTest(Blocks.field_10219), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10382.getDefaultState()),
											new class_3821(new BlockMatchRuleTest(Blocks.field_10566), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10382.getDefaultState())
										)
									)
								)
							),
							1
						),
						new Pair<>(
							new class_3781(
								"village/plains/terminators/terminator_04",
								ImmutableList.of(
									new RuleStructureProcessor(
										ImmutableList.of(
											new class_3821(new BlockMatchRuleTest(Blocks.field_10194), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10161.getDefaultState()),
											new class_3821(new RandomBlockMatchRuleTest(Blocks.field_10194, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.field_10219.getDefaultState()),
											new class_3821(new BlockMatchRuleTest(Blocks.field_10219), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10382.getDefaultState()),
											new class_3821(new BlockMatchRuleTest(Blocks.field_10566), new BlockMatchRuleTest(Blocks.field_10382), Blocks.field_10382.getDefaultState())
										)
									)
								)
							),
							1
						)
					),
					class_3785.Projection.TERRAIN_MATCHING
				)
			);
		class_3778.field_16666
			.method_16640(
				new class_3785(
					new Identifier("village/plains/trees"),
					new Identifier("empty"),
					ImmutableList.of(new Pair<>(new class_3776(new ConfiguredFeature<>(Feature.field_13510, FeatureConfig.DEFAULT)), 1)),
					class_3785.Projection.RIGID
				)
			);
		class_3778.field_16666
			.method_16640(
				new class_3785(
					new Identifier("village/plains/decor"),
					new Identifier("empty"),
					ImmutableList.of(
						new Pair<>(new class_3781("village/plains/plains_lamp_1"), 1),
						new Pair<>(new class_3776(new ConfiguredFeature<>(Feature.field_13510, FeatureConfig.DEFAULT)), 1),
						new Pair<>(new class_3776(new ConfiguredFeature<>(Feature.PLAIN_FLOWER, FeatureConfig.DEFAULT)), 1),
						new Pair<>(new class_3776(new ConfiguredFeature<>(Feature.field_16797, FeatureConfig.DEFAULT)), 1),
						Pair.of(class_3777.field_16663, 2)
					),
					class_3785.Projection.RIGID
				)
			);
		class_3778.field_16666
			.method_16640(
				new class_3785(
					new Identifier("village/plains/villagers"),
					new Identifier("empty"),
					ImmutableList.of(
						new Pair<>(new class_3781("village/plains/villagers/nitwit"), 1),
						new Pair<>(new class_3781("village/plains/villagers/unemployed"), 7),
						Pair.of(class_3777.field_16663, 1),
						new Pair<>(new class_3781("village/plains/villagers/armorer"), 1),
						new Pair<>(new class_3781("village/plains/villagers/butcher"), 2),
						new Pair<>(new class_3781("village/plains/villagers/cartographer"), 1),
						new Pair<>(new class_3781("village/plains/villagers/cleric"), 1),
						new Pair<>(new class_3781("village/plains/villagers/farmer"), 8),
						new Pair<>(new class_3781("village/plains/villagers/fishermen"), 3),
						new Pair<>(new class_3781("village/plains/villagers/fletcher"), 2),
						new Pair<>(new class_3781("village/plains/villagers/leatherworker"), 3),
						new Pair<>(new class_3781("village/plains/villagers/librarian"), 2),
						new Pair<>(new class_3781("village/plains/villagers/shepherd"), 3),
						new Pair<>(new class_3781("village/plains/villagers/toolsmith"), 4),
						new Pair<>(new class_3781("village/plains/villagers/weaponsmith"), 1)
					),
					class_3785.Projection.RIGID
				)
			);
		class_3778.field_16666
			.method_16640(
				new class_3785(
					new Identifier("village/common/animals"),
					new Identifier("empty"),
					ImmutableList.of(
						new Pair<>(new class_3781("village/common/animals/cows_1"), 7),
						new Pair<>(new class_3781("village/common/animals/pigs_1"), 7),
						new Pair<>(new class_3781("village/common/animals/horses_1"), 1),
						new Pair<>(new class_3781("village/common/animals/horses_2"), 1),
						new Pair<>(new class_3781("village/common/animals/horses_3"), 1),
						new Pair<>(new class_3781("village/common/animals/horses_4"), 1),
						new Pair<>(new class_3781("village/common/animals/horses_5"), 1),
						new Pair<>(new class_3781("village/common/animals/sheep_1"), 1),
						new Pair<>(new class_3781("village/common/animals/sheep_2"), 1),
						Pair.of(class_3777.field_16663, 5)
					),
					class_3785.Projection.RIGID
				)
			);
		class_3778.field_16666
			.method_16640(
				new class_3785(
					new Identifier("village/common/sheep"),
					new Identifier("empty"),
					ImmutableList.of(new Pair<>(new class_3781("village/common/animals/sheep_1"), 1), new Pair<>(new class_3781("village/common/animals/sheep_2"), 1)),
					class_3785.Projection.RIGID
				)
			);
		class_3778.field_16666
			.method_16640(
				new class_3785(
					new Identifier("village/common/butcher_animals"),
					new Identifier("empty"),
					ImmutableList.of(
						new Pair<>(new class_3781("village/common/animals/cows_1"), 3),
						new Pair<>(new class_3781("village/common/animals/pigs_1"), 3),
						new Pair<>(new class_3781("village/common/animals/sheep_1"), 1),
						new Pair<>(new class_3781("village/common/animals/sheep_2"), 1)
					),
					class_3785.Projection.RIGID
				)
			);
		class_3778.field_16666
			.method_16640(
				new class_3785(
					new Identifier("village/common/well_bottoms"),
					new Identifier("empty"),
					ImmutableList.of(new Pair<>(new class_3781("village/common/well_bottom"), 1)),
					class_3785.Projection.RIGID
				)
			);
	}
}

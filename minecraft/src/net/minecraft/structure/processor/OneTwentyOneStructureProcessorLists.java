package net.minecraft.structure.processor;

import java.util.List;
import net.minecraft.block.Blocks;
import net.minecraft.block.BulbBlock;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.structure.rule.AlwaysTrueRuleTest;
import net.minecraft.structure.rule.RandomBlockMatchRuleTest;
import net.minecraft.util.Identifier;

public class OneTwentyOneStructureProcessorLists {
	public static final RegistryKey<StructureProcessorList> TRIAL_CHAMBERS_COPPER_BULB_DEGRADATION = RegistryKey.of(
		RegistryKeys.PROCESSOR_LIST, new Identifier("trial_chambers_copper_bulb_degradation")
	);

	public static void bootstrap(Registerable<StructureProcessorList> processorListRegisterable) {
		register(
			processorListRegisterable,
			TRIAL_CHAMBERS_COPPER_BULB_DEGRADATION,
			List.of(
				new RuleStructureProcessor(
					List.of(
						new StructureProcessorRule(
							new RandomBlockMatchRuleTest(Blocks.WAXED_COPPER_BULB, 0.1F),
							AlwaysTrueRuleTest.INSTANCE,
							Blocks.OXIDIZED_COPPER_BULB.getDefaultState().with(BulbBlock.LIT, Boolean.valueOf(true))
						),
						new StructureProcessorRule(
							new RandomBlockMatchRuleTest(Blocks.WAXED_COPPER_BULB, 0.33333334F),
							AlwaysTrueRuleTest.INSTANCE,
							Blocks.WEATHERED_COPPER_BULB.getDefaultState().with(BulbBlock.LIT, Boolean.valueOf(true))
						),
						new StructureProcessorRule(
							new RandomBlockMatchRuleTest(Blocks.WAXED_COPPER_BULB, 0.5F),
							AlwaysTrueRuleTest.INSTANCE,
							Blocks.EXPOSED_COPPER_BULB.getDefaultState().with(BulbBlock.LIT, Boolean.valueOf(true))
						)
					)
				),
				new ProtectedBlocksStructureProcessor(BlockTags.FEATURES_CANNOT_REPLACE)
			)
		);
	}

	private static void register(
		Registerable<StructureProcessorList> processorListRegisterable, RegistryKey<StructureProcessorList> key, List<StructureProcessor> processors
	) {
		processorListRegisterable.register(key, new StructureProcessorList(processors));
	}
}

package net.minecraft.structure.processor;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldView;

public class RuleStructureProcessor extends StructureProcessor {
	public static final Codec<RuleStructureProcessor> field_25011 = StructureProcessorRule.CODEC
		.listOf()
		.fieldOf("rules")
		.<RuleStructureProcessor>xmap(RuleStructureProcessor::new, ruleStructureProcessor -> ruleStructureProcessor.rules)
		.codec();
	private final ImmutableList<StructureProcessorRule> rules;

	public RuleStructureProcessor(List<? extends StructureProcessorRule> list) {
		this.rules = ImmutableList.copyOf(list);
	}

	@Nullable
	@Override
	public Structure.StructureBlockInfo process(
		WorldView worldView,
		BlockPos pos,
		BlockPos blockPos,
		Structure.StructureBlockInfo structureBlockInfo,
		Structure.StructureBlockInfo structureBlockInfo2,
		StructurePlacementData structurePlacementData
	) {
		Random random = new Random(MathHelper.hashCode(structureBlockInfo2.pos));
		BlockState blockState = worldView.getBlockState(structureBlockInfo2.pos);

		for (StructureProcessorRule structureProcessorRule : this.rules) {
			if (structureProcessorRule.test(structureBlockInfo2.state, blockState, structureBlockInfo.pos, structureBlockInfo2.pos, blockPos, random)) {
				return new Structure.StructureBlockInfo(structureBlockInfo2.pos, structureProcessorRule.getOutputState(), structureProcessorRule.getTag());
			}
		}

		return structureBlockInfo2;
	}

	@Override
	protected StructureProcessorType<?> getType() {
		return StructureProcessorType.RULE;
	}
}

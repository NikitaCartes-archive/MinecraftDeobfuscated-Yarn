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
	public static final Codec<RuleStructureProcessor> CODEC = StructureProcessorRule.CODEC
		.listOf()
		.fieldOf("rules")
		.<RuleStructureProcessor>xmap(RuleStructureProcessor::new, ruleStructureProcessor -> ruleStructureProcessor.rules)
		.codec();
	private final ImmutableList<StructureProcessorRule> rules;

	public RuleStructureProcessor(List<? extends StructureProcessorRule> rules) {
		this.rules = ImmutableList.copyOf(rules);
	}

	@Nullable
	@Override
	public Structure.StructureBlockInfo process(
		WorldView world,
		BlockPos pos,
		BlockPos pivot,
		Structure.StructureBlockInfo originalBlockInfo,
		Structure.StructureBlockInfo currentBlockInfo,
		StructurePlacementData data
	) {
		Random random = new Random(MathHelper.hashCode(currentBlockInfo.pos));
		BlockState blockState = world.getBlockState(currentBlockInfo.pos);

		for (StructureProcessorRule structureProcessorRule : this.rules) {
			if (structureProcessorRule.test(currentBlockInfo.state, blockState, originalBlockInfo.pos, currentBlockInfo.pos, pivot, random)) {
				return new Structure.StructureBlockInfo(currentBlockInfo.pos, structureProcessorRule.getOutputState(), structureProcessorRule.getOutputNbt());
			}
		}

		return currentBlockInfo;
	}

	@Override
	protected StructureProcessorType<?> getType() {
		return StructureProcessorType.RULE;
	}
}

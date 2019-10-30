package net.minecraft.structure.processor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
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
	private final ImmutableList<StructureProcessorRule> rules;

	public RuleStructureProcessor(List<StructureProcessorRule> list) {
		this.rules = ImmutableList.copyOf(list);
	}

	public RuleStructureProcessor(Dynamic<?> dynamic) {
		this(dynamic.get("rules").asList(StructureProcessorRule::method_16765));
	}

	@Nullable
	@Override
	public Structure.StructureBlockInfo process(
		WorldView worldView,
		BlockPos pos,
		Structure.StructureBlockInfo structureBlockInfo,
		Structure.StructureBlockInfo structureBlockInfo2,
		StructurePlacementData placementData
	) {
		Random random = new Random(MathHelper.hashCode(structureBlockInfo2.pos));
		BlockState blockState = worldView.getBlockState(structureBlockInfo2.pos);

		for (StructureProcessorRule structureProcessorRule : this.rules) {
			if (structureProcessorRule.test(structureBlockInfo2.state, blockState, random)) {
				return new Structure.StructureBlockInfo(structureBlockInfo2.pos, structureProcessorRule.getOutputState(), structureProcessorRule.getTag());
			}
		}

		return structureBlockInfo2;
	}

	@Override
	protected StructureProcessorType getType() {
		return StructureProcessorType.RULE;
	}

	@Override
	protected <T> Dynamic<T> method_16666(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("rules"),
					dynamicOps.createList(this.rules.stream().map(structureProcessorRule -> structureProcessorRule.method_16764(dynamicOps).getValue()))
				)
			)
		);
	}
}

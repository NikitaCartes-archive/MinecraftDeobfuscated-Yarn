package net.minecraft.sortme.structures.processor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.class_3821;
import net.minecraft.block.BlockState;
import net.minecraft.sortme.Structure;
import net.minecraft.sortme.StructurePlacementData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ViewableWorld;

public class RuleStructureProcessor extends AbstractStructureProcessor {
	private final ImmutableList<class_3821> rules;

	public RuleStructureProcessor(List<class_3821> list) {
		this.rules = ImmutableList.copyOf(list);
	}

	public RuleStructureProcessor(Dynamic<?> dynamic) {
		this(dynamic.get("rules").asList(class_3821::method_16765));
	}

	@Nullable
	@Override
	public Structure.StructureBlockInfo process(
		ViewableWorld viewableWorld,
		BlockPos blockPos,
		Structure.StructureBlockInfo structureBlockInfo,
		Structure.StructureBlockInfo structureBlockInfo2,
		StructurePlacementData structurePlacementData
	) {
		Random random = new Random(MathHelper.hashCode(structureBlockInfo2.pos));
		BlockState blockState = viewableWorld.getBlockState(structureBlockInfo2.pos);

		for (class_3821 lv : this.rules) {
			if (lv.method_16762(structureBlockInfo2.state, blockState, random)) {
				return new Structure.StructureBlockInfo(structureBlockInfo2.pos, lv.method_16763(), lv.method_16760());
			}
		}

		return structureBlockInfo2;
	}

	@Override
	protected StructureProcessor getStructureProcessor() {
		return StructureProcessor.field_16990;
	}

	@Override
	protected <T> Dynamic<T> method_16666(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(dynamicOps.createString("rules"), dynamicOps.createList(this.rules.stream().map(arg -> arg.method_16764(dynamicOps).getValue())))
			)
		);
	}
}

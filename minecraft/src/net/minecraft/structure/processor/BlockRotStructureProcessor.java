package net.minecraft.structure.processor;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ViewableWorld;

public class BlockRotStructureProcessor extends StructureProcessor {
	private final float integrity;

	public BlockRotStructureProcessor(float f) {
		this.integrity = f;
	}

	public BlockRotStructureProcessor(Dynamic<?> dynamic) {
		this(dynamic.get("integrity").asFloat(1.0F));
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
		Random random = structurePlacementData.getRandom(blockPos);
		return !(this.integrity >= 1.0F) && !(random.nextFloat() <= this.integrity) ? null : structureBlockInfo2;
	}

	@Override
	protected StructureProcessorType getType() {
		return StructureProcessorType.field_16988;
	}

	@Override
	protected <T> Dynamic<T> method_16666(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("integrity"), dynamicOps.createFloat(this.integrity))));
	}
}

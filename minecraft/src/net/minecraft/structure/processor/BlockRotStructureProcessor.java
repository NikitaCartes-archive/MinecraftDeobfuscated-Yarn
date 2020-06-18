package net.minecraft.structure.processor;

import com.mojang.serialization.Codec;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public class BlockRotStructureProcessor extends StructureProcessor {
	public static final Codec<BlockRotStructureProcessor> CODEC = Codec.FLOAT
		.fieldOf("integrity")
		.withDefault(1.0F)
		.<BlockRotStructureProcessor>xmap(BlockRotStructureProcessor::new, blockRotStructureProcessor -> blockRotStructureProcessor.integrity)
		.codec();
	private final float integrity;

	public BlockRotStructureProcessor(float integrity) {
		this.integrity = integrity;
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
		Random random = structurePlacementData.getRandom(structureBlockInfo2.pos);
		return !(this.integrity >= 1.0F) && !(random.nextFloat() <= this.integrity) ? null : structureBlockInfo2;
	}

	@Override
	protected StructureProcessorType<?> getType() {
		return StructureProcessorType.BLOCK_ROT;
	}
}

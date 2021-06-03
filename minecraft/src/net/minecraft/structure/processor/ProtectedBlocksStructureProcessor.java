package net.minecraft.structure.processor;

import com.mojang.serialization.Codec;
import javax.annotation.Nullable;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import net.minecraft.world.gen.feature.Feature;

public class ProtectedBlocksStructureProcessor extends StructureProcessor {
	public final Identifier protectedBlocksTag;
	public static final Codec<ProtectedBlocksStructureProcessor> CODEC = Identifier.CODEC
		.xmap(ProtectedBlocksStructureProcessor::new, protectedBlocksStructureProcessor -> protectedBlocksStructureProcessor.protectedBlocksTag);

	public ProtectedBlocksStructureProcessor(Identifier protectedBlocksTag) {
		this.protectedBlocksTag = protectedBlocksTag;
	}

	@Nullable
	@Override
	public Structure.StructureBlockInfo process(
		WorldView world,
		BlockPos pos,
		BlockPos pivot,
		Structure.StructureBlockInfo structureBlockInfo,
		Structure.StructureBlockInfo structureBlockInfo2,
		StructurePlacementData data
	) {
		return Feature.notInBlockTagPredicate(this.protectedBlocksTag).test(world.getBlockState(structureBlockInfo2.pos)) ? structureBlockInfo2 : null;
	}

	@Override
	protected StructureProcessorType<?> getType() {
		return StructureProcessorType.PROTECTED_BLOCKS;
	}
}

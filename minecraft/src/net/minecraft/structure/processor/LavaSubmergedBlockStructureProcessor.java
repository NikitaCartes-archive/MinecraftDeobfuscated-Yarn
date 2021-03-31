package net.minecraft.structure.processor;

import com.mojang.serialization.Codec;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public class LavaSubmergedBlockStructureProcessor extends StructureProcessor {
	public static final Codec<LavaSubmergedBlockStructureProcessor> CODEC = Codec.unit(
		(Supplier<LavaSubmergedBlockStructureProcessor>)(() -> LavaSubmergedBlockStructureProcessor.INSTANCE)
	);
	public static final LavaSubmergedBlockStructureProcessor INSTANCE = new LavaSubmergedBlockStructureProcessor();

	@Nullable
	@Override
	public Structure.StructureBlockInfo process(
		WorldView world,
		BlockPos pos,
		BlockPos blockPos,
		Structure.StructureBlockInfo structureBlockInfo,
		Structure.StructureBlockInfo structureBlockInfo2,
		StructurePlacementData structurePlacementData
	) {
		BlockPos blockPos2 = structureBlockInfo2.pos;
		boolean bl = world.getBlockState(blockPos2).isOf(Blocks.LAVA);
		return bl && !Block.isShapeFullCube(structureBlockInfo2.state.getOutlineShape(world, blockPos2))
			? new Structure.StructureBlockInfo(blockPos2, Blocks.LAVA.getDefaultState(), structureBlockInfo2.nbt)
			: structureBlockInfo2;
	}

	@Override
	protected StructureProcessorType<?> getType() {
		return StructureProcessorType.LAVA_SUBMERGED_BLOCK;
	}
}

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
		BlockPos pivot,
		Structure.StructureBlockInfo originalBlockInfo,
		Structure.StructureBlockInfo currentBlockInfo,
		StructurePlacementData data
	) {
		BlockPos blockPos = currentBlockInfo.pos;
		boolean bl = world.getBlockState(blockPos).isOf(Blocks.LAVA);
		return bl && !Block.isShapeFullCube(currentBlockInfo.state.getOutlineShape(world, blockPos))
			? new Structure.StructureBlockInfo(blockPos, Blocks.LAVA.getDefaultState(), currentBlockInfo.nbt)
			: currentBlockInfo;
	}

	@Override
	protected StructureProcessorType<?> getType() {
		return StructureProcessorType.LAVA_SUBMERGED_BLOCK;
	}
}

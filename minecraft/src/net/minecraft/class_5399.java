package net.minecraft;

import com.mojang.serialization.Codec;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public class class_5399 extends StructureProcessor {
	public static final Codec<class_5399> field_25618 = Codec.unit((Supplier<class_5399>)(() -> class_5399.field_25619));
	public static final class_5399 field_25619 = new class_5399();

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
		BlockPos blockPos2 = structureBlockInfo2.pos;
		boolean bl = worldView.getBlockState(blockPos2).isOf(Blocks.LAVA);
		return bl && !Block.isShapeFullCube(structureBlockInfo2.state.getOutlineShape(worldView, blockPos2))
			? new Structure.StructureBlockInfo(blockPos2, Blocks.LAVA.getDefaultState(), structureBlockInfo2.tag)
			: structureBlockInfo2;
	}

	@Override
	protected StructureProcessorType<?> getType() {
		return StructureProcessorType.LAVA_SUBMERGED_BLOCK;
	}
}

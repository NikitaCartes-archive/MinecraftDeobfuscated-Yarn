package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class StructureVoidBlock extends Block {
	public static final MapCodec<StructureVoidBlock> CODEC = createCodec(StructureVoidBlock::new);
	private static final double SHAPE_MARGIN = 5.0;
	private static final VoxelShape SHAPE = Block.createCuboidShape(5.0, 5.0, 5.0, 11.0, 11.0, 11.0);

	@Override
	public MapCodec<StructureVoidBlock> getCodec() {
		return CODEC;
	}

	protected StructureVoidBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	protected BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.INVISIBLE;
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

	@Override
	protected float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
		return 1.0F;
	}
}

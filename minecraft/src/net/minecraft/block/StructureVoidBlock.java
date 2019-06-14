package net.minecraft.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.EntityContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class StructureVoidBlock extends Block {
	private static final VoxelShape field_11589 = Block.method_9541(5.0, 5.0, 5.0, 11.0, 11.0, 11.0);

	protected StructureVoidBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public BlockRenderType method_9604(BlockState blockState) {
		return BlockRenderType.field_11455;
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return field_11589;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public float method_9575(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return 1.0F;
	}

	@Override
	public PistonBehavior method_9527(BlockState blockState) {
		return PistonBehavior.field_15971;
	}
}

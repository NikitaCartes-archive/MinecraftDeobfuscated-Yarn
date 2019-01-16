package net.minecraft.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class StructureVoidBlock extends Block {
	private static final VoxelShape field_11589 = Block.createCubeShape(5.0, 5.0, 5.0, 11.0, 11.0, 11.0);

	protected StructureVoidBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public BlockRenderType getRenderType(BlockState blockState) {
		return BlockRenderType.field_11455;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return field_11589;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public float getAmbientOcclusionLightLevel(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return 1.0F;
	}

	@Override
	public PistonBehavior getPistonBehavior(BlockState blockState) {
		return PistonBehavior.field_15971;
	}
}

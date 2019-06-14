package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class BlockWithEntity extends Block implements BlockEntityProvider {
	protected BlockWithEntity(Block.Settings settings) {
		super(settings);
	}

	@Override
	public BlockRenderType method_9604(BlockState blockState) {
		return BlockRenderType.field_11455;
	}

	@Override
	public boolean method_9592(BlockState blockState, World world, BlockPos blockPos, int i, int j) {
		super.method_9592(blockState, world, blockPos, i, j);
		BlockEntity blockEntity = world.method_8321(blockPos);
		return blockEntity == null ? false : blockEntity.onBlockAction(i, j);
	}

	@Nullable
	@Override
	public NameableContainerProvider method_17454(BlockState blockState, World world, BlockPos blockPos) {
		BlockEntity blockEntity = world.method_8321(blockPos);
		return blockEntity instanceof NameableContainerProvider ? (NameableContainerProvider)blockEntity : null;
	}
}

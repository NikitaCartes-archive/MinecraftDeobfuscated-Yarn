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
	public BlockRenderType getRenderType(BlockState blockState) {
		return BlockRenderType.field_11455;
	}

	@Override
	public void onBlockRemoved(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (blockState.getBlock() != blockState2.getBlock()) {
			super.onBlockRemoved(blockState, world, blockPos, blockState2, bl);
			world.removeBlockEntity(blockPos);
		}
	}

	@Override
	public boolean onBlockAction(BlockState blockState, World world, BlockPos blockPos, int i, int j) {
		super.onBlockAction(blockState, world, blockPos, i, j);
		BlockEntity blockEntity = world.getBlockEntity(blockPos);
		return blockEntity == null ? false : blockEntity.onBlockAction(i, j);
	}

	@Nullable
	@Override
	public NameableContainerProvider method_17454(BlockState blockState, World world, BlockPos blockPos) {
		BlockEntity blockEntity = world.getBlockEntity(blockPos);
		return blockEntity instanceof NameableContainerProvider ? (NameableContainerProvider)blockEntity : null;
	}
}

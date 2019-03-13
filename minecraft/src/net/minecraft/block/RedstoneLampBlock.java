package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RedstoneLampBlock extends Block {
	public static final BooleanProperty field_11413 = RedstoneTorchBlock.field_11446;

	public RedstoneLampBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(this.method_9564().method_11657(field_11413, Boolean.valueOf(false)));
	}

	@Override
	public int method_9593(BlockState blockState) {
		return blockState.method_11654(field_11413) ? super.method_9593(blockState) : 0;
	}

	@Override
	public void method_9615(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2) {
		super.method_9615(blockState, world, blockPos, blockState2);
	}

	@Nullable
	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		return this.method_9564().method_11657(field_11413, Boolean.valueOf(itemPlacementContext.method_8045().method_8479(itemPlacementContext.method_8037())));
	}

	@Override
	public void method_9612(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2) {
		if (!world.isClient) {
			boolean bl = (Boolean)blockState.method_11654(field_11413);
			if (bl != world.method_8479(blockPos)) {
				if (bl) {
					world.method_8397().method_8676(blockPos, this, 4);
				} else {
					world.method_8652(blockPos, blockState.method_11572(field_11413), 2);
				}
			}
		}
	}

	@Override
	public void method_9588(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (!world.isClient) {
			if ((Boolean)blockState.method_11654(field_11413) && !world.method_8479(blockPos)) {
				world.method_8652(blockPos, blockState.method_11572(field_11413), 2);
			}
		}
	}

	@Override
	protected void method_9515(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_11413);
	}
}

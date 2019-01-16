package net.minecraft.block;

import net.minecraft.class_3914;
import net.minecraft.client.network.ClientDummyContainerProvider;
import net.minecraft.container.LoomContainer;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LoomBlock extends HorizontalFacingBlock {
	private static final TranslatableTextComponent field_17373 = new TranslatableTextComponent("container.loom");

	protected LoomBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		if (world.isClient) {
			return true;
		} else {
			playerEntity.openContainer(blockState.method_17526(world, blockPos));
			return true;
		}
	}

	@Override
	public NameableContainerProvider method_17454(BlockState blockState, World world, BlockPos blockPos) {
		return new ClientDummyContainerProvider(
			(i, playerInventory, playerEntity) -> new LoomContainer(i, playerInventory, class_3914.method_17392(world, blockPos)), field_17373
		);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		return this.getDefaultState().with(field_11177, itemPlacementContext.getPlayerHorizontalFacing());
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(field_11177);
	}
}

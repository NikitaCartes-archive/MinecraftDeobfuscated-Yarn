package net.minecraft.block;

import net.minecraft.container.BlockContext;
import net.minecraft.container.LoomContainer;
import net.minecraft.container.NameableContainerFactory;
import net.minecraft.container.SimpleNamedContainerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LoomBlock extends HorizontalFacingBlock {
	private static final TranslatableText CONTAINER_NAME = new TranslatableText("container.loom");

	protected LoomBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (world.isClient) {
			return ActionResult.SUCCESS;
		} else {
			player.openContainer(state.createContainerFactory(world, pos));
			player.incrementStat(Stats.INTERACT_WITH_LOOM);
			return ActionResult.SUCCESS;
		}
	}

	@Override
	public NameableContainerFactory createContainerFactory(BlockState state, World world, BlockPos pos) {
		return new SimpleNamedContainerFactory(
			(i, playerInventory, playerEntity) -> new LoomContainer(i, playerInventory, BlockContext.create(world, pos)), CONTAINER_NAME
		);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}
}

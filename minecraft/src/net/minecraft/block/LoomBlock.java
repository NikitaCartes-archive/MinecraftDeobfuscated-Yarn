package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.LoomScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LoomBlock extends HorizontalFacingBlock {
	public static final MapCodec<LoomBlock> CODEC = createCodec(LoomBlock::new);
	private static final Text TITLE = Text.translatable("container.loom");

	@Override
	public MapCodec<LoomBlock> getCodec() {
		return CODEC;
	}

	protected LoomBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
		if (!world.isClient) {
			player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
			player.incrementStat(Stats.INTERACT_WITH_LOOM);
		}

		return ActionResult.SUCCESS;
	}

	@Override
	protected NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
		return new SimpleNamedScreenHandlerFactory(
			(syncId, inventory, player) -> new LoomScreenHandler(syncId, inventory, ScreenHandlerContext.create(world, pos)), TITLE
		);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}
}

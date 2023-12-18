package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.screen.SmithingScreenHandler;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SmithingTableBlock extends CraftingTableBlock {
	public static final MapCodec<SmithingTableBlock> CODEC = createCodec(SmithingTableBlock::new);
	private static final Text SCREEN_TITLE = Text.translatable("container.upgrade");

	@Override
	public MapCodec<SmithingTableBlock> getCodec() {
		return CODEC;
	}

	protected SmithingTableBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
		return new SimpleNamedScreenHandlerFactory(
			(syncId, inventory, player) -> new SmithingScreenHandler(syncId, inventory, ScreenHandlerContext.create(world, pos)), SCREEN_TITLE
		);
	}

	@Override
	public ActionResult method_55766(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, BlockHitResult blockHitResult) {
		if (world.isClient) {
			return ActionResult.SUCCESS;
		} else {
			playerEntity.openHandledScreen(blockState.createScreenHandlerFactory(world, blockPos));
			playerEntity.incrementStat(Stats.INTERACT_WITH_SMITHING_TABLE);
			return ActionResult.CONSUME;
		}
	}
}

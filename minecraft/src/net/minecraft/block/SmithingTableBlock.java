package net.minecraft.block;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.LegacySmithingScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.screen.SmithingScreenHandler;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SmithingTableBlock extends CraftingTableBlock {
	private static final Text SCREEN_TITLE = Text.translatable("container.upgrade");

	protected SmithingTableBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
		return new SimpleNamedScreenHandlerFactory(
			(syncId, inventory, player) -> (ScreenHandler)(world.getEnabledFeatures().contains(FeatureFlags.UPDATE_1_20)
					? new SmithingScreenHandler(syncId, inventory, ScreenHandlerContext.create(world, pos))
					: new LegacySmithingScreenHandler(syncId, inventory, ScreenHandlerContext.create(world, pos))),
			SCREEN_TITLE
		);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (world.isClient) {
			return ActionResult.SUCCESS;
		} else {
			player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
			player.incrementStat(Stats.INTERACT_WITH_SMITHING_TABLE);
			return ActionResult.CONSUME;
		}
	}
}

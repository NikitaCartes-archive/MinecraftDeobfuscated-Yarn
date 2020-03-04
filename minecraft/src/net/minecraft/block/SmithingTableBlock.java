package net.minecraft.block;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.BlockContext;
import net.minecraft.screen.NameableScreenHandlerFactory;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.screen.SmithingScreenHandler;
import net.minecraft.stat.Stats;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SmithingTableBlock extends CraftingTableBlock {
	private static final TranslatableText SCREEN_TITLE = new TranslatableText("container.upgrade");

	protected SmithingTableBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public NameableScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
		return new SimpleNamedScreenHandlerFactory(
			(i, playerInventory, playerEntity) -> new SmithingScreenHandler(i, playerInventory, BlockContext.create(world, pos)), SCREEN_TITLE
		);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (world.isClient) {
			return ActionResult.SUCCESS;
		} else {
			player.openHandledScreen(state.createContainerFactory(world, pos));
			player.incrementStat(Stats.INTERACT_WITH_SMITHING_TABLE);
			return ActionResult.SUCCESS;
		}
	}
}

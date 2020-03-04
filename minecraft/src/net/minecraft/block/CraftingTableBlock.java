package net.minecraft.block;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.BlockContext;
import net.minecraft.screen.CraftingTableScreenHandler;
import net.minecraft.screen.NameableScreenHandlerFactory;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CraftingTableBlock extends Block {
	private static final Text CONTAINER_NAME = new TranslatableText("container.crafting");

	protected CraftingTableBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (world.isClient) {
			return ActionResult.SUCCESS;
		} else {
			player.openHandledScreen(state.createContainerFactory(world, pos));
			player.incrementStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
			return ActionResult.SUCCESS;
		}
	}

	@Override
	public NameableScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
		return new SimpleNamedScreenHandlerFactory(
			(i, playerInventory, playerEntity) -> new CraftingTableScreenHandler(i, playerInventory, BlockContext.create(world, pos)), CONTAINER_NAME
		);
	}
}

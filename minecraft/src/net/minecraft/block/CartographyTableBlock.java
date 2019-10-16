package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.client.network.ClientDummyContainerProvider;
import net.minecraft.container.BlockContext;
import net.minecraft.container.CartographyTableContainer;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CartographyTableBlock extends Block {
	private static final TranslatableText CONTAINER_NAME = new TranslatableText("container.cartography_table");

	protected CartographyTableBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult onUse(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		if (world.isClient) {
			return ActionResult.SUCCESS;
		} else {
			playerEntity.openContainer(blockState.createContainerProvider(world, blockPos));
			playerEntity.incrementStat(Stats.INTERACT_WITH_CARTOGRAPHY_TABLE);
			return ActionResult.SUCCESS;
		}
	}

	@Nullable
	@Override
	public NameableContainerProvider createContainerProvider(BlockState blockState, World world, BlockPos blockPos) {
		return new ClientDummyContainerProvider(
			(i, playerInventory, playerEntity) -> new CartographyTableContainer(i, playerInventory, BlockContext.create(world, blockPos)), CONTAINER_NAME
		);
	}
}

package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.client.network.ClientDummyContainerProvider;
import net.minecraft.container.CartographyTableContainer;
import net.minecraft.container.ContainerWorldContext;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CartographyTableBlock extends Block {
	private static final TranslatableTextComponent CONTAINER_NAME = new TranslatableTextComponent("container.cartography_table");

	protected CartographyTableBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		playerEntity.openContainer(blockState.createContainerProvider(world, blockPos));
		playerEntity.increaseStat(Stats.field_15368);
		return true;
	}

	@Nullable
	@Override
	public NameableContainerProvider createContainerProvider(BlockState blockState, World world, BlockPos blockPos) {
		return new ClientDummyContainerProvider(
			(i, playerInventory, playerEntity) -> new CartographyTableContainer(i, playerInventory, ContainerWorldContext.create(world, blockPos)), CONTAINER_NAME
		);
	}
}

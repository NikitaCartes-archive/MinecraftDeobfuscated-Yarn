package net.minecraft.block;

import net.minecraft.class_3914;
import net.minecraft.client.network.ClientDummyContainerProvider;
import net.minecraft.container.CraftingTableContainer;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CraftingTableBlock extends Block {
	private static final TextComponent CONTAINER_NAME = new TranslatableTextComponent("container.crafting");

	protected CraftingTableBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		playerEntity.openContainer(blockState.createContainerProvider(world, blockPos));
		playerEntity.increaseStat(Stats.field_15368);
		return true;
	}

	@Override
	public NameableContainerProvider createContainerProvider(BlockState blockState, World world, BlockPos blockPos) {
		return new ClientDummyContainerProvider(
			(i, playerInventory, playerEntity) -> new CraftingTableContainer(i, playerInventory, class_3914.method_17392(world, blockPos)), CONTAINER_NAME
		);
	}
}

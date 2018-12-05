package net.minecraft.block;

import net.minecraft.container.Container;
import net.minecraft.container.CraftingTableContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.stat.Stats;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class CraftingTableBlock extends Block {
	protected CraftingTableBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public boolean method_9534(
		BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, Direction direction, float f, float g, float h
	) {
		if (world.isRemote) {
			return true;
		} else {
			playerEntity.openContainer(new CraftingTableBlock.ContainerProvider(world, blockPos));
			playerEntity.method_7281(Stats.field_15368);
			return true;
		}
	}

	public static class ContainerProvider implements net.minecraft.container.ContainerProvider {
		private final World world;
		private final BlockPos pos;

		public ContainerProvider(World world, BlockPos blockPos) {
			this.world = world;
			this.pos = blockPos;
		}

		@Override
		public TextComponent getName() {
			return new TranslatableTextComponent(Blocks.field_9980.getTranslationKey() + ".name");
		}

		@Override
		public boolean hasCustomName() {
			return false;
		}

		@Override
		public Container createContainer(PlayerInventory playerInventory, PlayerEntity playerEntity) {
			return new CraftingTableContainer(playerInventory, this.world, this.pos);
		}

		@Override
		public String getContainerId() {
			return "minecraft:crafting_table";
		}
	}
}

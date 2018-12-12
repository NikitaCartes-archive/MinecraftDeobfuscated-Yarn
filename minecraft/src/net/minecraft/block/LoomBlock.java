package net.minecraft.block;

import net.minecraft.container.Container;
import net.minecraft.container.LoomContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class LoomBlock extends HorizontalFacingBlock {
	protected LoomBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public boolean activate(
		BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, Direction direction, float f, float g, float h
	) {
		if (world.isClient) {
			return true;
		} else {
			playerEntity.openContainer(new LoomBlock.ContainerProvider(blockPos.add(0.5, 0.0, 0.5)));
			return true;
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		return this.getDefaultState().with(field_11177, itemPlacementContext.getPlayerHorizontalFacing());
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(field_11177);
	}

	public static class ContainerProvider implements net.minecraft.container.ContainerProvider {
		private final BlockPos pos;

		public ContainerProvider(BlockPos blockPos) {
			this.pos = blockPos;
		}

		@Override
		public TextComponent getName() {
			return new TranslatableTextComponent(Blocks.field_10083.getTranslationKey() + ".name");
		}

		@Override
		public Container createContainer(PlayerInventory playerInventory, PlayerEntity playerEntity) {
			return new LoomContainer(playerInventory, this.pos);
		}

		@Override
		public String getContainerId() {
			return "minecraft:loom";
		}
	}
}

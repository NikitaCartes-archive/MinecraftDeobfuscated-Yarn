package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.WallHangingSignBlock;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class HangingSignItem extends VerticallyAttachableBlockItem {
	public HangingSignItem(Block hangingSign, Block wallHangingSign, Item.Settings settings) {
		super(hangingSign, wallHangingSign, settings, Direction.UP);
	}

	@Override
	protected boolean canPlaceAt(WorldView world, BlockState state, BlockPos pos) {
		if (state.getBlock() instanceof WallHangingSignBlock wallHangingSignBlock && !wallHangingSignBlock.canAttachAt(state, world, pos)) {
			return false;
		}

		return super.canPlaceAt(world, state, pos);
	}

	@Override
	protected boolean postPlacement(BlockPos pos, World world, @Nullable PlayerEntity player, ItemStack stack, BlockState state) {
		boolean bl = super.postPlacement(pos, world, player, stack, state);
		if (!world.isClient && !bl && player != null && world.getBlockEntity(pos) instanceof SignBlockEntity signBlockEntity) {
			player.openEditSignScreen(signBlockEntity);
		}

		return bl;
	}
}

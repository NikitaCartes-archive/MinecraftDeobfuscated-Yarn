package net.minecraft.block;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class SmithingTableBlock extends CraftingTableBlock {
	protected SmithingTableBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public boolean method_9534(
		BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, Direction direction, float f, float g, float h
	) {
		return false;
	}
}

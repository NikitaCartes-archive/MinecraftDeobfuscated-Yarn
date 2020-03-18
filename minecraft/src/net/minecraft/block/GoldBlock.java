package net.minecraft.block;

import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GoldBlock extends Block {
	public GoldBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		super.onBreak(world, pos, state, player);
		PiglinBrain.onGoldBlockBroken(player);
	}
}

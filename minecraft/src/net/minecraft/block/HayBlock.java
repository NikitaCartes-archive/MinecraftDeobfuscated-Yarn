package net.minecraft.block;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class HayBlock extends PillarBlock {
	public HayBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(this.field_10647.method_11664().method_11657(field_11459, Direction.Axis.Y));
	}

	@Override
	public void onLandedUpon(World world, BlockPos blockPos, Entity entity, float f) {
		entity.handleFallDamage(f, 0.2F);
	}
}

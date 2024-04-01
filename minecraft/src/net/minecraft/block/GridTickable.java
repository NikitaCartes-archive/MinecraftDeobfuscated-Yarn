package net.minecraft.block;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Grid;
import net.minecraft.world.World;

public interface GridTickable {
	void tick(World world, Grid grid, BlockState state, BlockPos pos, Vec3d gridPos, Direction movementDirection);
}

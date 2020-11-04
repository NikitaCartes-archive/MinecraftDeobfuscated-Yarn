package net.minecraft.world.chunk;

import net.minecraft.util.math.BlockPos;

public interface BlockEntityTickInvoker {
	void tick();

	boolean isRemoved();

	BlockPos getPos();

	String getName();
}

package net.minecraft.world.gen.surfacebuilder;

import net.minecraft.block.BlockState;

public interface SurfaceConfig {
	BlockState getTopMaterial();

	BlockState getUnderMaterial();

	BlockState getUnderwaterMaterial();
}

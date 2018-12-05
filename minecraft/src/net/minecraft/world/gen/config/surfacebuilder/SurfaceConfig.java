package net.minecraft.world.gen.config.surfacebuilder;

import net.minecraft.block.BlockState;

public interface SurfaceConfig {
	BlockState getTopMaterial();

	BlockState getUnderMaterial();
}

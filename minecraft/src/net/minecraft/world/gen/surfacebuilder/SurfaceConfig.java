package net.minecraft.world.gen.surfacebuilder;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.block.BlockState;

public interface SurfaceConfig {
	BlockState getTopMaterial();

	BlockState getUnderMaterial();

	<T> Dynamic<T> method_26681(DynamicOps<T> dynamicOps);
}

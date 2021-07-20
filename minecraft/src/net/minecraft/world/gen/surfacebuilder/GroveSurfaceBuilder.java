package net.minecraft.world.gen.surfacebuilder;

import com.mojang.serialization.Codec;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class GroveSurfaceBuilder extends NewMountainSurfaceBuilder {
	public GroveSurfaceBuilder(Codec<TernarySurfaceConfig> codec) {
		super(codec);
	}

	@Nullable
	@Override
	protected NewMountainSurfaceBuilder.class_6474 method_37775() {
		return null;
	}

	@Override
	protected BlockState getTopMaterial(TernarySurfaceConfig config, int x, int z) {
		return this.method_37778(0.1, x, z, Blocks.SNOW_BLOCK.getDefaultState(), Blocks.POWDER_SNOW.getDefaultState(), 0.35, 0.6);
	}

	@Override
	protected BlockState getUnderMaterial(TernarySurfaceConfig config, int x, int z) {
		return this.method_37778(0.1, x, z, Blocks.DIRT.getDefaultState(), Blocks.POWDER_SNOW.getDefaultState(), 0.45, 0.58);
	}
}
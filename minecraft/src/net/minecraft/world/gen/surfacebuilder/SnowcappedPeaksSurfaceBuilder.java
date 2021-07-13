package net.minecraft.world.gen.surfacebuilder;

import com.mojang.serialization.Codec;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class SnowcappedPeaksSurfaceBuilder extends NewMountainSurfaceBuilder {
	private final NewMountainSurfaceBuilder.class_6474 field_34259 = new NewMountainSurfaceBuilder.class_6474(
		Blocks.PACKED_ICE.getDefaultState(), true, false, false, true
	);

	public SnowcappedPeaksSurfaceBuilder(Codec<TernarySurfaceConfig> codec) {
		super(codec);
	}

	@Nullable
	@Override
	protected NewMountainSurfaceBuilder.class_6474 method_37775() {
		return this.field_34259;
	}

	@Override
	protected BlockState getTopMaterial(TernarySurfaceConfig config, int x, int z) {
		BlockState blockState = this.method_37778(0.5, x, z, Blocks.SNOW_BLOCK.getDefaultState(), Blocks.ICE.getDefaultState(), 0.0, 0.025);
		return this.method_37778(0.0625, x, z, blockState, Blocks.PACKED_ICE.getDefaultState(), 0.0, 0.2);
	}

	@Override
	protected BlockState getUnderMaterial(TernarySurfaceConfig config, int x, int z) {
		BlockState blockState = this.method_37778(0.5, x, z, Blocks.SNOW_BLOCK.getDefaultState(), Blocks.ICE.getDefaultState(), -0.0625, 0.025);
		return this.method_37778(0.0625, x, z, blockState, Blocks.PACKED_ICE.getDefaultState(), -0.5, 0.2);
	}
}

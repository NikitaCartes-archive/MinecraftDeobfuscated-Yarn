package net.minecraft.world.gen.surfacebuilder;

import com.mojang.serialization.Codec;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class LoftyPeaksSurfaceBuilder extends NewMountainSurfaceBuilder {
	private final NewMountainSurfaceBuilder.class_6474 field_34251 = new NewMountainSurfaceBuilder.class_6474(
		Blocks.STONE.getDefaultState(), true, false, false, true
	);

	public LoftyPeaksSurfaceBuilder(Codec<TernarySurfaceConfig> codec) {
		super(codec);
	}

	@Nullable
	@Override
	protected NewMountainSurfaceBuilder.class_6474 method_37775() {
		return this.field_34251;
	}

	@Override
	protected BlockState getTopMaterial(TernarySurfaceConfig config, int x, int z) {
		return config.getTopMaterial();
	}

	@Override
	protected BlockState getUnderMaterial(TernarySurfaceConfig config, int x, int z) {
		return config.getUnderMaterial();
	}
}

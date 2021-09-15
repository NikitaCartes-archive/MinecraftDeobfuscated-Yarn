package net.minecraft.world.gen.surfacebuilder;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.class_6557;
import net.minecraft.block.BlockState;
import net.minecraft.world.biome.Biome;

public class NopeSurfaceBuilder extends SurfaceBuilder<TernarySurfaceConfig> {
	public NopeSurfaceBuilder(Codec<TernarySurfaceConfig> codec) {
		super(codec);
	}

	public void generate(
		Random random,
		class_6557 arg,
		Biome biome,
		int i,
		int j,
		int k,
		double d,
		BlockState blockState,
		BlockState blockState2,
		int l,
		int m,
		long n,
		TernarySurfaceConfig ternarySurfaceConfig
	) {
	}
}

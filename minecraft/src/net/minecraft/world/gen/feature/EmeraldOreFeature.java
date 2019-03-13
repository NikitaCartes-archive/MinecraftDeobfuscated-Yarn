package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class EmeraldOreFeature extends Feature<EmeraldOreFeatureConfig> {
	public EmeraldOreFeature(Function<Dynamic<?>, ? extends EmeraldOreFeatureConfig> function) {
		super(function);
	}

	public boolean method_13811(
		IWorld iWorld,
		ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator,
		Random random,
		BlockPos blockPos,
		EmeraldOreFeatureConfig emeraldOreFeatureConfig
	) {
		if (iWorld.method_8320(blockPos).getBlock() == emeraldOreFeatureConfig.target.getBlock()) {
			iWorld.method_8652(blockPos, emeraldOreFeatureConfig.state, 2);
		}

		return true;
	}
}

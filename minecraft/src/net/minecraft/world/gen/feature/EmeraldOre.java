package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.config.feature.EmeraldOreFeatureConfig;

public class EmeraldOre extends Feature<EmeraldOreFeatureConfig> {
	public EmeraldOre(Function<Dynamic<?>, ? extends EmeraldOreFeatureConfig> function) {
		super(function);
	}

	public boolean method_13811(
		IWorld iWorld,
		ChunkGenerator<? extends ChunkGeneratorSettings> chunkGenerator,
		Random random,
		BlockPos blockPos,
		EmeraldOreFeatureConfig emeraldOreFeatureConfig
	) {
		if (iWorld.getBlockState(blockPos).getBlock() == emeraldOreFeatureConfig.target.getBlock()) {
			iWorld.setBlockState(blockPos, emeraldOreFeatureConfig.state, 2);
		}

		return true;
	}
}

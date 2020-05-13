package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class EmeraldOreFeature extends Feature<EmeraldOreFeatureConfig> {
	public EmeraldOreFeature(Function<Dynamic<?>, ? extends EmeraldOreFeatureConfig> function) {
		super(function);
	}

	public boolean generate(
		ServerWorldAccess serverWorldAccess,
		StructureAccessor structureAccessor,
		ChunkGenerator chunkGenerator,
		Random random,
		BlockPos blockPos,
		EmeraldOreFeatureConfig emeraldOreFeatureConfig
	) {
		if (serverWorldAccess.getBlockState(blockPos).isOf(emeraldOreFeatureConfig.target.getBlock())) {
			serverWorldAccess.setBlockState(blockPos, emeraldOreFeatureConfig.state, 2);
		}

		return true;
	}
}

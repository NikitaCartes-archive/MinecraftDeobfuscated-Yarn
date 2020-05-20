package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChorusFlowerBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class ChorusPlantFeature extends Feature<DefaultFeatureConfig> {
	public ChorusPlantFeature(Codec<DefaultFeatureConfig> codec) {
		super(codec);
	}

	public boolean generate(
		ServerWorldAccess serverWorldAccess,
		StructureAccessor structureAccessor,
		ChunkGenerator chunkGenerator,
		Random random,
		BlockPos blockPos,
		DefaultFeatureConfig defaultFeatureConfig
	) {
		if (serverWorldAccess.isAir(blockPos.up()) && serverWorldAccess.getBlockState(blockPos).isOf(Blocks.END_STONE)) {
			ChorusFlowerBlock.generate(serverWorldAccess, blockPos.up(), random, 8);
			return true;
		} else {
			return false;
		}
	}
}

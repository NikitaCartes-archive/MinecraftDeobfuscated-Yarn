package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class SimpleBlockFeature extends Feature<SimpleBlockFeatureConfig> {
	public SimpleBlockFeature(Function<Dynamic<?>, ? extends SimpleBlockFeatureConfig> function) {
		super(function);
	}

	public boolean generate(
		ServerWorldAccess serverWorldAccess,
		StructureAccessor structureAccessor,
		ChunkGenerator chunkGenerator,
		Random random,
		BlockPos blockPos,
		SimpleBlockFeatureConfig simpleBlockFeatureConfig
	) {
		if (simpleBlockFeatureConfig.placeOn.contains(serverWorldAccess.getBlockState(blockPos.down()))
			&& simpleBlockFeatureConfig.placeIn.contains(serverWorldAccess.getBlockState(blockPos))
			&& simpleBlockFeatureConfig.placeUnder.contains(serverWorldAccess.getBlockState(blockPos.up()))) {
			serverWorldAccess.setBlockState(blockPos, simpleBlockFeatureConfig.toPlace, 2);
			return true;
		} else {
			return false;
		}
	}
}

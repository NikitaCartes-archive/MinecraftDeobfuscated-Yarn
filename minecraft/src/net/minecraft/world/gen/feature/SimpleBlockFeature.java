package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class SimpleBlockFeature extends Feature<SimpleBlockFeatureConfig> {
	public SimpleBlockFeature(Function<Dynamic<?>, ? extends SimpleBlockFeatureConfig> function) {
		super(function);
	}

	public boolean method_13929(
		IWorld iWorld,
		ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator,
		Random random,
		BlockPos blockPos,
		SimpleBlockFeatureConfig simpleBlockFeatureConfig
	) {
		if (simpleBlockFeatureConfig.placeOn.contains(iWorld.method_8320(blockPos.down()))
			&& simpleBlockFeatureConfig.placeIn.contains(iWorld.method_8320(blockPos))
			&& simpleBlockFeatureConfig.placeUnder.contains(iWorld.method_8320(blockPos.up()))) {
			iWorld.method_8652(blockPos, simpleBlockFeatureConfig.toPlace, 2);
			return true;
		} else {
			return false;
		}
	}
}

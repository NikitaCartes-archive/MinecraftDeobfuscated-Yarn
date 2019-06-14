package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class MelonFeature extends Feature<DefaultFeatureConfig> {
	public MelonFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	public boolean method_13514(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig
	) {
		for (int i = 0; i < 64; i++) {
			BlockPos blockPos2 = blockPos.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
			BlockState blockState = Blocks.field_10545.method_9564();
			if (iWorld.method_8320(blockPos2).method_11620().isReplaceable() && iWorld.method_8320(blockPos2.down()).getBlock() == Blocks.field_10219) {
				iWorld.method_8652(blockPos2, blockState, 2);
			}
		}

		return true;
	}
}

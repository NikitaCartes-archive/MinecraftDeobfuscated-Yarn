package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.VineBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.config.feature.DefaultFeatureConfig;

public class VinesFeature extends Feature<DefaultFeatureConfig> {
	public VinesFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	public boolean method_14201(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorSettings> chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig
	) {
		BlockPos.Mutable mutable = new BlockPos.Mutable(blockPos);

		for (int i = blockPos.getY(); i < 256; i++) {
			mutable.set(blockPos);
			mutable.method_10100(random.nextInt(4) - random.nextInt(4), 0, random.nextInt(4) - random.nextInt(4));
			mutable.setY(i);
			if (iWorld.isAir(mutable)) {
				for (Direction direction : Direction.class_2353.HORIZONTAL) {
					BlockState blockState = Blocks.field_10597.getDefaultState().with(VineBlock.method_10828(direction), Boolean.valueOf(true));
					if (blockState.canPlaceAt(iWorld, mutable)) {
						iWorld.setBlockState(mutable, blockState, 2);
						break;
					}
				}
			}
		}

		return true;
	}
}

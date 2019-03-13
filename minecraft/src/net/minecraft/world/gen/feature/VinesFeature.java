package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Blocks;
import net.minecraft.block.VineBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class VinesFeature extends Feature<DefaultFeatureConfig> {
	private static final Direction[] field_17396 = Direction.values();

	public VinesFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	public boolean method_14201(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig
	) {
		BlockPos.Mutable mutable = new BlockPos.Mutable(blockPos);

		for (int i = blockPos.getY(); i < 256; i++) {
			mutable.method_10101(blockPos);
			mutable.setOffset(random.nextInt(4) - random.nextInt(4), 0, random.nextInt(4) - random.nextInt(4));
			mutable.setY(i);
			if (iWorld.method_8623(mutable)) {
				for (Direction direction : field_17396) {
					if (direction != Direction.DOWN && VineBlock.method_10821(iWorld, mutable, direction)) {
						iWorld.method_8652(mutable, Blocks.field_10597.method_9564().method_11657(VineBlock.method_10828(direction), Boolean.valueOf(true)), 2);
						break;
					}
				}
			}
		}

		return true;
	}
}

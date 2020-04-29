package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class BlueIceFeature extends Feature<DefaultFeatureConfig> {
	public BlueIceFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	public boolean generate(
		IWorld iWorld,
		StructureAccessor structureAccessor,
		ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator,
		Random random,
		BlockPos blockPos,
		DefaultFeatureConfig defaultFeatureConfig
	) {
		if (blockPos.getY() > iWorld.getSeaLevel() - 1) {
			return false;
		} else if (!iWorld.getBlockState(blockPos).isOf(Blocks.WATER) && !iWorld.getBlockState(blockPos.down()).isOf(Blocks.WATER)) {
			return false;
		} else {
			boolean bl = false;

			for (Direction direction : Direction.values()) {
				if (direction != Direction.DOWN && iWorld.getBlockState(blockPos.offset(direction)).isOf(Blocks.PACKED_ICE)) {
					bl = true;
					break;
				}
			}

			if (!bl) {
				return false;
			} else {
				iWorld.setBlockState(blockPos, Blocks.BLUE_ICE.getDefaultState(), 2);

				for (int i = 0; i < 200; i++) {
					int j = random.nextInt(5) - random.nextInt(6);
					int k = 3;
					if (j < 2) {
						k += j / 2;
					}

					if (k >= 1) {
						BlockPos blockPos2 = blockPos.add(random.nextInt(k) - random.nextInt(k), j, random.nextInt(k) - random.nextInt(k));
						BlockState blockState = iWorld.getBlockState(blockPos2);
						if (blockState.getMaterial() == Material.AIR || blockState.isOf(Blocks.WATER) || blockState.isOf(Blocks.PACKED_ICE) || blockState.isOf(Blocks.ICE)) {
							for (Direction direction2 : Direction.values()) {
								BlockState blockState2 = iWorld.getBlockState(blockPos2.offset(direction2));
								if (blockState2.isOf(Blocks.BLUE_ICE)) {
									iWorld.setBlockState(blockPos2, Blocks.BLUE_ICE.getDefaultState(), 2);
									break;
								}
							}
						}
					}
				}

				return true;
			}
		}
	}
}

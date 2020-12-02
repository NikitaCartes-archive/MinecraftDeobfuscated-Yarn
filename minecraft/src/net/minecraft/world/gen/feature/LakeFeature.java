package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.LightType;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class LakeFeature extends Feature<SingleStateFeatureConfig> {
	private static final BlockState CAVE_AIR = Blocks.CAVE_AIR.getDefaultState();

	public LakeFeature(Codec<SingleStateFeatureConfig> codec) {
		super(codec);
	}

	public boolean generate(
		StructureWorldAccess structureWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, SingleStateFeatureConfig singleStateFeatureConfig
	) {
		while (blockPos.getY() > structureWorldAccess.getSectionCount() + 5 && structureWorldAccess.isAir(blockPos)) {
			blockPos = blockPos.down();
		}

		if (blockPos.getY() <= structureWorldAccess.getSectionCount() + 4) {
			return false;
		} else {
			blockPos = blockPos.down(4);
			if (structureWorldAccess.getStructures(ChunkSectionPos.from(blockPos), StructureFeature.VILLAGE).findAny().isPresent()) {
				return false;
			} else {
				boolean[] bls = new boolean[2048];
				int i = random.nextInt(4) + 4;

				for (int j = 0; j < i; j++) {
					double d = random.nextDouble() * 6.0 + 3.0;
					double e = random.nextDouble() * 4.0 + 2.0;
					double f = random.nextDouble() * 6.0 + 3.0;
					double g = random.nextDouble() * (16.0 - d - 2.0) + 1.0 + d / 2.0;
					double h = random.nextDouble() * (8.0 - e - 4.0) + 2.0 + e / 2.0;
					double k = random.nextDouble() * (16.0 - f - 2.0) + 1.0 + f / 2.0;

					for (int l = 1; l < 15; l++) {
						for (int m = 1; m < 15; m++) {
							for (int n = 1; n < 7; n++) {
								double o = ((double)l - g) / (d / 2.0);
								double p = ((double)n - h) / (e / 2.0);
								double q = ((double)m - k) / (f / 2.0);
								double r = o * o + p * p + q * q;
								if (r < 1.0) {
									bls[(l * 16 + m) * 8 + n] = true;
								}
							}
						}
					}
				}

				for (int j = 0; j < 16; j++) {
					for (int s = 0; s < 16; s++) {
						for (int t = 0; t < 8; t++) {
							boolean bl = !bls[(j * 16 + s) * 8 + t]
								&& (
									j < 15 && bls[((j + 1) * 16 + s) * 8 + t]
										|| j > 0 && bls[((j - 1) * 16 + s) * 8 + t]
										|| s < 15 && bls[(j * 16 + s + 1) * 8 + t]
										|| s > 0 && bls[(j * 16 + (s - 1)) * 8 + t]
										|| t < 7 && bls[(j * 16 + s) * 8 + t + 1]
										|| t > 0 && bls[(j * 16 + s) * 8 + (t - 1)]
								);
							if (bl) {
								Material material = structureWorldAccess.getBlockState(blockPos.add(j, t, s)).getMaterial();
								if (t >= 4 && material.isLiquid()) {
									return false;
								}

								if (t < 4 && !material.isSolid() && structureWorldAccess.getBlockState(blockPos.add(j, t, s)) != singleStateFeatureConfig.state) {
									return false;
								}
							}
						}
					}
				}

				for (int j = 0; j < 16; j++) {
					for (int s = 0; s < 16; s++) {
						for (int tx = 0; tx < 8; tx++) {
							if (bls[(j * 16 + s) * 8 + tx]) {
								structureWorldAccess.setBlockState(blockPos.add(j, tx, s), tx >= 4 ? CAVE_AIR : singleStateFeatureConfig.state, 2);
							}
						}
					}
				}

				for (int j = 0; j < 16; j++) {
					for (int s = 0; s < 16; s++) {
						for (int txx = 4; txx < 8; txx++) {
							if (bls[(j * 16 + s) * 8 + txx]) {
								BlockPos blockPos2 = blockPos.add(j, txx - 1, s);
								if (isSoil(structureWorldAccess.getBlockState(blockPos2)) && structureWorldAccess.getLightLevel(LightType.SKY, blockPos.add(j, txx, s)) > 0) {
									Biome biome = structureWorldAccess.getBiome(blockPos2);
									if (biome.getGenerationSettings().getSurfaceConfig().getTopMaterial().isOf(Blocks.MYCELIUM)) {
										structureWorldAccess.setBlockState(blockPos2, Blocks.MYCELIUM.getDefaultState(), 2);
									} else {
										structureWorldAccess.setBlockState(blockPos2, Blocks.GRASS_BLOCK.getDefaultState(), 2);
									}
								}
							}
						}
					}
				}

				if (singleStateFeatureConfig.state.getMaterial() == Material.LAVA) {
					for (int j = 0; j < 16; j++) {
						for (int s = 0; s < 16; s++) {
							for (int txxx = 0; txxx < 8; txxx++) {
								boolean bl = !bls[(j * 16 + s) * 8 + txxx]
									&& (
										j < 15 && bls[((j + 1) * 16 + s) * 8 + txxx]
											|| j > 0 && bls[((j - 1) * 16 + s) * 8 + txxx]
											|| s < 15 && bls[(j * 16 + s + 1) * 8 + txxx]
											|| s > 0 && bls[(j * 16 + (s - 1)) * 8 + txxx]
											|| txxx < 7 && bls[(j * 16 + s) * 8 + txxx + 1]
											|| txxx > 0 && bls[(j * 16 + s) * 8 + (txxx - 1)]
									);
								if (bl && (txxx < 4 || random.nextInt(2) != 0) && structureWorldAccess.getBlockState(blockPos.add(j, txxx, s)).getMaterial().isSolid()) {
									structureWorldAccess.setBlockState(blockPos.add(j, txxx, s), Blocks.STONE.getDefaultState(), 2);
								}
							}
						}
					}
				}

				if (singleStateFeatureConfig.state.getMaterial() == Material.WATER) {
					for (int j = 0; j < 16; j++) {
						for (int s = 0; s < 16; s++) {
							int txxxx = 4;
							BlockPos blockPos2 = blockPos.add(j, 4, s);
							if (structureWorldAccess.getBiome(blockPos2).canSetIce(structureWorldAccess, blockPos2, false)) {
								structureWorldAccess.setBlockState(blockPos2, Blocks.ICE.getDefaultState(), 2);
							}
						}
					}
				}

				return true;
			}
		}
	}
}

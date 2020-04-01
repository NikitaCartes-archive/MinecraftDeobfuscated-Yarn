package net.minecraft;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.DirectionTransformation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.EmptyBlockView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.RandomDimension;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.stateprovider.SimpleBlockStateProvider;

public class class_5034 extends RandomDimension {
	private static final String[] field_23517 = method_26538();

	public class_5034(World world, DimensionType dimensionType) {
		super(world, dimensionType, 1.0F);
	}

	private static String[] method_26538() {
		try {
			InputStream inputStream = class_5034.class.getResourceAsStream("/credits.txt");
			Throwable var1 = null;

			String[] var6;
			try {
				Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
				Throwable var3 = null;

				try {
					BufferedReader bufferedReader = new BufferedReader(reader);
					Throwable var5 = null;

					try {
						var6 = (String[])bufferedReader.lines().toArray(String[]::new);
					} catch (Throwable var53) {
						var5 = var53;
						throw var53;
					} finally {
						if (bufferedReader != null) {
							if (var5 != null) {
								try {
									bufferedReader.close();
								} catch (Throwable var52) {
									var5.addSuppressed(var52);
								}
							} else {
								bufferedReader.close();
							}
						}
					}
				} catch (Throwable var55) {
					var3 = var55;
					throw var55;
				} finally {
					if (reader != null) {
						if (var3 != null) {
							try {
								reader.close();
							} catch (Throwable var51) {
								var3.addSuppressed(var51);
							}
						} else {
							reader.close();
						}
					}
				}
			} catch (Throwable var57) {
				var1 = var57;
				throw var57;
			} finally {
				if (inputStream != null) {
					if (var1 != null) {
						try {
							inputStream.close();
						} catch (Throwable var50) {
							var1.addSuppressed(var50);
						}
					} else {
						inputStream.close();
					}
				}
			}

			return var6;
		} catch (IOException var59) {
			return new String[0];
		}
	}

	@Override
	public ChunkGenerator<?> createChunkGenerator() {
		return new class_5034.class_5035(this.field_23566, method_26572(Biomes.THE_VOID), class_5099.field_23565);
	}

	@Override
	public float getSkyAngle(long timeOfDay, float tickDelta) {
		return 12000.0F;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public Vec3d modifyFogColor(Vec3d vec3d, float tickDelta) {
		return vec3d;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean isFogThick(int x, int z) {
		return false;
	}

	public static class class_5035 extends ChunkGenerator<class_5099> {
		final SimpleBlockStateProvider field_23518 = new SimpleBlockStateProvider(Blocks.SPONGE.getDefaultState());

		public class_5035(IWorld iWorld, BiomeSource biomeSource, class_5099 arg) {
			super(iWorld, biomeSource, arg);
		}

		@Override
		public void buildSurface(ChunkRegion region, Chunk chunk) {
		}

		@Override
		public int getSpawnHeight() {
			return 30;
		}

		@Override
		public void populateNoise(IWorld world, Chunk chunk) {
			ChunkPos chunkPos = chunk.getPos();
			int i = chunkPos.x * 2;
			int j = chunkPos.z * 2;
			this.method_26539(chunk, j, i, 0, 0);
			this.method_26539(chunk, j, i, 1, 0);
			this.method_26539(chunk, j, i, 0, 1);
			this.method_26539(chunk, j, i, 1, 1);
		}

		private void method_26539(Chunk chunk, int i, int j, int k, int l) {
			int m = j + k;
			int n = i + l;
			if (n >= 0 && n < class_5034.field_23517.length) {
				String string = class_5034.field_23517[n];
				if (m >= 0 && m < string.length()) {
					char c = string.charAt(m);
					class_5102.method_26586(
						new BlockPos(8 * k, 20, 8 * l),
						new class_5104(this.field_23518, c, DirectionTransformation.ROT_90_X_NEG),
						blockPos -> chunk.setBlockState(blockPos, Blocks.NETHERITE_BLOCK.getDefaultState(), false)
					);
				}
			}
		}

		@Override
		public void carve(BiomeAccess biomeAccess, Chunk chunk, GenerationStep.Carver carver) {
		}

		@Override
		public void generateFeatures(ChunkRegion region) {
		}

		@Override
		public int getHeight(int x, int z, Heightmap.Type heightmapType) {
			return 0;
		}

		@Override
		public BlockView getColumnSample(int x, int z) {
			return EmptyBlockView.INSTANCE;
		}

		@Override
		public ChunkGeneratorType<?, ?> method_26490() {
			return ChunkGeneratorType.field_23464;
		}
	}
}

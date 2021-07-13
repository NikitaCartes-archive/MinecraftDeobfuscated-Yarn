package net.minecraft.world.gen.surfacebuilder;

import com.mojang.serialization.Codec;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkRandom;

public abstract class NewMountainSurfaceBuilder extends DefaultSurfaceBuilder {
	private long seed;
	protected DoublePerlinNoiseSampler noiseSampler;

	public NewMountainSurfaceBuilder(Codec<TernarySurfaceConfig> codec) {
		super(codec);
	}

	@Override
	public void generate(
		Random random,
		Chunk chunk,
		Biome biome,
		int i,
		int j,
		int k,
		double d,
		BlockState blockState,
		BlockState blockState2,
		int l,
		int m,
		long n,
		TernarySurfaceConfig ternarySurfaceConfig
	) {
		BlockState blockState3;
		BlockState blockState4;
		if (this.method_37775() != null && this.method_37779(chunk, i, j, this.method_37775())) {
			blockState3 = this.method_37775().method_37780();
			blockState4 = this.method_37775().method_37780();
		} else {
			blockState3 = this.getTopMaterial(ternarySurfaceConfig, i, j);
			blockState4 = this.getUnderMaterial(ternarySurfaceConfig, i, j);
		}

		generate(random, chunk, biome, i, j, k, d, blockState, blockState2, blockState3, blockState4, ternarySurfaceConfig.getUnderwaterMaterial(), m);
	}

	protected BlockState method_37778(double d, int i, int j, BlockState blockState, BlockState blockState2, double e, double f) {
		double g = this.noiseSampler.sample((double)i * d, 100.0, (double)j * d);
		BlockState blockState3;
		if (g >= e && g <= f) {
			blockState3 = blockState2;
		} else {
			blockState3 = blockState;
		}

		return blockState3;
	}

	@Override
	public void initSeed(long seed) {
		if (this.seed != seed) {
			ChunkRandom chunkRandom = new ChunkRandom(seed);
			this.noiseSampler = DoublePerlinNoiseSampler.create(chunkRandom, -3, 1.0, 1.0, 1.0, 1.0);
		}

		this.seed = seed;
	}

	public boolean method_37779(Chunk chunk, int i, int j, NewMountainSurfaceBuilder.class_6474 arg) {
		int k = 1;
		int l = 3;
		int m = i & 15;
		int n = j & 15;
		if (arg.method_37781() || arg.method_37782()) {
			int o = Math.max(n - 1, 0);
			int p = Math.min(n + 1, 15);
			int q = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, m, o);
			int r = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, m, p);
			int s = q - r;
			if (arg.method_37782() && s > 3) {
				return true;
			}

			if (arg.method_37781() && -s > 3) {
				return true;
			}
		}

		if (!arg.method_37784() && !arg.method_37783()) {
			return false;
		} else {
			int ox = Math.max(m - 1, 0);
			int px = Math.min(m + 1, 15);
			int qx = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, ox, n);
			int rx = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, px, n);
			int sx = qx - rx;
			return arg.method_37784() && sx > 3 ? true : arg.method_37783() && -sx > 3;
		}
	}

	@Nullable
	protected abstract NewMountainSurfaceBuilder.class_6474 method_37775();

	protected abstract BlockState getTopMaterial(TernarySurfaceConfig config, int x, int z);

	protected abstract BlockState getUnderMaterial(TernarySurfaceConfig config, int x, int z);

	public static class class_6474 {
		private final BlockState field_34254;
		private final boolean field_34255;
		private final boolean field_34256;
		private final boolean field_34257;
		private final boolean field_34258;

		public class_6474(BlockState blockState, boolean bl, boolean bl2, boolean bl3, boolean bl4) {
			this.field_34254 = blockState;
			this.field_34255 = bl;
			this.field_34256 = bl2;
			this.field_34257 = bl3;
			this.field_34258 = bl4;
		}

		public BlockState method_37780() {
			return this.field_34254;
		}

		public boolean method_37781() {
			return this.field_34255;
		}

		public boolean method_37782() {
			return this.field_34256;
		}

		public boolean method_37783() {
			return this.field_34257;
		}

		public boolean method_37784() {
			return this.field_34258;
		}
	}
}

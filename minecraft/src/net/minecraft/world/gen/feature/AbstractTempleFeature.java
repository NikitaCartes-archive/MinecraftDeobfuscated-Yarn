package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public abstract class AbstractTempleFeature<C extends FeatureConfig> extends StructureFeature<C> {
	public AbstractTempleFeature(Function<Dynamic<?>, ? extends C> function) {
		super(function);
	}

	@Override
	protected ChunkPos method_14018(ChunkGenerator<?> chunkGenerator, Random random, int i, int j, int k, int l) {
		int m = this.method_13773(chunkGenerator);
		int n = this.method_13775(chunkGenerator);
		int o = i + m * k;
		int p = j + m * l;
		int q = o < 0 ? o - m + 1 : o;
		int r = p < 0 ? p - m + 1 : p;
		int s = q / m;
		int t = r / m;
		((ChunkRandom)random).setStructureSeed(chunkGenerator.getSeed(), s, t, this.method_13774());
		s *= m;
		t *= m;
		s += random.nextInt(m - n);
		t += random.nextInt(m - n);
		return new ChunkPos(s, t);
	}

	@Override
	public boolean method_14026(ChunkGenerator<?> chunkGenerator, Random random, int i, int j) {
		ChunkPos chunkPos = this.method_14018(chunkGenerator, random, i, j, 0, 0);
		if (i == chunkPos.x && j == chunkPos.z) {
			Biome biome = chunkGenerator.getBiomeSource().getBiome(new BlockPos(i * 16 + 9, 0, j * 16 + 9));
			if (chunkGenerator.hasStructure(biome, this)) {
				return true;
			}
		}

		return false;
	}

	protected int method_13773(ChunkGenerator<?> chunkGenerator) {
		return chunkGenerator.getSettings().getTempleDistance();
	}

	protected int method_13775(ChunkGenerator<?> chunkGenerator) {
		return chunkGenerator.getSettings().method_12568();
	}

	protected abstract int method_13774();
}

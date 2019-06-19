package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public abstract class AbstractTempleFeature<C extends FeatureConfig> extends StructureFeature<C> {
	public AbstractTempleFeature(Function<Dynamic<?>, ? extends C> function) {
		super(function);
	}

	@Override
	protected ChunkPos getStart(ChunkGenerator<?> chunkGenerator, Random random, int i, int j, int k, int l) {
		int m = this.getSpacing(chunkGenerator);
		int n = this.getSeparation(chunkGenerator);
		int o = i + m * k;
		int p = j + m * l;
		int q = o < 0 ? o - m + 1 : o;
		int r = p < 0 ? p - m + 1 : p;
		int s = q / m;
		int t = r / m;
		((ChunkRandom)random).setStructureSeed(chunkGenerator.getSeed(), s, t, this.getSeedModifier());
		s *= m;
		t *= m;
		s += random.nextInt(m - n);
		t += random.nextInt(m - n);
		return new ChunkPos(s, t);
	}

	@Override
	public boolean shouldStartAt(ChunkGenerator<?> chunkGenerator, Random random, int i, int j) {
		ChunkPos chunkPos = this.getStart(chunkGenerator, random, i, j, 0, 0);
		if (i == chunkPos.x && j == chunkPos.z) {
			Biome biome = chunkGenerator.getBiomeSource().getBiome(new BlockPos(i * 16 + 9, 0, j * 16 + 9));
			if (chunkGenerator.hasStructure(biome, this)) {
				return true;
			}
		}

		return false;
	}

	protected int getSpacing(ChunkGenerator<?> chunkGenerator) {
		return chunkGenerator.getConfig().getTempleDistance();
	}

	protected int getSeparation(ChunkGenerator<?> chunkGenerator) {
		return chunkGenerator.getConfig().getTempleSeparation();
	}

	protected abstract int getSeedModifier();
}

package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.structure.EndCityGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeAccess;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class EndCityFeature extends StructureFeature<DefaultFeatureConfig> {
	public EndCityFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	@Override
	protected ChunkPos getStart(ChunkGenerator<?> chunkGenerator, Random random, int i, int j, int k, int l) {
		int m = chunkGenerator.getConfig().getEndCityDistance();
		int n = chunkGenerator.getConfig().getEndCitySeparation();
		int o = i + m * k;
		int p = j + m * l;
		int q = o < 0 ? o - m + 1 : o;
		int r = p < 0 ? p - m + 1 : p;
		int s = q / m;
		int t = r / m;
		((ChunkRandom)random).setStructureSeed(chunkGenerator.getSeed(), s, t, 10387313);
		s *= m;
		t *= m;
		s += (random.nextInt(m - n) + random.nextInt(m - n)) / 2;
		t += (random.nextInt(m - n) + random.nextInt(m - n)) / 2;
		return new ChunkPos(s, t);
	}

	@Override
	public boolean shouldStartAt(BiomeAccess biomeAccess, ChunkGenerator<?> chunkGenerator, Random random, int i, int j, Biome biome) {
		ChunkPos chunkPos = this.getStart(chunkGenerator, random, i, j, 0, 0);
		if (i != chunkPos.x || j != chunkPos.z) {
			return false;
		} else if (!chunkGenerator.hasStructure(biome, this)) {
			return false;
		} else {
			int k = getGenerationHeight(i, j, chunkGenerator);
			return k >= 60;
		}
	}

	@Override
	public StructureFeature.StructureStartFactory getStructureStartFactory() {
		return EndCityFeature.Start::new;
	}

	@Override
	public String getName() {
		return "EndCity";
	}

	@Override
	public int getRadius() {
		return 8;
	}

	private static int getGenerationHeight(int i, int j, ChunkGenerator<?> chunkGenerator) {
		Random random = new Random((long)(i + j * 10387313));
		BlockRotation blockRotation = BlockRotation.values()[random.nextInt(BlockRotation.values().length)];
		int k = 5;
		int l = 5;
		if (blockRotation == BlockRotation.CLOCKWISE_90) {
			k = -5;
		} else if (blockRotation == BlockRotation.CLOCKWISE_180) {
			k = -5;
			l = -5;
		} else if (blockRotation == BlockRotation.COUNTERCLOCKWISE_90) {
			l = -5;
		}

		int m = (i << 4) + 7;
		int n = (j << 4) + 7;
		int o = chunkGenerator.getHeightInGround(m, n, Heightmap.Type.WORLD_SURFACE_WG);
		int p = chunkGenerator.getHeightInGround(m, n + l, Heightmap.Type.WORLD_SURFACE_WG);
		int q = chunkGenerator.getHeightInGround(m + k, n, Heightmap.Type.WORLD_SURFACE_WG);
		int r = chunkGenerator.getHeightInGround(m + k, n + l, Heightmap.Type.WORLD_SURFACE_WG);
		return Math.min(Math.min(o, p), Math.min(q, r));
	}

	public static class Start extends StructureStart {
		public Start(StructureFeature<?> structureFeature, int i, int j, BlockBox blockBox, int k, long l) {
			super(structureFeature, i, j, blockBox, k, l);
		}

		@Override
		public void initialize(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int i, int j, Biome biome) {
			BlockRotation blockRotation = BlockRotation.values()[this.random.nextInt(BlockRotation.values().length)];
			int k = EndCityFeature.getGenerationHeight(i, j, chunkGenerator);
			if (k >= 60) {
				BlockPos blockPos = new BlockPos(i * 16 + 8, k, j * 16 + 8);
				EndCityGenerator.addPieces(structureManager, blockPos, blockRotation, this.children, this.random);
				this.setBoundingBoxFromChildren();
			}
		}
	}
}

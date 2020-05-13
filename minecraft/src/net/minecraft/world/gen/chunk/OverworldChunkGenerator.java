package net.minecraft.world.gen.chunk;

import java.util.List;
import java.util.stream.IntStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.village.ZombieSiegeManager;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.CatSpawner;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.PhantomSpawner;
import net.minecraft.world.gen.PillagerSpawner;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.feature.Feature;

public class OverworldChunkGenerator extends SurfaceChunkGenerator<OverworldChunkGeneratorConfig> {
	private static final float[] BIOME_WEIGHT_TABLE = Util.make(new float[25], fs -> {
		for (int i = -2; i <= 2; i++) {
			for (int j = -2; j <= 2; j++) {
				float f = 10.0F / MathHelper.sqrt((float)(i * i + j * j) + 0.2F);
				fs[i + 2 + (j + 2) * 5] = f;
			}
		}
	});
	private final OctavePerlinNoiseSampler noiseSampler;
	private final PhantomSpawner phantomSpawner = new PhantomSpawner();
	private final PillagerSpawner pillagerSpawner = new PillagerSpawner();
	private final CatSpawner catSpawner = new CatSpawner();
	private final ZombieSiegeManager zombieSiegeManager = new ZombieSiegeManager();
	private final OverworldChunkGeneratorConfig field_24518;

	public OverworldChunkGenerator(BiomeSource biomeSource, long l, OverworldChunkGeneratorConfig overworldChunkGeneratorConfig) {
		super(biomeSource, l, overworldChunkGeneratorConfig, 4, 8, 256, true);
		this.field_24518 = overworldChunkGeneratorConfig;
		this.random.consume(2620);
		this.noiseSampler = new OctavePerlinNoiseSampler(this.random, IntStream.rangeClosed(-15, 0));
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ChunkGenerator method_27997(long l) {
		return new OverworldChunkGenerator(this.biomeSource.method_27985(l), l, this.field_24518);
	}

	@Override
	public void populateEntities(ChunkRegion region) {
		int i = region.getCenterChunkX();
		int j = region.getCenterChunkZ();
		Biome biome = region.getBiome(new ChunkPos(i, j).getCenterBlockPos());
		ChunkRandom chunkRandom = new ChunkRandom();
		chunkRandom.setPopulationSeed(region.getSeed(), i << 4, j << 4);
		SpawnHelper.populateEntities(region, biome, i, j, chunkRandom);
	}

	@Override
	protected void sampleNoiseColumn(double[] buffer, int x, int z) {
		double d = 684.412F;
		double e = 684.412F;
		double f = 8.555149841308594;
		double g = 4.277574920654297;
		int i = -10;
		int j = 3;
		this.sampleNoiseColumn(buffer, x, z, 684.412F, 684.412F, 8.555149841308594, 4.277574920654297, 3, -10);
	}

	@Override
	protected double computeNoiseFalloff(double depth, double scale, int y) {
		double d = 8.5;
		double e = ((double)y - (8.5 + depth * 8.5 / 8.0 * 4.0)) * 12.0 * 128.0 / 256.0 / scale;
		if (e < 0.0) {
			e *= 4.0;
		}

		return e;
	}

	@Override
	protected double[] computeNoiseRange(int x, int z) {
		double[] ds = new double[2];
		float f = 0.0F;
		float g = 0.0F;
		float h = 0.0F;
		int i = 2;
		int j = this.getSeaLevel();
		float k = this.biomeSource.getBiomeForNoiseGen(x, j, z).getDepth();

		for (int l = -2; l <= 2; l++) {
			for (int m = -2; m <= 2; m++) {
				Biome biome = this.biomeSource.getBiomeForNoiseGen(x + l, j, z + m);
				float n = biome.getDepth();
				float o = biome.getScale();
				if (this.field_24518.method_28008() && n > 0.0F) {
					n = 1.0F + n * 2.0F;
					o = 1.0F + o * 4.0F;
				}

				float p = BIOME_WEIGHT_TABLE[l + 2 + (m + 2) * 5] / (n + 2.0F);
				if (biome.getDepth() > k) {
					p /= 2.0F;
				}

				f += o * p;
				g += n * p;
				h += p;
			}
		}

		f /= h;
		g /= h;
		f = f * 0.9F + 0.1F;
		g = (g * 4.0F - 1.0F) / 8.0F;
		ds[0] = (double)g + this.sampleNoise(x, z);
		ds[1] = (double)f;
		return ds;
	}

	private double sampleNoise(int x, int y) {
		double d = this.noiseSampler.sample((double)(x * 200), 10.0, (double)(y * 200), 1.0, 0.0, true) * 65535.0 / 8000.0;
		if (d < 0.0) {
			d = -d * 0.3;
		}

		d = d * 3.0 - 2.0;
		if (d < 0.0) {
			d /= 28.0;
		} else {
			if (d > 1.0) {
				d = 1.0;
			}

			d /= 40.0;
		}

		return d;
	}

	@Override
	public List<Biome.SpawnEntry> getEntitySpawnList(Biome biome, StructureAccessor structureAccessor, SpawnGroup spawnGroup, BlockPos blockPos) {
		if (Feature.SWAMP_HUT.method_14029(structureAccessor, blockPos)) {
			if (spawnGroup == SpawnGroup.MONSTER) {
				return Feature.SWAMP_HUT.getMonsterSpawns();
			}

			if (spawnGroup == SpawnGroup.CREATURE) {
				return Feature.SWAMP_HUT.getCreatureSpawns();
			}
		} else if (spawnGroup == SpawnGroup.MONSTER) {
			if (Feature.PILLAGER_OUTPOST.isApproximatelyInsideStructure(structureAccessor, blockPos)) {
				return Feature.PILLAGER_OUTPOST.getMonsterSpawns();
			}

			if (Feature.OCEAN_MONUMENT.isApproximatelyInsideStructure(structureAccessor, blockPos)) {
				return Feature.OCEAN_MONUMENT.getMonsterSpawns();
			}
		}

		return super.getEntitySpawnList(biome, structureAccessor, spawnGroup, blockPos);
	}

	@Override
	public void spawnEntities(ServerWorld world, boolean spawnMonsters, boolean spawnAnimals) {
		this.phantomSpawner.spawn(world, spawnMonsters, spawnAnimals);
		this.pillagerSpawner.spawn(world, spawnMonsters, spawnAnimals);
		this.catSpawner.spawn(world, spawnMonsters, spawnAnimals);
		this.zombieSiegeManager.spawn(world, spawnMonsters, spawnAnimals);
	}

	@Override
	public int getSeaLevel() {
		return 63;
	}
}

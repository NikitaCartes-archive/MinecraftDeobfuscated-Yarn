package net.minecraft.world.gen.chunk;

import java.util.List;
import net.minecraft.entity.EntityCategory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.village.ZombieSiegeManager;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.IWorld;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.CatSpawner;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.PhantomSpawner;
import net.minecraft.world.gen.PillagerSpawner;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.level.LevelGeneratorType;

public class OverworldChunkGenerator extends SurfaceChunkGenerator<OverworldChunkGeneratorConfig> {
	private static final float[] BIOME_WEIGHT_TABLE = SystemUtil.consume(new float[25], fs -> {
		for (int i = -2; i <= 2; i++) {
			for (int j = -2; j <= 2; j++) {
				float f = 10.0F / MathHelper.sqrt((float)(i * i + j * j) + 0.2F);
				fs[i + 2 + (j + 2) * 5] = f;
			}
		}
	});
	private final OctavePerlinNoiseSampler noiseSampler;
	private final boolean amplified;
	private final PhantomSpawner phantomSpawner = new PhantomSpawner();
	private final PillagerSpawner pillagerSpawner = new PillagerSpawner();
	private final CatSpawner field_19181 = new CatSpawner();
	private final ZombieSiegeManager field_19430 = new ZombieSiegeManager();

	public OverworldChunkGenerator(IWorld iWorld, BiomeSource biomeSource, OverworldChunkGeneratorConfig overworldChunkGeneratorConfig) {
		super(iWorld, biomeSource, 4, 8, 256, overworldChunkGeneratorConfig, true);
		this.random.consume(2620);
		this.noiseSampler = new OctavePerlinNoiseSampler(this.random, 16);
		this.amplified = iWorld.getLevelProperties().getGeneratorType() == LevelGeneratorType.AMPLIFIED;
	}

	@Override
	public void populateEntities(ChunkRegion chunkRegion) {
		int i = chunkRegion.getCenterChunkX();
		int j = chunkRegion.getCenterChunkZ();
		Biome biome = chunkRegion.getChunk(i, j).getBiomeArray()[0];
		ChunkRandom chunkRandom = new ChunkRandom();
		chunkRandom.setSeed(chunkRegion.getSeed(), i << 4, j << 4);
		SpawnHelper.populateEntities(chunkRegion, biome, i, j, chunkRandom);
	}

	@Override
	protected void sampleNoiseColumn(double[] ds, int i, int j) {
		double d = 684.412F;
		double e = 684.412F;
		double f = 8.555149841308594;
		double g = 4.277574920654297;
		int k = -10;
		int l = 3;
		this.sampleNoiseColumn(ds, i, j, 684.412F, 684.412F, 8.555149841308594, 4.277574920654297, 3, -10);
	}

	@Override
	protected double computeNoiseFalloff(double d, double e, int i) {
		double f = 8.5;
		double g = ((double)i - (8.5 + d * 8.5 / 8.0 * 4.0)) * 12.0 * 128.0 / 256.0 / e;
		if (g < 0.0) {
			g *= 4.0;
		}

		return g;
	}

	@Override
	protected double[] computeNoiseRange(int i, int j) {
		double[] ds = new double[2];
		float f = 0.0F;
		float g = 0.0F;
		float h = 0.0F;
		int k = 2;
		float l = this.biomeSource.getBiomeForNoiseGen(i, j).getDepth();

		for (int m = -2; m <= 2; m++) {
			for (int n = -2; n <= 2; n++) {
				Biome biome = this.biomeSource.getBiomeForNoiseGen(i + m, j + n);
				float o = biome.getDepth();
				float p = biome.getScale();
				if (this.amplified && o > 0.0F) {
					o = 1.0F + o * 2.0F;
					p = 1.0F + p * 4.0F;
				}

				float q = BIOME_WEIGHT_TABLE[m + 2 + (n + 2) * 5] / (o + 2.0F);
				if (biome.getDepth() > l) {
					q /= 2.0F;
				}

				f += p * q;
				g += o * q;
				h += q;
			}
		}

		f /= h;
		g /= h;
		f = f * 0.9F + 0.1F;
		g = (g * 4.0F - 1.0F) / 8.0F;
		ds[0] = (double)g + this.method_16414(i, j);
		ds[1] = (double)f;
		return ds;
	}

	private double method_16414(int i, int j) {
		double d = this.noiseSampler.sample((double)(i * 200), 10.0, (double)(j * 200), 1.0, 0.0, true) / 8000.0;
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
	public List<Biome.SpawnEntry> getEntitySpawnList(EntityCategory entityCategory, BlockPos blockPos) {
		if (Feature.SWAMP_HUT.method_14029(this.world, blockPos)) {
			if (entityCategory == EntityCategory.field_6302) {
				return Feature.SWAMP_HUT.getMonsterSpawns();
			}

			if (entityCategory == EntityCategory.field_6294) {
				return Feature.SWAMP_HUT.getCreatureSpawns();
			}
		} else if (entityCategory == EntityCategory.field_6302) {
			if (Feature.PILLAGER_OUTPOST.isApproximatelyInsideStructure(this.world, blockPos)) {
				return Feature.PILLAGER_OUTPOST.getMonsterSpawns();
			}

			if (Feature.OCEAN_MONUMENT.isApproximatelyInsideStructure(this.world, blockPos)) {
				return Feature.OCEAN_MONUMENT.getMonsterSpawns();
			}
		}

		return super.getEntitySpawnList(entityCategory, blockPos);
	}

	@Override
	public void spawnEntities(ServerWorld serverWorld, boolean bl, boolean bl2) {
		this.phantomSpawner.spawn(serverWorld, bl, bl2);
		this.pillagerSpawner.spawn(serverWorld, bl, bl2);
		this.field_19181.spawn(serverWorld, bl, bl2);
		this.field_19430.tick(serverWorld, bl, bl2);
	}

	@Override
	public int getSpawnHeight() {
		return this.world.getSeaLevel() + 1;
	}

	@Override
	public int getSeaLevel() {
		return 63;
	}
}

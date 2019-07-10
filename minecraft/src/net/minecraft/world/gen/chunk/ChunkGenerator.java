package net.minecraft.world.gen.chunk;

import java.util.BitSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.client.network.DebugRendererInfoManager;
import net.minecraft.entity.EntityCategory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public abstract class ChunkGenerator<C extends ChunkGeneratorConfig> {
	protected final IWorld world;
	protected final long seed;
	protected final BiomeSource biomeSource;
	protected final C config;

	public ChunkGenerator(IWorld iWorld, BiomeSource biomeSource, C chunkGeneratorConfig) {
		this.world = iWorld;
		this.seed = iWorld.getSeed();
		this.biomeSource = biomeSource;
		this.config = chunkGeneratorConfig;
	}

	public void populateBiomes(Chunk chunk) {
		ChunkPos chunkPos = chunk.getPos();
		int i = chunkPos.x;
		int j = chunkPos.z;
		Biome[] biomes = this.biomeSource.sampleBiomes(i * 16, j * 16, 16, 16);
		chunk.setBiomeArray(biomes);
	}

	protected Biome getDecorationBiome(Chunk chunk) {
		return chunk.getBiome(BlockPos.ORIGIN);
	}

	protected Biome getDecorationBiome(ChunkRegion chunkRegion, BlockPos blockPos) {
		return this.biomeSource.getBiome(blockPos);
	}

	public void carve(Chunk chunk, GenerationStep.Carver carver) {
		ChunkRandom chunkRandom = new ChunkRandom();
		int i = 8;
		ChunkPos chunkPos = chunk.getPos();
		int j = chunkPos.x;
		int k = chunkPos.z;
		BitSet bitSet = chunk.getCarvingMask(carver);

		for (int l = j - 8; l <= j + 8; l++) {
			for (int m = k - 8; m <= k + 8; m++) {
				List<ConfiguredCarver<?>> list = this.getDecorationBiome(chunk).getCarversForStep(carver);
				ListIterator<ConfiguredCarver<?>> listIterator = list.listIterator();

				while (listIterator.hasNext()) {
					int n = listIterator.nextIndex();
					ConfiguredCarver<?> configuredCarver = (ConfiguredCarver<?>)listIterator.next();
					chunkRandom.setStructureSeed(this.seed + (long)n, l, m);
					if (configuredCarver.shouldCarve(chunkRandom, l, m)) {
						configuredCarver.carve(chunk, chunkRandom, this.getSeaLevel(), l, m, j, k, bitSet);
					}
				}
			}
		}
	}

	@Nullable
	public BlockPos locateStructure(World world, String string, BlockPos blockPos, int i, boolean bl) {
		StructureFeature<?> structureFeature = (StructureFeature<?>)Feature.STRUCTURES.get(string.toLowerCase(Locale.ROOT));
		return structureFeature != null ? structureFeature.locateStructure(world, this, blockPos, i, bl) : null;
	}

	public void generateFeatures(ChunkRegion chunkRegion) {
		int i = chunkRegion.getCenterChunkX();
		int j = chunkRegion.getCenterChunkZ();
		int k = i * 16;
		int l = j * 16;
		BlockPos blockPos = new BlockPos(k, 0, l);
		Biome biome = this.getDecorationBiome(chunkRegion, blockPos.add(8, 8, 8));
		ChunkRandom chunkRandom = new ChunkRandom();
		long m = chunkRandom.setSeed(chunkRegion.getSeed(), k, l);

		for (GenerationStep.Feature feature : GenerationStep.Feature.values()) {
			try {
				biome.generateFeatureStep(feature, this, chunkRegion, m, chunkRandom, blockPos);
			} catch (Exception var17) {
				CrashReport crashReport = CrashReport.create(var17, "Biome decoration");
				crashReport.addElement("Generation").add("CenterX", i).add("CenterZ", j).add("Step", feature).add("Seed", m).add("Biome", Registry.BIOME.getId(biome));
				throw new CrashException(crashReport);
			}
		}
	}

	public abstract void buildSurface(Chunk chunk);

	public void populateEntities(ChunkRegion chunkRegion) {
	}

	public C getConfig() {
		return this.config;
	}

	public abstract int getSpawnHeight();

	public void spawnEntities(ServerWorld serverWorld, boolean bl, boolean bl2) {
	}

	public boolean hasStructure(Biome biome, StructureFeature<? extends FeatureConfig> structureFeature) {
		return biome.hasStructureFeature(structureFeature);
	}

	@Nullable
	public <C extends FeatureConfig> C getStructureConfig(Biome biome, StructureFeature<C> structureFeature) {
		return biome.getStructureFeatureConfig(structureFeature);
	}

	public BiomeSource getBiomeSource() {
		return this.biomeSource;
	}

	public long getSeed() {
		return this.seed;
	}

	public int getMaxY() {
		return 256;
	}

	public List<Biome.SpawnEntry> getEntitySpawnList(EntityCategory entityCategory, BlockPos blockPos) {
		return this.world.getBiome(blockPos).getEntitySpawnList(entityCategory);
	}

	public void setStructureStarts(Chunk chunk, ChunkGenerator<?> chunkGenerator, StructureManager structureManager) {
		for (StructureFeature<?> structureFeature : Feature.STRUCTURES.values()) {
			if (chunkGenerator.getBiomeSource().hasStructureFeature(structureFeature)) {
				ChunkRandom chunkRandom = new ChunkRandom();
				ChunkPos chunkPos = chunk.getPos();
				StructureStart structureStart = StructureStart.DEFAULT;
				if (structureFeature.shouldStartAt(chunkGenerator, chunkRandom, chunkPos.x, chunkPos.z)) {
					Biome biome = this.getBiomeSource().getBiome(new BlockPos(chunkPos.getStartX() + 9, 0, chunkPos.getStartZ() + 9));
					StructureStart structureStart2 = structureFeature.getStructureStartFactory()
						.create(structureFeature, chunkPos.x, chunkPos.z, biome, MutableIntBoundingBox.empty(), 0, chunkGenerator.getSeed());
					structureStart2.initialize(this, structureManager, chunkPos.x, chunkPos.z, biome);
					structureStart = structureStart2.hasChildren() ? structureStart2 : StructureStart.DEFAULT;
				}

				chunk.setStructureStart(structureFeature.getName(), structureStart);
			}
		}
	}

	public void addStructureReferences(IWorld iWorld, Chunk chunk) {
		int i = 8;
		int j = chunk.getPos().x;
		int k = chunk.getPos().z;
		int l = j << 4;
		int m = k << 4;

		for (int n = j - 8; n <= j + 8; n++) {
			for (int o = k - 8; o <= k + 8; o++) {
				long p = ChunkPos.toLong(n, o);

				for (Entry<String, StructureStart> entry : iWorld.getChunk(n, o).getStructureStarts().entrySet()) {
					StructureStart structureStart = (StructureStart)entry.getValue();
					if (structureStart != StructureStart.DEFAULT && structureStart.getBoundingBox().intersectsXZ(l, m, l + 15, m + 15)) {
						chunk.addStructureReference((String)entry.getKey(), p);
						DebugRendererInfoManager.sendStructureStart(iWorld, structureStart);
					}
				}
			}
		}
	}

	public abstract void populateNoise(IWorld iWorld, Chunk chunk);

	public int getSeaLevel() {
		return 63;
	}

	public abstract int getHeightOnGround(int i, int j, Heightmap.Type type);

	public int method_20402(int i, int j, Heightmap.Type type) {
		return this.getHeightOnGround(i, j, type);
	}

	public int getHeightInGround(int i, int j, Heightmap.Type type) {
		return this.getHeightOnGround(i, j, type) - 1;
	}
}

package net.minecraft.world.gen.chunk;

import java.util.BitSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityCategory;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public abstract class ChunkGenerator<C extends ChunkGeneratorConfig> {
	protected final IWorld world;
	protected final long seed;
	protected final BiomeSource biomeSource;
	protected final C config;

	public ChunkGenerator(IWorld world, BiomeSource biomeSource, C config) {
		this.world = world;
		this.seed = world.getSeed();
		this.biomeSource = biomeSource;
		this.config = config;
	}

	public void populateBiomes(Chunk chunk) {
		ChunkPos chunkPos = chunk.getPos();
		((ProtoChunk)chunk).setBiomes(new BiomeArray(chunkPos, this.biomeSource));
	}

	protected Biome getDecorationBiome(BiomeAccess biomeAccess, BlockPos pos) {
		return biomeAccess.getBiome(pos);
	}

	public void carve(BiomeAccess biomeAccess, Chunk chunk, GenerationStep.Carver carver) {
		ChunkRandom chunkRandom = new ChunkRandom();
		int i = 8;
		ChunkPos chunkPos = chunk.getPos();
		int j = chunkPos.x;
		int k = chunkPos.z;
		Biome biome = this.getDecorationBiome(biomeAccess, chunkPos.getCenterBlockPos());
		BitSet bitSet = chunk.getCarvingMask(carver);

		for (int l = j - 8; l <= j + 8; l++) {
			for (int m = k - 8; m <= k + 8; m++) {
				List<ConfiguredCarver<?>> list = biome.getCarversForStep(carver);
				ListIterator<ConfiguredCarver<?>> listIterator = list.listIterator();

				while (listIterator.hasNext()) {
					int n = listIterator.nextIndex();
					ConfiguredCarver<?> configuredCarver = (ConfiguredCarver<?>)listIterator.next();
					chunkRandom.setCarverSeed(this.seed + (long)n, l, m);
					if (configuredCarver.shouldCarve(chunkRandom, l, m)) {
						configuredCarver.carve(chunk, blockPos -> this.getDecorationBiome(biomeAccess, blockPos), chunkRandom, this.getSeaLevel(), l, m, j, k, bitSet);
					}
				}
			}
		}
	}

	@Nullable
	public BlockPos locateStructure(ServerWorld serverWorld, String id, BlockPos center, int radius, boolean skipExistingChunks) {
		StructureFeature<?> structureFeature = (StructureFeature<?>)Feature.STRUCTURES.get(id.toLowerCase(Locale.ROOT));
		return structureFeature != null ? structureFeature.locateStructure(serverWorld, this, center, radius, skipExistingChunks) : null;
	}

	public void generateFeatures(ChunkRegion chunkRegion, StructureAccessor structureAccessor) {
		int i = chunkRegion.getCenterChunkX();
		int j = chunkRegion.getCenterChunkZ();
		int k = i * 16;
		int l = j * 16;
		BlockPos blockPos = new BlockPos(k, 0, l);
		Biome biome = this.getDecorationBiome(chunkRegion.getBiomeAccess(), blockPos.add(8, 8, 8));
		ChunkRandom chunkRandom = new ChunkRandom();
		long m = chunkRandom.setPopulationSeed(chunkRegion.getSeed(), k, l);

		for (GenerationStep.Feature feature : GenerationStep.Feature.values()) {
			try {
				biome.generateFeatureStep(feature, structureAccessor, this, chunkRegion, m, chunkRandom, blockPos);
			} catch (Exception var18) {
				CrashReport crashReport = CrashReport.create(var18, "Biome decoration");
				crashReport.addElement("Generation").add("CenterX", i).add("CenterZ", j).add("Step", feature).add("Seed", m).add("Biome", Registry.BIOME.getId(biome));
				throw new CrashException(crashReport);
			}
		}
	}

	public abstract void buildSurface(ChunkRegion region, Chunk chunk);

	public void populateEntities(ChunkRegion region) {
	}

	public C getConfig() {
		return this.config;
	}

	public abstract int getSpawnHeight();

	public void spawnEntities(ServerWorld world, boolean spawnMonsters, boolean spawnAnimals) {
	}

	public boolean hasStructure(Biome biome, StructureFeature<? extends FeatureConfig> feature) {
		return biome.hasStructureFeature(feature);
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

	public List<Biome.SpawnEntry> getEntitySpawnList(StructureAccessor structureAccessor, EntityCategory entityCategory, BlockPos blockPos) {
		return this.world.getBiome(blockPos).getEntitySpawnList(entityCategory);
	}

	public void setStructureStarts(
		StructureAccessor structureAccessor, BiomeAccess biomeAccess, Chunk chunk, ChunkGenerator<?> chunkGenerator, StructureManager structureManager
	) {
		for (StructureFeature<?> structureFeature : Feature.STRUCTURES.values()) {
			if (chunkGenerator.getBiomeSource().hasStructureFeature(structureFeature)) {
				StructureStart structureStart = structureAccessor.getStructureStart(ChunkSectionPos.from(chunk.getPos(), 0), structureFeature, chunk);
				int i = structureStart != null ? structureStart.getReferences() : 0;
				ChunkRandom chunkRandom = new ChunkRandom();
				ChunkPos chunkPos = chunk.getPos();
				StructureStart structureStart2 = StructureStart.DEFAULT;
				Biome biome = biomeAccess.getBiome(new BlockPos(chunkPos.getStartX() + 9, 0, chunkPos.getStartZ() + 9));
				if (structureFeature.shouldStartAt(biomeAccess, chunkGenerator, chunkRandom, chunkPos.x, chunkPos.z, biome)) {
					StructureStart structureStart3 = structureFeature.getStructureStartFactory()
						.create(structureFeature, chunkPos.x, chunkPos.z, BlockBox.empty(), i, chunkGenerator.getSeed());
					structureStart3.initialize(this, structureManager, chunkPos.x, chunkPos.z, biome);
					structureStart2 = structureStart3.hasChildren() ? structureStart3 : StructureStart.DEFAULT;
				}

				structureAccessor.setStructureStart(ChunkSectionPos.from(chunk.getPos(), 0), structureFeature, structureStart2, chunk);
			}
		}
	}

	public void addStructureReferences(IWorld world, StructureAccessor structureAccessor, Chunk chunk) {
		int i = 8;
		int j = chunk.getPos().x;
		int k = chunk.getPos().z;
		int l = j << 4;
		int m = k << 4;
		ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(chunk.getPos(), 0);

		for (int n = j - 8; n <= j + 8; n++) {
			for (int o = k - 8; o <= k + 8; o++) {
				long p = ChunkPos.toLong(n, o);

				for (Entry<String, StructureStart> entry : world.getChunk(n, o).getStructureStarts().entrySet()) {
					StructureStart structureStart = (StructureStart)entry.getValue();
					if (structureStart != StructureStart.DEFAULT && structureStart.getBoundingBox().intersectsXZ(l, m, l + 15, m + 15)) {
						structureAccessor.addStructureReference(chunkSectionPos, structureStart.getFeature(), p, chunk);
						DebugInfoSender.sendStructureStart(world, structureStart);
					}
				}
			}
		}
	}

	public abstract void populateNoise(IWorld world, StructureAccessor structureAccessor, Chunk chunk);

	public int getSeaLevel() {
		return 63;
	}

	public abstract int getHeight(int x, int z, Heightmap.Type heightmapType);

	public abstract BlockView getColumnSample(int x, int z);

	public int getHeightOnGround(int x, int z, Heightmap.Type heightmapType) {
		return this.getHeight(x, z, heightmapType);
	}

	public int getHeightInGround(int x, int z, Heightmap.Type heightmapType) {
		return this.getHeight(x, z, heightmapType) - 1;
	}
}

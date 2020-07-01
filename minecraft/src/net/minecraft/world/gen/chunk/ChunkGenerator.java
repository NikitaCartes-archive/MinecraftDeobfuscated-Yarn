package net.minecraft.world.gen.chunk;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.WorldAccess;
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
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.StructureFeature;

/**
 * In charge of shaping, adding biome specific surface blocks, and carving chunks,
 * as well as populating the generated chunks with {@linkplain net.minecraft.world.gen.feature.Feature features} and {@linkplain net.minecraft.entity.Entity entities}.
 * Biome placement starts here, however all vanilla and most modded chunk generators delegate this to a {@linkplain net.minecraft.world.biome.source.BiomeSource biome source}.
 */
public abstract class ChunkGenerator {
	public static final Codec<ChunkGenerator> field_24746 = Registry.CHUNK_GENERATOR.dispatchStable(ChunkGenerator::method_28506, Function.identity());
	protected final BiomeSource biomeSource;
	protected final BiomeSource field_24747;
	private final StructuresConfig config;
	private final long field_24748;
	private final List<ChunkPos> field_24749 = Lists.<ChunkPos>newArrayList();

	public ChunkGenerator(BiomeSource biomeSource, StructuresConfig structuresConfig) {
		this(biomeSource, biomeSource, structuresConfig, 0L);
	}

	public ChunkGenerator(BiomeSource biomeSource, BiomeSource biomeSource2, StructuresConfig structuresConfig, long l) {
		this.biomeSource = biomeSource;
		this.field_24747 = biomeSource2;
		this.config = structuresConfig;
		this.field_24748 = l;
	}

	private void method_28509() {
		if (this.field_24749.isEmpty()) {
			StrongholdConfig strongholdConfig = this.config.getStronghold();
			if (strongholdConfig != null && strongholdConfig.getCount() != 0) {
				List<Biome> list = Lists.<Biome>newArrayList();

				for (Biome biome : this.biomeSource.getBiomes()) {
					if (biome.hasStructureFeature(StructureFeature.STRONGHOLD)) {
						list.add(biome);
					}
				}

				int i = strongholdConfig.getDistance();
				int j = strongholdConfig.getCount();
				int k = strongholdConfig.getSpread();
				Random random = new Random();
				random.setSeed(this.field_24748);
				double d = random.nextDouble() * Math.PI * 2.0;
				int l = 0;
				int m = 0;

				for (int n = 0; n < j; n++) {
					double e = (double)(4 * i + i * m * 6) + (random.nextDouble() - 0.5) * (double)i * 2.5;
					int o = (int)Math.round(Math.cos(d) * e);
					int p = (int)Math.round(Math.sin(d) * e);
					BlockPos blockPos = this.biomeSource.locateBiome((o << 4) + 8, 0, (p << 4) + 8, 112, list, random);
					if (blockPos != null) {
						o = blockPos.getX() >> 4;
						p = blockPos.getZ() >> 4;
					}

					this.field_24749.add(new ChunkPos(o, p));
					d += (Math.PI * 2) / (double)k;
					if (++l == k) {
						m++;
						l = 0;
						k += 2 * k / (m + 1);
						k = Math.min(k, j - n);
						d += random.nextDouble() * Math.PI * 2.0;
					}
				}
			}
		}
	}

	protected abstract Codec<? extends ChunkGenerator> method_28506();

	@Environment(EnvType.CLIENT)
	public abstract ChunkGenerator withSeed(long seed);

	public void populateBiomes(Chunk chunk) {
		ChunkPos chunkPos = chunk.getPos();
		((ProtoChunk)chunk).setBiomes(new BiomeArray(chunkPos, this.field_24747));
	}

	public void carve(long seed, BiomeAccess access, Chunk chunk, GenerationStep.Carver carver) {
		BiomeAccess biomeAccess = access.withSource(this.biomeSource);
		ChunkRandom chunkRandom = new ChunkRandom();
		int i = 8;
		ChunkPos chunkPos = chunk.getPos();
		int j = chunkPos.x;
		int k = chunkPos.z;
		Biome biome = this.biomeSource.getBiomeForNoiseGen(chunkPos.x << 2, 0, chunkPos.z << 2);
		BitSet bitSet = ((ProtoChunk)chunk).getOrCreateCarvingMask(carver);

		for (int l = j - 8; l <= j + 8; l++) {
			for (int m = k - 8; m <= k + 8; m++) {
				List<ConfiguredCarver<?>> list = biome.getCarversForStep(carver);
				ListIterator<ConfiguredCarver<?>> listIterator = list.listIterator();

				while (listIterator.hasNext()) {
					int n = listIterator.nextIndex();
					ConfiguredCarver<?> configuredCarver = (ConfiguredCarver<?>)listIterator.next();
					chunkRandom.setCarverSeed(seed + (long)n, l, m);
					if (configuredCarver.shouldCarve(chunkRandom, l, m)) {
						configuredCarver.carve(chunk, biomeAccess::getBiome, chunkRandom, this.getSeaLevel(), l, m, j, k, bitSet);
					}
				}
			}
		}
	}

	@Nullable
	public BlockPos locateStructure(ServerWorld world, StructureFeature<?> feature, BlockPos center, int radius, boolean skipExistingChunks) {
		if (!this.biomeSource.hasStructureFeature(feature)) {
			return null;
		} else if (feature == StructureFeature.STRONGHOLD) {
			this.method_28509();
			BlockPos blockPos = null;
			double d = Double.MAX_VALUE;
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (ChunkPos chunkPos : this.field_24749) {
				mutable.set((chunkPos.x << 4) + 8, 32, (chunkPos.z << 4) + 8);
				double e = mutable.getSquaredDistance(center);
				if (blockPos == null) {
					blockPos = new BlockPos(mutable);
					d = e;
				} else if (e < d) {
					blockPos = new BlockPos(mutable);
					d = e;
				}
			}

			return blockPos;
		} else {
			return feature.locateStructure(world, world.getStructureAccessor(), center, radius, skipExistingChunks, world.getSeed(), this.config.method_28600(feature));
		}
	}

	public void generateFeatures(ChunkRegion region, StructureAccessor accessor) {
		int i = region.getCenterChunkX();
		int j = region.getCenterChunkZ();
		int k = i * 16;
		int l = j * 16;
		BlockPos blockPos = new BlockPos(k, 0, l);
		Biome biome = this.biomeSource.getBiomeForNoiseGen((i << 2) + 2, 2, (j << 2) + 2);
		ChunkRandom chunkRandom = new ChunkRandom();
		long m = chunkRandom.setPopulationSeed(region.getSeed(), k, l);

		for (GenerationStep.Feature feature : GenerationStep.Feature.values()) {
			try {
				biome.generateFeatureStep(feature, accessor, this, region, m, chunkRandom, blockPos);
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

	public StructuresConfig getConfig() {
		return this.config;
	}

	public int getSpawnHeight() {
		return 64;
	}

	public BiomeSource getBiomeSource() {
		return this.field_24747;
	}

	public int getMaxY() {
		return 256;
	}

	public List<Biome.SpawnEntry> getEntitySpawnList(Biome biome, StructureAccessor accessor, SpawnGroup group, BlockPos pos) {
		return biome.getEntitySpawnList(group);
	}

	public void setStructureStarts(StructureAccessor structureAccessor, Chunk chunk, StructureManager structureManager, long l) {
		ChunkPos chunkPos = chunk.getPos();
		Biome biome = this.biomeSource.getBiomeForNoiseGen((chunkPos.x << 2) + 2, 0, (chunkPos.z << 2) + 2);
		this.method_28508(DefaultBiomeFeatures.STRONGHOLD, structureAccessor, chunk, structureManager, l, chunkPos, biome);

		for (ConfiguredStructureFeature<?, ?> configuredStructureFeature : biome.method_28413()) {
			this.method_28508(configuredStructureFeature, structureAccessor, chunk, structureManager, l, chunkPos, biome);
		}
	}

	private void method_28508(
		ConfiguredStructureFeature<?, ?> configuredStructureFeature,
		StructureAccessor structureAccessor,
		Chunk chunk,
		StructureManager structureManager,
		long l,
		ChunkPos chunkPos,
		Biome biome
	) {
		StructureStart<?> structureStart = structureAccessor.getStructureStart(ChunkSectionPos.from(chunk.getPos(), 0), configuredStructureFeature.feature, chunk);
		int i = structureStart != null ? structureStart.getReferences() : 0;
		StructureStart<?> structureStart2 = configuredStructureFeature.method_28622(
			this, this.biomeSource, structureManager, l, chunkPos, biome, i, this.config.method_28600(configuredStructureFeature.feature)
		);
		structureAccessor.setStructureStart(ChunkSectionPos.from(chunk.getPos(), 0), configuredStructureFeature.feature, structureStart2, chunk);
	}

	public void addStructureReferences(ServerWorldAccess serverWorldAccess, StructureAccessor accessor, Chunk chunk) {
		int i = 8;
		int j = chunk.getPos().x;
		int k = chunk.getPos().z;
		int l = j << 4;
		int m = k << 4;
		ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(chunk.getPos(), 0);

		for (int n = j - 8; n <= j + 8; n++) {
			for (int o = k - 8; o <= k + 8; o++) {
				long p = ChunkPos.toLong(n, o);

				for (StructureStart<?> structureStart : serverWorldAccess.getChunk(n, o).getStructureStarts().values()) {
					try {
						if (structureStart != StructureStart.DEFAULT && structureStart.getBoundingBox().intersectsXZ(l, m, l + 15, m + 15)) {
							accessor.addStructureReference(chunkSectionPos, structureStart.getFeature(), p, chunk);
							DebugInfoSender.sendStructureStart(serverWorldAccess, structureStart);
						}
					} catch (Exception var19) {
						CrashReport crashReport = CrashReport.create(var19, "Generating structure reference");
						CrashReportSection crashReportSection = crashReport.addElement("Structure");
						crashReportSection.add("Id", (CrashCallable<String>)(() -> Registry.STRUCTURE_FEATURE.getId(structureStart.getFeature()).toString()));
						crashReportSection.add("Name", (CrashCallable<String>)(() -> structureStart.getFeature().getName()));
						crashReportSection.add("Class", (CrashCallable<String>)(() -> structureStart.getFeature().getClass().getCanonicalName()));
						throw new CrashException(crashReport);
					}
				}
			}
		}
	}

	/**
	 * Generates the base shape of the chunk out of the basic block states as decided by this chunk generator's config.
	 */
	public abstract void populateNoise(WorldAccess world, StructureAccessor accessor, Chunk chunk);

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

	public boolean method_28507(ChunkPos chunkPos) {
		this.method_28509();
		return this.field_24749.contains(chunkPos);
	}

	static {
		Registry.register(Registry.CHUNK_GENERATOR, "noise", SurfaceChunkGenerator.CODEC);
		Registry.register(Registry.CHUNK_GENERATOR, "flat", FlatChunkGenerator.field_24769);
		Registry.register(Registry.CHUNK_GENERATOR, "debug", DebugChunkGenerator.field_24768);
	}
}

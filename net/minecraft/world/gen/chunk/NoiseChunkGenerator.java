/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.chunk;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.SharedConstants;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.class_6748;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Util;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.dynamic.RegistryLookupCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.gen.BlockSource;
import net.minecraft.world.gen.ChainedBlockSource;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.NoiseColumnSampler;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.StructureWeightSampler;
import net.minecraft.world.gen.carver.CarverContext;
import net.minecraft.world.gen.carver.CarvingMask;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.chunk.AquiferSampler;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.ChunkNoiseSampler;
import net.minecraft.world.gen.chunk.GenerationShapeConfig;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.feature.NetherFortressFeature;
import net.minecraft.world.gen.feature.OceanMonumentFeature;
import net.minecraft.world.gen.feature.PillagerOutpostFeature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.SwampHutFeature;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.ChunkRandom;
import net.minecraft.world.gen.random.RandomSeed;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.tick.OrderedTick;
import org.jetbrains.annotations.Nullable;

public final class NoiseChunkGenerator
extends ChunkGenerator {
    public static final Codec<NoiseChunkGenerator> CODEC = RecordCodecBuilder.create(instance -> instance.group(RegistryLookupCodec.of(Registry.NOISE_WORLDGEN).forGetter(noiseChunkGenerator -> noiseChunkGenerator.noiseRegistry), ((MapCodec)BiomeSource.CODEC.fieldOf("biome_source")).forGetter(noiseChunkGenerator -> noiseChunkGenerator.populationSource), ((MapCodec)Codec.LONG.fieldOf("seed")).stable().forGetter(noiseChunkGenerator -> noiseChunkGenerator.seed), ((MapCodec)ChunkGeneratorSettings.REGISTRY_CODEC.fieldOf("settings")).forGetter(noiseChunkGenerator -> noiseChunkGenerator.settings)).apply((Applicative<NoiseChunkGenerator, ?>)instance, instance.stable(NoiseChunkGenerator::new)));
    private static final BlockState AIR = Blocks.AIR.getDefaultState();
    private static final BlockState[] EMPTY = new BlockState[0];
    private final int verticalNoiseResolution;
    private final int horizontalNoiseResolution;
    private final int noiseSizeX;
    private final int noiseSizeY;
    private final int noiseSizeZ;
    protected final BlockState defaultBlock;
    private final Registry<DoublePerlinNoiseSampler.NoiseParameters> noiseRegistry;
    private final long seed;
    protected final Supplier<ChunkGeneratorSettings> settings;
    private final int worldHeight;
    private final NoiseColumnSampler noiseColumnSampler;
    private final SurfaceBuilder surfaceBuilder;
    private final BlockSource blockStateSampler;
    private final AquiferSampler.FluidLevelSampler fluidLevelSampler;

    public NoiseChunkGenerator(Registry<DoublePerlinNoiseSampler.NoiseParameters> noiseRegistry, BiomeSource biomeSource, long seed, Supplier<ChunkGeneratorSettings> supplier) {
        this(noiseRegistry, biomeSource, biomeSource, seed, supplier);
    }

    private NoiseChunkGenerator(Registry<DoublePerlinNoiseSampler.NoiseParameters> noiseRegistry, BiomeSource biomeSource, BiomeSource biomeSource2, long seed, Supplier<ChunkGeneratorSettings> settings) {
        super(biomeSource, biomeSource2, settings.get().getStructuresConfig(), seed);
        this.noiseRegistry = noiseRegistry;
        this.seed = seed;
        ChunkGeneratorSettings chunkGeneratorSettings = settings.get();
        this.settings = settings;
        GenerationShapeConfig generationShapeConfig = chunkGeneratorSettings.getGenerationShapeConfig();
        this.worldHeight = generationShapeConfig.height();
        this.verticalNoiseResolution = BiomeCoords.toBlock(generationShapeConfig.verticalSize());
        this.horizontalNoiseResolution = BiomeCoords.toBlock(generationShapeConfig.horizontalSize());
        this.defaultBlock = chunkGeneratorSettings.getDefaultBlock();
        this.noiseSizeX = 16 / this.horizontalNoiseResolution;
        this.noiseSizeY = generationShapeConfig.height() / this.verticalNoiseResolution;
        this.noiseSizeZ = 16 / this.horizontalNoiseResolution;
        this.noiseColumnSampler = new NoiseColumnSampler(this.horizontalNoiseResolution, this.verticalNoiseResolution, this.noiseSizeY, generationShapeConfig, chunkGeneratorSettings.hasNoiseCaves(), seed, noiseRegistry, chunkGeneratorSettings.getRandomProvider());
        ImmutableList.Builder builder = ImmutableList.builder();
        builder.add(ChunkNoiseSampler::sampleInitialNoiseBlockState);
        builder.add(ChunkNoiseSampler::sampleOreVeins);
        this.blockStateSampler = new ChainedBlockSource((List<BlockSource>)((Object)builder.build()));
        AquiferSampler.FluidLevel fluidLevel = new AquiferSampler.FluidLevel(-54, Blocks.LAVA.getDefaultState());
        AquiferSampler.FluidLevel fluidLevel2 = new AquiferSampler.FluidLevel(chunkGeneratorSettings.getSeaLevel(), chunkGeneratorSettings.getDefaultFluid());
        AquiferSampler.FluidLevel fluidLevel3 = new AquiferSampler.FluidLevel(chunkGeneratorSettings.getGenerationShapeConfig().minimumY() - 1, Blocks.AIR.getDefaultState());
        this.fluidLevelSampler = (x, y, z) -> {
            if (y < -54) {
                return fluidLevel;
            }
            return fluidLevel2;
        };
        this.surfaceBuilder = new SurfaceBuilder(this.noiseColumnSampler, noiseRegistry, this.defaultBlock, chunkGeneratorSettings.getSeaLevel(), seed, chunkGeneratorSettings.getRandomProvider());
    }

    @Override
    public CompletableFuture<Chunk> populateBiomes(Executor executor, class_6748 arg, StructureAccessor structureAccessor, Chunk chunk) {
        return CompletableFuture.supplyAsync(Util.debugSupplier("init_biomes", () -> {
            this.method_38327(arg, structureAccessor, chunk);
            return chunk;
        }), Util.getMainWorkerExecutor());
    }

    private void method_38327(class_6748 arg, StructureAccessor world, Chunk chunk) {
        ChunkPos chunkPos = chunk.getPos();
        int i2 = Math.max(this.settings.get().getGenerationShapeConfig().minimumY(), chunk.getBottomY());
        int j2 = Math.min(this.settings.get().getGenerationShapeConfig().minimumY() + this.settings.get().getGenerationShapeConfig().height(), chunk.getTopY());
        int k = MathHelper.floorDiv(i2, this.verticalNoiseResolution);
        int l = MathHelper.floorDiv(j2 - i2, this.verticalNoiseResolution);
        ChunkNoiseSampler chunkNoiseSampler = chunk.getOrCreateChunkNoiseSampler(k, l, chunkPos.getStartX(), chunkPos.getStartZ(), this.horizontalNoiseResolution, this.verticalNoiseResolution, this.noiseColumnSampler, () -> new StructureWeightSampler(world, chunk), this.settings, this.fluidLevelSampler, arg);
        chunk.method_38257(this.biomeSource, (i, y, j) -> this.noiseColumnSampler.method_39329(i, y, j, chunkNoiseSampler.createMultiNoisePoint(i, j)));
    }

    @Override
    public MultiNoiseUtil.MultiNoiseSampler getMultiNoiseSampler() {
        return this.noiseColumnSampler;
    }

    @Override
    protected Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }

    @Override
    public ChunkGenerator withSeed(long seed) {
        return new NoiseChunkGenerator(this.noiseRegistry, this.populationSource.withSeed(seed), seed, this.settings);
    }

    public boolean matchesSettings(long seed, RegistryKey<ChunkGeneratorSettings> settingsKey) {
        return this.seed == seed && this.settings.get().equals(settingsKey);
    }

    @Override
    public int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world) {
        int i = Math.max(this.settings.get().getGenerationShapeConfig().minimumY(), world.getBottomY());
        int j = Math.min(this.settings.get().getGenerationShapeConfig().minimumY() + this.settings.get().getGenerationShapeConfig().height(), world.getTopY());
        int k = MathHelper.floorDiv(i, this.verticalNoiseResolution);
        int l = MathHelper.floorDiv(j - i, this.verticalNoiseResolution);
        if (l <= 0) {
            return world.getBottomY();
        }
        return this.sampleHeightmap(x, z, null, heightmap.getBlockPredicate(), k, l).orElse(world.getBottomY());
    }

    @Override
    public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world) {
        int i = Math.max(this.settings.get().getGenerationShapeConfig().minimumY(), world.getBottomY());
        int j = Math.min(this.settings.get().getGenerationShapeConfig().minimumY() + this.settings.get().getGenerationShapeConfig().height(), world.getTopY());
        int k = MathHelper.floorDiv(i, this.verticalNoiseResolution);
        int l = MathHelper.floorDiv(j - i, this.verticalNoiseResolution);
        if (l <= 0) {
            return new VerticalBlockSample(i, EMPTY);
        }
        BlockState[] blockStates = new BlockState[l * this.verticalNoiseResolution];
        this.sampleHeightmap(x, z, blockStates, null, k, l);
        return new VerticalBlockSample(i, blockStates);
    }

    private OptionalInt sampleHeightmap(int x2, int z2, @Nullable BlockState[] states, @Nullable Predicate<BlockState> predicate, int minimumY, int height) {
        int i = Math.floorDiv(x2, this.horizontalNoiseResolution);
        int j = Math.floorDiv(z2, this.horizontalNoiseResolution);
        int k = Math.floorMod(x2, this.horizontalNoiseResolution);
        int l = Math.floorMod(z2, this.horizontalNoiseResolution);
        int m = i * this.horizontalNoiseResolution;
        int n = j * this.horizontalNoiseResolution;
        double d = (double)k / (double)this.horizontalNoiseResolution;
        double e = (double)l / (double)this.horizontalNoiseResolution;
        ChunkNoiseSampler chunkNoiseSampler = new ChunkNoiseSampler(this.horizontalNoiseResolution, this.verticalNoiseResolution, 1, height, minimumY, this.noiseColumnSampler, m, n, (x, y, z) -> 0.0, this.settings, this.fluidLevelSampler, class_6748.method_39336());
        chunkNoiseSampler.sampleStartNoise();
        chunkNoiseSampler.sampleEndNoise(0);
        for (int o = height - 1; o >= 0; --o) {
            chunkNoiseSampler.sampleNoiseCorners(o, 0);
            for (int p = this.verticalNoiseResolution - 1; p >= 0; --p) {
                BlockState blockState2;
                int q = (minimumY + o) * this.verticalNoiseResolution + p;
                double f = (double)p / (double)this.verticalNoiseResolution;
                chunkNoiseSampler.sampleNoiseY(f);
                chunkNoiseSampler.sampleNoiseX(d);
                chunkNoiseSampler.sampleNoise(e);
                BlockState blockState = this.blockStateSampler.apply(chunkNoiseSampler, x2, q, z2);
                BlockState blockState3 = blockState2 = blockState == null ? this.defaultBlock : blockState;
                if (states != null) {
                    int r = o * this.verticalNoiseResolution + p;
                    states[r] = blockState2;
                }
                if (predicate == null || !predicate.test(blockState2)) continue;
                return OptionalInt.of(q + 1);
            }
        }
        return OptionalInt.empty();
    }

    @Override
    public void buildSurface(ChunkRegion region, StructureAccessor structures, Chunk chunk) {
        ChunkPos chunkPos = chunk.getPos();
        if (SharedConstants.method_37896(chunkPos)) {
            return;
        }
        int i = chunkPos.getStartX();
        int j = chunkPos.getStartZ();
        int k = Math.max(this.settings.get().getGenerationShapeConfig().minimumY(), chunk.getBottomY());
        int l = Math.min(this.settings.get().getGenerationShapeConfig().minimumY() + this.settings.get().getGenerationShapeConfig().height(), chunk.getTopY());
        int m = MathHelper.floorDiv(k, this.verticalNoiseResolution);
        int n = MathHelper.floorDiv(l - k, this.verticalNoiseResolution);
        HeightContext heightContext = new HeightContext(this, region);
        ChunkNoiseSampler chunkNoiseSampler = chunk.getOrCreateChunkNoiseSampler(m, n, i, j, this.horizontalNoiseResolution, this.verticalNoiseResolution, this.noiseColumnSampler, () -> new StructureWeightSampler(structures, chunk), this.settings, this.fluidLevelSampler, class_6748.method_39342(region));
        this.surfaceBuilder.buildSurface(region.getBiomeAccess(), region.getRegistryManager().get(Registry.BIOME_KEY), this.settings.get().usesLegacyRandom(), heightContext, chunk, chunkNoiseSampler, this.settings.get().getSurfaceRule());
    }

    @Override
    public void carve(ChunkRegion chunkRegion, long seed, BiomeAccess biomeAccess, StructureAccessor structureAccessor, Chunk chunk, GenerationStep.Carver generationStep) {
        BiomeAccess biomeAccess2 = biomeAccess.withSource((i, j, k) -> this.populationSource.getBiome(i, j, k, this.getMultiNoiseSampler()));
        ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(RandomSeed.getSeed()));
        int i2 = 8;
        ChunkPos chunkPos = chunk.getPos();
        CarverContext carverContext = new CarverContext(this, chunkRegion.getRegistryManager(), chunk);
        ChunkPos chunkPos2 = chunk.getPos();
        GenerationShapeConfig generationShapeConfig = this.settings.get().getGenerationShapeConfig();
        int j2 = Math.max(generationShapeConfig.minimumY(), chunk.getBottomY());
        int k2 = Math.min(generationShapeConfig.minimumY() + generationShapeConfig.height(), chunk.getTopY());
        int l = MathHelper.floorDiv(j2, this.verticalNoiseResolution);
        int m = MathHelper.floorDiv(k2 - j2, this.verticalNoiseResolution);
        ChunkNoiseSampler chunkNoiseSampler = chunk.getOrCreateChunkNoiseSampler(l, m, chunkPos2.getStartX(), chunkPos2.getStartZ(), this.horizontalNoiseResolution, this.verticalNoiseResolution, this.noiseColumnSampler, () -> new StructureWeightSampler(structureAccessor, chunk), this.settings, this.fluidLevelSampler, class_6748.method_39342(chunkRegion));
        AquiferSampler aquiferSampler = chunkNoiseSampler.getAquiferSampler();
        CarvingMask carvingMask = ((ProtoChunk)chunk).getOrCreateCarvingMask(generationStep);
        for (int n = -8; n <= 8; ++n) {
            for (int o = -8; o <= 8; ++o) {
                ChunkPos chunkPos3 = new ChunkPos(chunkPos.x + n, chunkPos.z + o);
                Chunk chunk2 = chunkRegion.getChunk(chunkPos3.x, chunkPos3.z);
                GenerationSettings generationSettings = chunk2.method_38258(() -> this.populationSource.getBiome(BiomeCoords.fromBlock(chunkPos3.getStartX()), 0, BiomeCoords.fromBlock(chunkPos3.getStartZ()), this.getMultiNoiseSampler())).getGenerationSettings();
                List<Supplier<ConfiguredCarver<?>>> list = generationSettings.getCarversForStep(generationStep);
                ListIterator<Supplier<ConfiguredCarver<?>>> listIterator = list.listIterator();
                while (listIterator.hasNext()) {
                    int p = listIterator.nextIndex();
                    ConfiguredCarver<?> configuredCarver = listIterator.next().get();
                    chunkRandom.setCarverSeed(seed + (long)p, chunkPos3.x, chunkPos3.z);
                    if (!configuredCarver.shouldCarve(chunkRandom)) continue;
                    configuredCarver.carve(carverContext, chunk, biomeAccess2::getBiome, chunkRandom, aquiferSampler, chunkPos3, carvingMask);
                }
            }
        }
    }

    @Override
    public CompletableFuture<Chunk> populateNoise(Executor executor, class_6748 arg, StructureAccessor structureAccessor, Chunk chunk2) {
        GenerationShapeConfig generationShapeConfig = this.settings.get().getGenerationShapeConfig();
        int i = Math.max(generationShapeConfig.minimumY(), chunk2.getHeightLimitView().getBottomY());
        int j = Math.min(generationShapeConfig.minimumY() + generationShapeConfig.height(), chunk2.getHeightLimitView().getTopY());
        int k = MathHelper.floorDiv(i, this.verticalNoiseResolution);
        int l = MathHelper.floorDiv(j - i, this.verticalNoiseResolution);
        if (l <= 0) {
            return CompletableFuture.completedFuture(chunk2);
        }
        int m = chunk2.getSectionIndex(l * this.verticalNoiseResolution - 1 + i);
        int n = chunk2.getSectionIndex(i);
        HashSet<ChunkSection> set = Sets.newHashSet();
        for (int o = m; o >= n; --o) {
            ChunkSection chunkSection = chunk2.getSection(o);
            chunkSection.lock();
            set.add(chunkSection);
        }
        return CompletableFuture.supplyAsync(Util.debugSupplier("wgen_fill_noise", () -> this.populateNoise(arg, structureAccessor, chunk2, k, l)), Util.getMainWorkerExecutor()).whenCompleteAsync((chunk, throwable) -> {
            for (ChunkSection chunkSection : set) {
                chunkSection.unlock();
            }
        }, executor);
    }

    private Chunk populateNoise(class_6748 arg, StructureAccessor structureAccessor, Chunk chunk, int i, int j) {
        Heightmap heightmap = chunk.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
        Heightmap heightmap2 = chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);
        ChunkPos chunkPos = chunk.getPos();
        int k = chunkPos.getStartX();
        int l = chunkPos.getStartZ();
        ChunkNoiseSampler chunkNoiseSampler = chunk.getOrCreateChunkNoiseSampler(i, j, k, l, this.horizontalNoiseResolution, this.verticalNoiseResolution, this.noiseColumnSampler, () -> new StructureWeightSampler(structureAccessor, chunk), this.settings, this.fluidLevelSampler, arg);
        AquiferSampler aquiferSampler = chunkNoiseSampler.getAquiferSampler();
        chunkNoiseSampler.sampleStartNoise();
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int m = 0; m < this.noiseSizeX; ++m) {
            chunkNoiseSampler.sampleEndNoise(m);
            for (int n = 0; n < this.noiseSizeZ; ++n) {
                ChunkSection chunkSection = chunk.getSection(chunk.countVerticalSections() - 1);
                for (int o = j - 1; o >= 0; --o) {
                    chunkNoiseSampler.sampleNoiseCorners(o, n);
                    for (int p = this.verticalNoiseResolution - 1; p >= 0; --p) {
                        int q = (i + o) * this.verticalNoiseResolution + p;
                        int r = q & 0xF;
                        int s = chunk.getSectionIndex(q);
                        if (chunk.getSectionIndex(chunkSection.getYOffset()) != s) {
                            chunkSection = chunk.getSection(s);
                        }
                        double d = (double)p / (double)this.verticalNoiseResolution;
                        chunkNoiseSampler.sampleNoiseY(d);
                        for (int t = 0; t < this.horizontalNoiseResolution; ++t) {
                            int u = k + m * this.horizontalNoiseResolution + t;
                            int v = u & 0xF;
                            double e = (double)t / (double)this.horizontalNoiseResolution;
                            chunkNoiseSampler.sampleNoiseX(e);
                            for (int w = 0; w < this.horizontalNoiseResolution; ++w) {
                                int x = l + n * this.horizontalNoiseResolution + w;
                                int y = x & 0xF;
                                double f = (double)w / (double)this.horizontalNoiseResolution;
                                chunkNoiseSampler.sampleNoise(f);
                                BlockState blockState = this.blockStateSampler.apply(chunkNoiseSampler, u, q, x);
                                if (blockState == null) {
                                    blockState = this.defaultBlock;
                                }
                                if ((blockState = this.method_38323(q, u, x, blockState)) == AIR || SharedConstants.method_37896(chunk.getPos())) continue;
                                if (blockState.getLuminance() != 0 && chunk instanceof ProtoChunk) {
                                    mutable.set(u, q, x);
                                    ((ProtoChunk)chunk).addLightSource(mutable);
                                }
                                chunkSection.setBlockState(v, r, y, blockState, false);
                                heightmap.trackUpdate(v, q, y, blockState);
                                heightmap2.trackUpdate(v, q, y, blockState);
                                if (!aquiferSampler.needsFluidTick() || blockState.getFluidState().isEmpty()) continue;
                                mutable.set(u, q, x);
                                chunk.getFluidTickScheduler().scheduleTick(OrderedTick.create(blockState.getFluidState().getFluid(), mutable, 0L));
                            }
                        }
                    }
                }
            }
            chunkNoiseSampler.swapBuffers();
        }
        return chunk;
    }

    private BlockState method_38323(int y, int x, int z, BlockState block) {
        return block;
    }

    @Override
    public int getWorldHeight() {
        return this.worldHeight;
    }

    @Override
    public int getSeaLevel() {
        return this.settings.get().getSeaLevel();
    }

    @Override
    public int getMinimumY() {
        return this.settings.get().getGenerationShapeConfig().minimumY();
    }

    @Override
    public Pool<SpawnSettings.SpawnEntry> getEntitySpawnList(Biome biome, StructureAccessor accessor, SpawnGroup group, BlockPos pos) {
        if (!accessor.hasStructureReferences(pos)) {
            return super.getEntitySpawnList(biome, accessor, group, pos);
        }
        if (accessor.method_38854(pos, StructureFeature.SWAMP_HUT).hasChildren()) {
            if (group == SpawnGroup.MONSTER) {
                return SwampHutFeature.MONSTER_SPAWNS;
            }
            if (group == SpawnGroup.CREATURE) {
                return SwampHutFeature.CREATURE_SPAWNS;
            }
        }
        if (group == SpawnGroup.MONSTER) {
            if (accessor.getStructureAt(pos, StructureFeature.PILLAGER_OUTPOST).hasChildren()) {
                return PillagerOutpostFeature.MONSTER_SPAWNS;
            }
            if (accessor.getStructureAt(pos, StructureFeature.MONUMENT).hasChildren()) {
                return OceanMonumentFeature.MONSTER_SPAWNS;
            }
            if (accessor.method_38854(pos, StructureFeature.FORTRESS).hasChildren()) {
                return NetherFortressFeature.MONSTER_SPAWNS;
            }
        }
        if ((group == SpawnGroup.UNDERGROUND_WATER_CREATURE || group == SpawnGroup.AXOLOTLS) && accessor.getStructureAt(pos, StructureFeature.MONUMENT).hasChildren()) {
            return SpawnSettings.EMPTY_ENTRY_POOL;
        }
        return super.getEntitySpawnList(biome, accessor, group, pos);
    }

    @Override
    public void populateEntities(ChunkRegion region) {
        if (this.settings.get().isMobGenerationDisabled()) {
            return;
        }
        ChunkPos chunkPos = region.getCenterPos();
        Biome biome = region.getBiome(chunkPos.getStartPos());
        ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(RandomSeed.getSeed()));
        chunkRandom.setPopulationSeed(region.getSeed(), chunkPos.getStartX(), chunkPos.getStartZ());
        SpawnHelper.populateEntities(region, biome, chunkPos, chunkRandom);
    }

    @Deprecated
    public Optional<BlockState> method_39041(CarverContext context, Function<BlockPos, Biome> function, Chunk chunk, BlockPos pos, boolean bl) {
        return this.surfaceBuilder.method_39110(this.settings.get().getSurfaceRule(), context, function, chunk, pos, bl);
    }
}


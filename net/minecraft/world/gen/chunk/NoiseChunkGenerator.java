/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.OptionalInt;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.DoubleFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Util;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.math.noise.InterpolatedNoiseSampler;
import net.minecraft.util.math.noise.NoiseSampler;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;
import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.gen.BlockSource;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.DeepslateBlockSource;
import net.minecraft.world.gen.NoiseCaveSampler;
import net.minecraft.world.gen.NoiseColumnSampler;
import net.minecraft.world.gen.NoiseInterpolator;
import net.minecraft.world.gen.OreVeinGenerator;
import net.minecraft.world.gen.SimpleRandom;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.StructureWeightSampler;
import net.minecraft.world.gen.WorldGenRandom;
import net.minecraft.world.gen.chunk.AquiferSampler;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.GenerationShapeConfig;
import net.minecraft.world.gen.chunk.NoodleCavesGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.chunk.WeightSampler;
import net.minecraft.world.gen.feature.StructureFeature;
import org.jetbrains.annotations.Nullable;

public final class NoiseChunkGenerator
extends ChunkGenerator {
    public static final Codec<NoiseChunkGenerator> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)BiomeSource.CODEC.fieldOf("biome_source")).forGetter(noiseChunkGenerator -> noiseChunkGenerator.populationSource), ((MapCodec)Codec.LONG.fieldOf("seed")).stable().forGetter(noiseChunkGenerator -> noiseChunkGenerator.seed), ((MapCodec)ChunkGeneratorSettings.REGISTRY_CODEC.fieldOf("settings")).forGetter(noiseChunkGenerator -> noiseChunkGenerator.settings)).apply((Applicative<NoiseChunkGenerator, ?>)instance, instance.stable(NoiseChunkGenerator::new)));
    private static final BlockState AIR = Blocks.AIR.getDefaultState();
    private static final BlockState[] EMPTY = new BlockState[0];
    private final int verticalNoiseResolution;
    private final int horizontalNoiseResolution;
    final int noiseSizeX;
    final int noiseSizeY;
    final int noiseSizeZ;
    private final NoiseSampler surfaceDepthNoise;
    private final DoublePerlinNoiseSampler edgeDensityNoise;
    private final DoublePerlinNoiseSampler fluidLevelNoise;
    private final DoublePerlinNoiseSampler fluidTypeNoise;
    protected final BlockState defaultBlock;
    protected final BlockState defaultFluid;
    private final long seed;
    protected final Supplier<ChunkGeneratorSettings> settings;
    private final int worldHeight;
    private final NoiseColumnSampler noiseColumnSampler;
    private final BlockSource deepslateSource;
    final OreVeinGenerator oreVeinGenerator;
    final NoodleCavesGenerator noodleCavesGenerator;

    public NoiseChunkGenerator(BiomeSource biomeSource, long seed, Supplier<ChunkGeneratorSettings> settings) {
        this(biomeSource, biomeSource, seed, settings);
    }

    private NoiseChunkGenerator(BiomeSource populationSource, BiomeSource biomeSource, long seed, Supplier<ChunkGeneratorSettings> settings) {
        super(populationSource, biomeSource, settings.get().getStructuresConfig(), seed);
        SimplexNoiseSampler simplexNoiseSampler;
        this.seed = seed;
        ChunkGeneratorSettings chunkGeneratorSettings = settings.get();
        this.settings = settings;
        GenerationShapeConfig generationShapeConfig = chunkGeneratorSettings.getGenerationShapeConfig();
        this.worldHeight = generationShapeConfig.getHeight();
        this.verticalNoiseResolution = BiomeCoords.toBlock(generationShapeConfig.getSizeVertical());
        this.horizontalNoiseResolution = BiomeCoords.toBlock(generationShapeConfig.getSizeHorizontal());
        this.defaultBlock = chunkGeneratorSettings.getDefaultBlock();
        this.defaultFluid = chunkGeneratorSettings.getDefaultFluid();
        this.noiseSizeX = 16 / this.horizontalNoiseResolution;
        this.noiseSizeY = generationShapeConfig.getHeight() / this.verticalNoiseResolution;
        this.noiseSizeZ = 16 / this.horizontalNoiseResolution;
        ChunkRandom chunkRandom = new ChunkRandom(seed);
        InterpolatedNoiseSampler interpolatedNoiseSampler = new InterpolatedNoiseSampler(chunkRandom);
        this.surfaceDepthNoise = generationShapeConfig.hasSimplexSurfaceNoise() ? new OctaveSimplexNoiseSampler((WorldGenRandom)chunkRandom, IntStream.rangeClosed(-3, 0)) : new OctavePerlinNoiseSampler((WorldGenRandom)chunkRandom, IntStream.rangeClosed(-3, 0));
        chunkRandom.skip(2620);
        OctavePerlinNoiseSampler octavePerlinNoiseSampler = new OctavePerlinNoiseSampler((WorldGenRandom)chunkRandom, IntStream.rangeClosed(-15, 0));
        if (generationShapeConfig.hasIslandNoiseOverride()) {
            ChunkRandom chunkRandom2 = new ChunkRandom(seed);
            chunkRandom2.skip(17292);
            simplexNoiseSampler = new SimplexNoiseSampler(chunkRandom2);
        } else {
            simplexNoiseSampler = null;
        }
        this.edgeDensityNoise = DoublePerlinNoiseSampler.create((WorldGenRandom)new SimpleRandom(chunkRandom.nextLong()), -3, 1.0);
        this.fluidLevelNoise = DoublePerlinNoiseSampler.create((WorldGenRandom)new SimpleRandom(chunkRandom.nextLong()), -3, 1.0, 0.0, 2.0);
        this.fluidTypeNoise = DoublePerlinNoiseSampler.create((WorldGenRandom)new SimpleRandom(chunkRandom.nextLong()), -1, 1.0, 0.0);
        WeightSampler weightSampler = chunkGeneratorSettings.hasNoiseCaves() ? new NoiseCaveSampler(chunkRandom, generationShapeConfig.getMinimumY() / this.verticalNoiseResolution) : WeightSampler.DEFAULT;
        this.noiseColumnSampler = new NoiseColumnSampler(populationSource, this.horizontalNoiseResolution, this.verticalNoiseResolution, this.noiseSizeY, generationShapeConfig, interpolatedNoiseSampler, simplexNoiseSampler, octavePerlinNoiseSampler, weightSampler);
        this.deepslateSource = new DeepslateBlockSource(seed, this.defaultBlock, Blocks.DEEPSLATE.getDefaultState(), chunkGeneratorSettings);
        this.oreVeinGenerator = new OreVeinGenerator(seed, this.defaultBlock, this.horizontalNoiseResolution, this.verticalNoiseResolution, chunkGeneratorSettings.getGenerationShapeConfig().getMinimumY());
        this.noodleCavesGenerator = new NoodleCavesGenerator(seed);
    }

    private boolean hasAquifers() {
        return this.settings.get().hasAquifers();
    }

    @Override
    protected Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }

    @Override
    public ChunkGenerator withSeed(long seed) {
        return new NoiseChunkGenerator(this.populationSource.withSeed(seed), seed, this.settings);
    }

    public boolean matchesSettings(long seed, RegistryKey<ChunkGeneratorSettings> settingsKey) {
        return this.seed == seed && this.settings.get().equals(settingsKey);
    }

    private double[] sampleNoiseColumn(int x, int z, int minY, int noiseSizeY) {
        double[] ds = new double[noiseSizeY + 1];
        this.sampleNoiseColumn(ds, x, z, minY, noiseSizeY);
        return ds;
    }

    private void sampleNoiseColumn(double[] buffer, int x, int z, int minY, int noiseSizeY) {
        GenerationShapeConfig generationShapeConfig = this.settings.get().getGenerationShapeConfig();
        this.noiseColumnSampler.sampleNoiseColumn(buffer, x, z, generationShapeConfig, this.getSeaLevel(), minY, noiseSizeY);
    }

    @Override
    public int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world) {
        int i = Math.max(this.settings.get().getGenerationShapeConfig().getMinimumY(), world.getBottomY());
        int j = Math.min(this.settings.get().getGenerationShapeConfig().getMinimumY() + this.settings.get().getGenerationShapeConfig().getHeight(), world.getTopY());
        int k = MathHelper.floorDiv(i, this.verticalNoiseResolution);
        int l = MathHelper.floorDiv(j - i, this.verticalNoiseResolution);
        if (l <= 0) {
            return world.getBottomY();
        }
        return this.sampleHeightmap(x, z, null, heightmap.getBlockPredicate(), k, l).orElse(world.getBottomY());
    }

    @Override
    public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world) {
        int i = Math.max(this.settings.get().getGenerationShapeConfig().getMinimumY(), world.getBottomY());
        int j = Math.min(this.settings.get().getGenerationShapeConfig().getMinimumY() + this.settings.get().getGenerationShapeConfig().getHeight(), world.getTopY());
        int k = MathHelper.floorDiv(i, this.verticalNoiseResolution);
        int l = MathHelper.floorDiv(j - i, this.verticalNoiseResolution);
        if (l <= 0) {
            return new VerticalBlockSample(i, EMPTY);
        }
        BlockState[] blockStates = new BlockState[l * this.verticalNoiseResolution];
        this.sampleHeightmap(x, z, blockStates, null, k, l);
        return new VerticalBlockSample(i, blockStates);
    }

    @Override
    public BlockSource getBlockSource() {
        return this.deepslateSource;
    }

    private OptionalInt sampleHeightmap(int x, int z, @Nullable BlockState[] states, @Nullable Predicate<BlockState> predicate, int minY, int noiseSizeY) {
        int i = ChunkSectionPos.getSectionCoord(x);
        int j = ChunkSectionPos.getSectionCoord(z);
        int k = Math.floorDiv(x, this.horizontalNoiseResolution);
        int l = Math.floorDiv(z, this.horizontalNoiseResolution);
        int m = Math.floorMod(x, this.horizontalNoiseResolution);
        int n = Math.floorMod(z, this.horizontalNoiseResolution);
        double d = (double)m / (double)this.horizontalNoiseResolution;
        double e = (double)n / (double)this.horizontalNoiseResolution;
        double[][] ds = new double[][]{this.sampleNoiseColumn(k, l, minY, noiseSizeY), this.sampleNoiseColumn(k, l + 1, minY, noiseSizeY), this.sampleNoiseColumn(k + 1, l, minY, noiseSizeY), this.sampleNoiseColumn(k + 1, l + 1, minY, noiseSizeY)};
        AquiferSampler aquiferSampler = this.createBlockSampler(minY, noiseSizeY, new ChunkPos(i, j));
        for (int o = noiseSizeY - 1; o >= 0; --o) {
            double f = ds[0][o];
            double g = ds[1][o];
            double h = ds[2][o];
            double p = ds[3][o];
            double q = ds[0][o + 1];
            double r = ds[1][o + 1];
            double s = ds[2][o + 1];
            double t = ds[3][o + 1];
            for (int u = this.verticalNoiseResolution - 1; u >= 0; --u) {
                double v = (double)u / (double)this.verticalNoiseResolution;
                double w = MathHelper.lerp3(v, d, e, f, q, h, s, g, r, p, t);
                int y = o * this.verticalNoiseResolution + u;
                int aa = y + minY * this.verticalNoiseResolution;
                BlockState blockState = this.getBlockState(StructureWeightSampler.INSTANCE, aquiferSampler, this.deepslateSource, WeightSampler.DEFAULT, x, aa, z, w);
                if (states != null) {
                    states[y] = blockState;
                }
                if (predicate == null || !predicate.test(blockState)) continue;
                return OptionalInt.of(aa + 1);
            }
        }
        return OptionalInt.empty();
    }

    private AquiferSampler createBlockSampler(int startY, int deltaY, ChunkPos pos) {
        if (!this.hasAquifers()) {
            return AquiferSampler.seaLevel(this.getSeaLevel(), this.defaultFluid);
        }
        return AquiferSampler.aquifer(pos, this.edgeDensityNoise, this.fluidLevelNoise, this.fluidTypeNoise, this.settings.get(), this.noiseColumnSampler, startY * this.verticalNoiseResolution, deltaY * this.verticalNoiseResolution);
    }

    protected BlockState getBlockState(StructureWeightSampler structures, AquiferSampler aquiferSampler, BlockSource blockInterpolator, WeightSampler weightSampler, int i, int j, int k, double d) {
        double e = MathHelper.clamp(d / 200.0, -1.0, 1.0);
        e = e / 2.0 - e * e * e / 24.0;
        e = weightSampler.sample(e, i, j, k);
        return aquiferSampler.apply(blockInterpolator, i, j, k, e += structures.getWeight(i, j, k));
    }

    @Override
    public void buildSurface(ChunkRegion region, Chunk chunk) {
        ChunkPos chunkPos = chunk.getPos();
        int i = chunkPos.x;
        int j = chunkPos.z;
        ChunkRandom chunkRandom = new ChunkRandom();
        chunkRandom.setTerrainSeed(i, j);
        ChunkPos chunkPos2 = chunk.getPos();
        int k = chunkPos2.getStartX();
        int l = chunkPos2.getStartZ();
        double d = 0.0625;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int m = 0; m < 16; ++m) {
            for (int n = 0; n < 16; ++n) {
                int o = k + m;
                int p = l + n;
                int q = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, m, n) + 1;
                double e = this.surfaceDepthNoise.sample((double)o * 0.0625, (double)p * 0.0625, 0.0625, (double)m * 0.0625) * 15.0;
                int r = this.settings.get().getMinSurfaceLevel();
                region.getBiome(mutable.set(k + m, q, l + n)).buildSurface(chunkRandom, chunk, o, p, q, e, this.defaultBlock, this.defaultFluid, this.getSeaLevel(), r, region.getSeed());
            }
        }
        this.buildBedrock(chunk, chunkRandom);
    }

    private void buildBedrock(Chunk chunk, Random random) {
        boolean bl2;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        int i = chunk.getPos().getStartX();
        int j = chunk.getPos().getStartZ();
        ChunkGeneratorSettings chunkGeneratorSettings = this.settings.get();
        int k = chunkGeneratorSettings.getGenerationShapeConfig().getMinimumY();
        int l = k + chunkGeneratorSettings.getBedrockFloorY();
        int m = this.worldHeight - 1 + k - chunkGeneratorSettings.getBedrockCeilingY();
        int n = 5;
        int o = chunk.getBottomY();
        int p = chunk.getTopY();
        boolean bl = m + 5 - 1 >= o && m < p;
        boolean bl3 = bl2 = l + 5 - 1 >= o && l < p;
        if (!bl && !bl2) {
            return;
        }
        for (BlockPos blockPos : BlockPos.iterate(i, 0, j, i + 15, 0, j + 15)) {
            int q;
            if (bl) {
                for (q = 0; q < 5; ++q) {
                    if (q > random.nextInt(5)) continue;
                    chunk.setBlockState(mutable.set(blockPos.getX(), m - q, blockPos.getZ()), Blocks.BEDROCK.getDefaultState(), false);
                }
            }
            if (!bl2) continue;
            for (q = 4; q >= 0; --q) {
                if (q > random.nextInt(5)) continue;
                chunk.setBlockState(mutable.set(blockPos.getX(), l + q, blockPos.getZ()), Blocks.BEDROCK.getDefaultState(), false);
            }
        }
    }

    @Override
    public CompletableFuture<Chunk> populateNoise(Executor executor, StructureAccessor accessor, Chunk chunk) {
        GenerationShapeConfig generationShapeConfig = this.settings.get().getGenerationShapeConfig();
        int i = Math.max(generationShapeConfig.getMinimumY(), chunk.getBottomY());
        int j = Math.min(generationShapeConfig.getMinimumY() + generationShapeConfig.getHeight(), chunk.getTopY());
        int k = MathHelper.floorDiv(i, this.verticalNoiseResolution);
        int l = MathHelper.floorDiv(j - i, this.verticalNoiseResolution);
        if (l <= 0) {
            return CompletableFuture.completedFuture(chunk);
        }
        int m = chunk.getSectionIndex(l * this.verticalNoiseResolution - 1 + i);
        int n = chunk.getSectionIndex(i);
        return CompletableFuture.supplyAsync(() -> {
            HashSet<ChunkSection> set = Sets.newHashSet();
            try {
                for (int m = m; m >= n; --m) {
                    ChunkSection chunkSection = chunk.getSection(m);
                    chunkSection.lock();
                    set.add(chunkSection);
                }
                Chunk chunk2 = this.populateNoise(accessor, chunk, k, l);
                return chunk2;
            } finally {
                for (ChunkSection chunkSection2 : set) {
                    chunkSection2.unlock();
                }
            }
        }, Util.getMainWorkerExecutor());
    }

    private Chunk populateNoise(StructureAccessor accessor, Chunk chunk, int startY, int noiseSizeY) {
        Heightmap heightmap = chunk.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
        Heightmap heightmap2 = chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);
        ChunkPos chunkPos = chunk.getPos();
        int i = chunkPos.getStartX();
        int j = chunkPos.getStartZ();
        StructureWeightSampler structureWeightSampler = new StructureWeightSampler(accessor, chunk);
        AquiferSampler aquiferSampler = this.createBlockSampler(startY, noiseSizeY, chunkPos);
        NoiseInterpolator noiseInterpolator2 = new NoiseInterpolator(this.noiseSizeX, noiseSizeY, this.noiseSizeZ, chunkPos, startY, this::sampleNoiseColumn);
        ArrayList<NoiseInterpolator> list = Lists.newArrayList(noiseInterpolator2);
        Consumer<NoiseInterpolator> consumer = list::add;
        DoubleFunction<BlockSource> doubleFunction = this.createBlockSourceFactory(startY, chunkPos, consumer);
        DoubleFunction<WeightSampler> doubleFunction2 = this.createWeightSamplerFactory(startY, chunkPos, consumer);
        list.forEach(NoiseInterpolator::sampleStartNoise);
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int k = 0; k < this.noiseSizeX; ++k) {
            int l = k;
            list.forEach(noiseInterpolator -> noiseInterpolator.sampleEndNoise(l));
            for (int m = 0; m < this.noiseSizeZ; ++m) {
                ChunkSection chunkSection = chunk.getSection(chunk.countVerticalSections() - 1);
                for (int n = noiseSizeY - 1; n >= 0; --n) {
                    int o = m;
                    int p = n;
                    list.forEach(noiseInterpolator -> noiseInterpolator.sampleNoiseCorners(p, o));
                    for (int q = this.verticalNoiseResolution - 1; q >= 0; --q) {
                        int r = (startY + n) * this.verticalNoiseResolution + q;
                        int s = r & 0xF;
                        int t = chunk.getSectionIndex(r);
                        if (chunk.getSectionIndex(chunkSection.getYOffset()) != t) {
                            chunkSection = chunk.getSection(t);
                        }
                        double d = (double)q / (double)this.verticalNoiseResolution;
                        list.forEach(noiseInterpolator -> noiseInterpolator.sampleNoiseY(d));
                        for (int u = 0; u < this.horizontalNoiseResolution; ++u) {
                            int v = i + k * this.horizontalNoiseResolution + u;
                            int w = v & 0xF;
                            double e = (double)u / (double)this.horizontalNoiseResolution;
                            list.forEach(noiseInterpolator -> noiseInterpolator.sampleNoiseX(e));
                            for (int x = 0; x < this.horizontalNoiseResolution; ++x) {
                                int y = j + m * this.horizontalNoiseResolution + x;
                                int z = y & 0xF;
                                double f = (double)x / (double)this.horizontalNoiseResolution;
                                double g = noiseInterpolator2.sampleNoise(f);
                                BlockState blockState = this.getBlockState(structureWeightSampler, aquiferSampler, doubleFunction.apply(f), doubleFunction2.apply(f), v, r, y, g);
                                if (blockState == AIR) continue;
                                if (blockState.getLuminance() != 0 && chunk instanceof ProtoChunk) {
                                    mutable.set(v, r, y);
                                    ((ProtoChunk)chunk).addLightSource(mutable);
                                }
                                chunkSection.setBlockState(w, s, z, blockState, false);
                                heightmap.trackUpdate(w, r, z, blockState);
                                heightmap2.trackUpdate(w, r, z, blockState);
                                if (!aquiferSampler.needsFluidTick() || blockState.getFluidState().isEmpty()) continue;
                                mutable.set(v, r, y);
                                chunk.getFluidTickScheduler().schedule(mutable, blockState.getFluidState().getFluid(), 0);
                            }
                        }
                    }
                }
            }
            list.forEach(NoiseInterpolator::swapBuffers);
        }
        return chunk;
    }

    private DoubleFunction<WeightSampler> createWeightSamplerFactory(int minY, ChunkPos pos, Consumer<NoiseInterpolator> consumer) {
        if (!this.settings.get().hasNoodleCaves()) {
            return d -> WeightSampler.DEFAULT;
        }
        NoodleCavesSampler noodleCavesSampler = new NoodleCavesSampler(pos, minY);
        noodleCavesSampler.feed(consumer);
        return noodleCavesSampler::setDeltaZ;
    }

    private DoubleFunction<BlockSource> createBlockSourceFactory(int minY, ChunkPos pos, Consumer<NoiseInterpolator> consumer) {
        if (!this.settings.get().hasOreVeins()) {
            return d -> this.deepslateSource;
        }
        OreVeinSource oreVeinSource = new OreVeinSource(pos, minY, this.seed + 1L);
        oreVeinSource.feed(consumer);
        BlockSource blockSource = (i, j, k) -> {
            BlockState blockState = oreVeinSource.sample(i, j, k);
            if (blockState != this.defaultBlock) {
                return blockState;
            }
            return this.deepslateSource.sample(i, j, k);
        };
        return deltaZ -> {
            oreVeinSource.setDeltaZ(deltaZ);
            return blockSource;
        };
    }

    @Override
    protected AquiferSampler createAquiferSampler(Chunk chunk) {
        ChunkPos chunkPos = chunk.getPos();
        int i = Math.max(this.settings.get().getGenerationShapeConfig().getMinimumY(), chunk.getBottomY());
        int j = MathHelper.floorDiv(i, this.verticalNoiseResolution);
        return this.createBlockSampler(j, this.noiseSizeY, chunkPos);
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
        return this.settings.get().getGenerationShapeConfig().getMinimumY();
    }

    @Override
    public Pool<SpawnSettings.SpawnEntry> getEntitySpawnList(Biome biome, StructureAccessor accessor, SpawnGroup group, BlockPos pos) {
        if (accessor.getStructureAt(pos, true, StructureFeature.SWAMP_HUT).hasChildren()) {
            if (group == SpawnGroup.MONSTER) {
                return StructureFeature.SWAMP_HUT.getMonsterSpawns();
            }
            if (group == SpawnGroup.CREATURE) {
                return StructureFeature.SWAMP_HUT.getCreatureSpawns();
            }
        }
        if (group == SpawnGroup.MONSTER) {
            if (accessor.getStructureAt(pos, false, StructureFeature.PILLAGER_OUTPOST).hasChildren()) {
                return StructureFeature.PILLAGER_OUTPOST.getMonsterSpawns();
            }
            if (accessor.getStructureAt(pos, false, StructureFeature.MONUMENT).hasChildren()) {
                return StructureFeature.MONUMENT.getMonsterSpawns();
            }
            if (accessor.getStructureAt(pos, true, StructureFeature.FORTRESS).hasChildren()) {
                return StructureFeature.FORTRESS.getMonsterSpawns();
            }
        }
        if (group == SpawnGroup.UNDERGROUND_WATER_CREATURE && accessor.getStructureAt(pos, false, StructureFeature.MONUMENT).hasChildren()) {
            return StructureFeature.MONUMENT.getUndergroundWaterCreatureSpawns();
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
        ChunkRandom chunkRandom = new ChunkRandom();
        chunkRandom.setPopulationSeed(region.getSeed(), chunkPos.getStartX(), chunkPos.getStartZ());
        SpawnHelper.populateEntities(region, biome, chunkPos, chunkRandom);
    }

    class NoodleCavesSampler
    implements WeightSampler {
        private final NoiseInterpolator field_33646;
        private final NoiseInterpolator field_33647;
        private final NoiseInterpolator field_33648;
        private final NoiseInterpolator field_33649;
        private double deltaZ;

        public NoodleCavesSampler(ChunkPos pos, int minY) {
            this.field_33646 = new NoiseInterpolator(NoiseChunkGenerator.this.noiseSizeX, NoiseChunkGenerator.this.noiseSizeY, NoiseChunkGenerator.this.noiseSizeZ, pos, minY, NoiseChunkGenerator.this.noodleCavesGenerator::method_36471);
            this.field_33647 = new NoiseInterpolator(NoiseChunkGenerator.this.noiseSizeX, NoiseChunkGenerator.this.noiseSizeY, NoiseChunkGenerator.this.noiseSizeZ, pos, minY, NoiseChunkGenerator.this.noodleCavesGenerator::method_36474);
            this.field_33648 = new NoiseInterpolator(NoiseChunkGenerator.this.noiseSizeX, NoiseChunkGenerator.this.noiseSizeY, NoiseChunkGenerator.this.noiseSizeZ, pos, minY, NoiseChunkGenerator.this.noodleCavesGenerator::method_36475);
            this.field_33649 = new NoiseInterpolator(NoiseChunkGenerator.this.noiseSizeX, NoiseChunkGenerator.this.noiseSizeY, NoiseChunkGenerator.this.noiseSizeZ, pos, minY, NoiseChunkGenerator.this.noodleCavesGenerator::method_36476);
        }

        public WeightSampler setDeltaZ(double deltaZ) {
            this.deltaZ = deltaZ;
            return this;
        }

        @Override
        public double sample(double weight, int x, int y, int z) {
            double d = this.field_33646.sampleNoise(this.deltaZ);
            double e = this.field_33647.sampleNoise(this.deltaZ);
            double f = this.field_33648.sampleNoise(this.deltaZ);
            double g = this.field_33649.sampleNoise(this.deltaZ);
            return NoiseChunkGenerator.this.noodleCavesGenerator.method_36470(weight, x, y, z, d, e, f, g, NoiseChunkGenerator.this.getMinimumY());
        }

        public void feed(Consumer<NoiseInterpolator> consumer) {
            consumer.accept(this.field_33646);
            consumer.accept(this.field_33647);
            consumer.accept(this.field_33648);
            consumer.accept(this.field_33649);
        }
    }

    class OreVeinSource
    implements BlockSource {
        private final NoiseInterpolator field_33581;
        private final NoiseInterpolator field_33582;
        private final NoiseInterpolator field_33583;
        private double deltaZ;
        private final long seed;
        private final ChunkRandom random = new ChunkRandom();

        public OreVeinSource(ChunkPos pos, int minY, long seed) {
            this.field_33581 = new NoiseInterpolator(NoiseChunkGenerator.this.noiseSizeX, NoiseChunkGenerator.this.noiseSizeY, NoiseChunkGenerator.this.noiseSizeZ, pos, minY, NoiseChunkGenerator.this.oreVeinGenerator::method_36401);
            this.field_33582 = new NoiseInterpolator(NoiseChunkGenerator.this.noiseSizeX, NoiseChunkGenerator.this.noiseSizeY, NoiseChunkGenerator.this.noiseSizeZ, pos, minY, NoiseChunkGenerator.this.oreVeinGenerator::method_36404);
            this.field_33583 = new NoiseInterpolator(NoiseChunkGenerator.this.noiseSizeX, NoiseChunkGenerator.this.noiseSizeY, NoiseChunkGenerator.this.noiseSizeZ, pos, minY, NoiseChunkGenerator.this.oreVeinGenerator::method_36405);
            this.seed = seed;
        }

        public void feed(Consumer<NoiseInterpolator> consumer) {
            consumer.accept(this.field_33581);
            consumer.accept(this.field_33582);
            consumer.accept(this.field_33583);
        }

        public void setDeltaZ(double deltaZ) {
            this.deltaZ = deltaZ;
        }

        @Override
        public BlockState sample(int x, int y, int z) {
            double d = this.field_33581.sampleNoise(this.deltaZ);
            double e = this.field_33582.sampleNoise(this.deltaZ);
            double f = this.field_33583.sampleNoise(this.deltaZ);
            this.random.setGrimstoneSeed(this.seed, x, y, z);
            return NoiseChunkGenerator.this.oreVeinGenerator.sample(this.random, x, y, z, d, e, f);
        }
    }
}


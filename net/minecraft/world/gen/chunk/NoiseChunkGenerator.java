/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.chunk;

import com.google.common.collect.Sets;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashSet;
import java.util.List;
import java.util.OptionalInt;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.math.noise.InterpolatedNoise;
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
import net.minecraft.world.gen.AquiferSampler;
import net.minecraft.world.gen.BlockInterpolator;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.GrimstoneInterpolator;
import net.minecraft.world.gen.NoiseCaveSampler;
import net.minecraft.world.gen.NoiseColumnSampler;
import net.minecraft.world.gen.SimpleRandom;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.StructureWeightSampler;
import net.minecraft.world.gen.WorldGenRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.GenerationShapeConfig;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.feature.StructureFeature;
import org.jetbrains.annotations.Nullable;

public final class NoiseChunkGenerator
extends ChunkGenerator {
    public static final Codec<NoiseChunkGenerator> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)BiomeSource.CODEC.fieldOf("biome_source")).forGetter(noiseChunkGenerator -> noiseChunkGenerator.populationSource), ((MapCodec)Codec.LONG.fieldOf("seed")).stable().forGetter(noiseChunkGenerator -> noiseChunkGenerator.seed), ((MapCodec)ChunkGeneratorSettings.REGISTRY_CODEC.fieldOf("settings")).forGetter(noiseChunkGenerator -> noiseChunkGenerator.settings)).apply((Applicative<NoiseChunkGenerator, ?>)instance, instance.stable(NoiseChunkGenerator::new)));
    private static final BlockState AIR = Blocks.AIR.getDefaultState();
    private static final BlockState[] EMPTY = new BlockState[0];
    private final int verticalNoiseResolution;
    private final int horizontalNoiseResolution;
    private final int noiseSizeX;
    private final int noiseSizeY;
    private final int noiseSizeZ;
    private final NoiseSampler surfaceDepthNoise;
    private final DoublePerlinNoiseSampler field_28843;
    private final DoublePerlinNoiseSampler waterLevelNoise;
    protected final BlockState defaultBlock;
    protected final BlockState defaultFluid;
    private final long seed;
    protected final Supplier<ChunkGeneratorSettings> settings;
    private final int worldHeight;
    private final NoiseColumnSampler noiseColumnSampler;
    private final boolean hasAquifers;
    private final BlockInterpolator blockInterpolator;

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
        InterpolatedNoise interpolatedNoise = new InterpolatedNoise(chunkRandom);
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
        this.field_28843 = DoublePerlinNoiseSampler.create((WorldGenRandom)new SimpleRandom(chunkRandom.nextLong()), -3, 1.0);
        this.waterLevelNoise = DoublePerlinNoiseSampler.create((WorldGenRandom)new SimpleRandom(chunkRandom.nextLong()), -3, 1.0, 0.0, 2.0);
        NoiseCaveSampler noiseCaveSampler = chunkGeneratorSettings.hasNoiseCaves() ? new NoiseCaveSampler(chunkRandom, generationShapeConfig.getMinimumY() / this.verticalNoiseResolution) : null;
        this.noiseColumnSampler = new NoiseColumnSampler(populationSource, this.horizontalNoiseResolution, this.verticalNoiseResolution, this.noiseSizeY, generationShapeConfig, interpolatedNoise, simplexNoiseSampler, octavePerlinNoiseSampler, noiseCaveSampler);
        this.hasAquifers = chunkGeneratorSettings.hasAquifers();
        this.blockInterpolator = new GrimstoneInterpolator(seed, this.defaultBlock, Blocks.GRIMSTONE.getDefaultState());
    }

    @Override
    protected Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public ChunkGenerator withSeed(long seed) {
        return new NoiseChunkGenerator(this.populationSource.withSeed(seed), seed, this.settings);
    }

    public boolean matchesSettings(long seed, RegistryKey<ChunkGeneratorSettings> settingsKey) {
        return this.seed == seed && this.settings.get().equals(settingsKey);
    }

    private double[] sampleNoiseColumn(int x, int z, int minY, int noiseSizeY) {
        double[] ds = new double[noiseSizeY + 1];
        this.noiseColumnSampler.sampleNoiseColumn(ds, x, z, this.settings.get().getGenerationShapeConfig(), this.getSeaLevel(), minY, noiseSizeY);
        return ds;
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
        AquiferSampler aquiferSampler = this.hasAquifers ? new AquiferSampler(i, j, this.field_28843, this.waterLevelNoise, this.settings.get(), this.noiseColumnSampler, noiseSizeY * this.verticalNoiseResolution) : null;
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
                BlockState blockState = this.getBlockState(StructureWeightSampler.INSTANCE, aquiferSampler, this.blockInterpolator, x, aa, z, w);
                if (states != null) {
                    states[y] = blockState;
                }
                if (predicate == null || !predicate.test(blockState)) continue;
                return OptionalInt.of(aa + 1);
            }
        }
        return OptionalInt.empty();
    }

    protected BlockState getBlockState(StructureWeightSampler structures, @Nullable AquiferSampler aquiferSampler, BlockInterpolator blockInterpolator, int x, int y, int z, double noise) {
        BlockState blockState;
        double d = MathHelper.clamp(noise / 200.0, -1.0, 1.0);
        d = d / 2.0 - d * d * d / 24.0;
        d += structures.getWeight(x, y, z);
        if (aquiferSampler != null) {
            aquiferSampler.apply(x, y, z);
            d += aquiferSampler.getDensityAddition();
        }
        if (d > 0.0) {
            blockState = blockInterpolator.sample(x, y, z, this.settings.get());
        } else {
            int i = aquiferSampler == null ? this.getSeaLevel() : aquiferSampler.getWaterLevel();
            blockState = y < i ? this.defaultFluid : AIR;
        }
        return blockState;
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
                region.getBiome(mutable.set(k + m, q, l + n)).buildSurface(chunkRandom, chunk, o, p, q, e, this.defaultBlock, this.defaultFluid, this.getSeaLevel(), region.getSeed());
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
    public CompletableFuture<Chunk> populateNoise(Executor executor, StructureAccessor accessor, Chunk chunk2) {
        GenerationShapeConfig generationShapeConfig = this.settings.get().getGenerationShapeConfig();
        int i = Math.max(generationShapeConfig.getMinimumY(), chunk2.getBottomY());
        int j = Math.min(generationShapeConfig.getMinimumY() + generationShapeConfig.getHeight(), chunk2.getTopY());
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
        return CompletableFuture.supplyAsync(() -> this.populateNoise(accessor, chunk2, k, l), Util.getMainWorkerExecutor()).thenApplyAsync(chunk -> {
            for (ChunkSection chunkSection : set) {
                chunkSection.unlock();
            }
            return chunk;
        }, executor);
    }

    private Chunk populateNoise(StructureAccessor accessor, Chunk chunk, int minY, int noiseSizeY) {
        int p;
        int o;
        GenerationShapeConfig generationShapeConfig = this.settings.get().getGenerationShapeConfig();
        int i = generationShapeConfig.getMinimumY();
        Heightmap heightmap = chunk.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
        Heightmap heightmap2 = chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);
        ChunkPos chunkPos = chunk.getPos();
        int j = chunkPos.x;
        int k = chunkPos.z;
        int l = chunkPos.getStartX();
        int m = chunkPos.getStartZ();
        StructureWeightSampler structureWeightSampler = new StructureWeightSampler(accessor, chunk);
        AquiferSampler aquiferSampler = this.hasAquifers ? new AquiferSampler(j, k, this.field_28843, this.waterLevelNoise, this.settings.get(), this.noiseColumnSampler, noiseSizeY * this.verticalNoiseResolution) : null;
        double[][][] ds = new double[2][this.noiseSizeZ + 1][noiseSizeY + 1];
        for (int n = 0; n < this.noiseSizeZ + 1; ++n) {
            ds[0][n] = new double[noiseSizeY + 1];
            double[] es = ds[0][n];
            o = j * this.noiseSizeX;
            p = k * this.noiseSizeZ + n;
            this.noiseColumnSampler.sampleNoiseColumn(es, o, p, generationShapeConfig, this.getSeaLevel(), minY, noiseSizeY);
            ds[1][n] = new double[noiseSizeY + 1];
        }
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int q = 0; q < this.noiseSizeX; ++q) {
            int r;
            o = j * this.noiseSizeX + q + 1;
            for (p = 0; p < this.noiseSizeZ + 1; ++p) {
                double[] fs = ds[1][p];
                r = k * this.noiseSizeZ + p;
                this.noiseColumnSampler.sampleNoiseColumn(fs, o, r, generationShapeConfig, this.getSeaLevel(), minY, noiseSizeY);
            }
            for (p = 0; p < this.noiseSizeZ; ++p) {
                ChunkSection chunkSection = chunk.getSection(chunk.countVerticalSections() - 1);
                for (r = noiseSizeY - 1; r >= 0; --r) {
                    double d = ds[0][p][r];
                    double e = ds[0][p + 1][r];
                    double f = ds[1][p][r];
                    double g = ds[1][p + 1][r];
                    double h = ds[0][p][r + 1];
                    double s = ds[0][p + 1][r + 1];
                    double t = ds[1][p][r + 1];
                    double u = ds[1][p + 1][r + 1];
                    for (int v = this.verticalNoiseResolution - 1; v >= 0; --v) {
                        int w = r * this.verticalNoiseResolution + v + i;
                        int x = w & 0xF;
                        int y = chunk.getSectionIndex(w);
                        if (chunk.getSectionIndex(chunkSection.getYOffset()) != y) {
                            chunkSection = chunk.getSection(y);
                        }
                        double z = (double)v / (double)this.verticalNoiseResolution;
                        double aa = MathHelper.lerp(z, d, h);
                        double ab = MathHelper.lerp(z, f, t);
                        double ac = MathHelper.lerp(z, e, s);
                        double ad = MathHelper.lerp(z, g, u);
                        for (int ae = 0; ae < this.horizontalNoiseResolution; ++ae) {
                            int af = l + q * this.horizontalNoiseResolution + ae;
                            int ag = af & 0xF;
                            double ah = (double)ae / (double)this.horizontalNoiseResolution;
                            double ai = MathHelper.lerp(ah, aa, ab);
                            double aj = MathHelper.lerp(ah, ac, ad);
                            for (int ak = 0; ak < this.horizontalNoiseResolution; ++ak) {
                                int al = m + p * this.horizontalNoiseResolution + ak;
                                int am = al & 0xF;
                                double an = (double)ak / (double)this.horizontalNoiseResolution;
                                double ao = MathHelper.lerp(an, ai, aj);
                                BlockState blockState = this.getBlockState(structureWeightSampler, aquiferSampler, this.blockInterpolator, af, w, al, ao);
                                if (blockState == AIR) continue;
                                if (blockState.getLuminance() != 0 && chunk instanceof ProtoChunk) {
                                    mutable.set(af, w, al);
                                    ((ProtoChunk)chunk).addLightSource(mutable);
                                }
                                chunkSection.setBlockState(ag, x, am, blockState, false);
                                heightmap.trackUpdate(ag, w, am, blockState);
                                heightmap2.trackUpdate(ag, w, am, blockState);
                                if (aquiferSampler == null || !aquiferSampler.needsFluidTick() || blockState.getFluidState().isEmpty()) continue;
                                mutable.set(af, w, al);
                                chunk.getFluidTickScheduler().schedule(mutable, blockState.getFluidState().getFluid(), 0);
                            }
                        }
                    }
                }
            }
            this.swapElements((T[])ds);
        }
        return chunk;
    }

    public <T> void swapElements(T[] array) {
        T object = array[0];
        array[0] = array[1];
        array[1] = object;
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
    public List<SpawnSettings.SpawnEntry> getEntitySpawnList(Biome biome, StructureAccessor accessor, SpawnGroup group, BlockPos pos) {
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
}


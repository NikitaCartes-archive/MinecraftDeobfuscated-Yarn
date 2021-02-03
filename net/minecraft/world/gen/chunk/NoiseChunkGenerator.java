/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.chunk;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.OptionalInt;
import java.util.Random;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.class_5742;
import net.minecraft.class_5817;
import net.minecraft.class_5818;
import net.minecraft.class_5819;
import net.minecraft.class_5822;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.NoiseSampler;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;
import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.StructureAccessor;
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
    private static final BlockState[] field_28746 = new BlockState[0];
    private final int verticalNoiseResolution;
    private final int horizontalNoiseResolution;
    private final int noiseSizeX;
    private final int field_28747;
    private final int noiseSizeZ;
    protected final class_5819 random;
    private final NoiseSampler surfaceDepthNoise;
    protected final BlockState defaultBlock;
    protected final BlockState defaultFluid;
    private final long seed;
    protected final Supplier<ChunkGeneratorSettings> settings;
    private final int worldHeight;
    private final class_5818 field_28748;

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
        this.verticalNoiseResolution = class_5742.method_33101(generationShapeConfig.getSizeVertical());
        this.horizontalNoiseResolution = class_5742.method_33101(generationShapeConfig.getSizeHorizontal());
        this.defaultBlock = chunkGeneratorSettings.getDefaultBlock();
        this.defaultFluid = chunkGeneratorSettings.getDefaultFluid();
        this.noiseSizeX = 16 / this.horizontalNoiseResolution;
        this.field_28747 = generationShapeConfig.getHeight() / this.verticalNoiseResolution;
        this.noiseSizeZ = 16 / this.horizontalNoiseResolution;
        this.random = new ChunkRandom(seed);
        class_5822 lv = new class_5822(this.random);
        this.surfaceDepthNoise = generationShapeConfig.hasSimplexSurfaceNoise() ? new OctaveSimplexNoiseSampler(this.random, IntStream.rangeClosed(-3, 0)) : new OctavePerlinNoiseSampler(this.random, IntStream.rangeClosed(-3, 0));
        this.random.method_33650(2620);
        OctavePerlinNoiseSampler octavePerlinNoiseSampler = new OctavePerlinNoiseSampler(this.random, IntStream.rangeClosed(-15, 0));
        if (generationShapeConfig.hasIslandNoiseOverride()) {
            ChunkRandom chunkRandom = new ChunkRandom(seed);
            chunkRandom.method_33650(17292);
            simplexNoiseSampler = new SimplexNoiseSampler(chunkRandom);
        } else {
            simplexNoiseSampler = null;
        }
        this.field_28748 = new class_5818(populationSource, this.horizontalNoiseResolution, this.verticalNoiseResolution, this.field_28747, generationShapeConfig, lv, simplexNoiseSampler, octavePerlinNoiseSampler);
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

    private double[] sampleNoiseColumn(int x, int z, int i, int j) {
        double[] ds = new double[j + 1];
        this.field_28748.method_33648(ds, x, z, this.settings.get().getGenerationShapeConfig(), this.getSeaLevel(), i, j);
        return ds;
    }

    @Override
    public int getHeight(int x, int z, Heightmap.Type heightmapType, HeightLimitView heightLimitView) {
        int i = Math.max(this.settings.get().getGenerationShapeConfig().getMinimumY(), heightLimitView.getBottomSectionLimit());
        int j = Math.min(this.settings.get().getGenerationShapeConfig().getMinimumY() + this.settings.get().getGenerationShapeConfig().getHeight(), heightLimitView.getTopHeightLimit());
        int k = MathHelper.floorDiv(i, this.verticalNoiseResolution);
        int l = MathHelper.floorDiv(j - i, this.verticalNoiseResolution);
        if (l <= 0) {
            return heightLimitView.getBottomSectionLimit();
        }
        return this.sampleHeightmap(x, z, null, heightmapType.getBlockPredicate(), k, l).orElse(heightLimitView.getBottomSectionLimit());
    }

    @Override
    public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView heightLimitView) {
        int i = Math.max(this.settings.get().getGenerationShapeConfig().getMinimumY(), heightLimitView.getBottomSectionLimit());
        int j = Math.min(this.settings.get().getGenerationShapeConfig().getMinimumY() + this.settings.get().getGenerationShapeConfig().getHeight(), heightLimitView.getTopHeightLimit());
        int k = MathHelper.floorDiv(i, this.verticalNoiseResolution);
        int l = MathHelper.floorDiv(j - i, this.verticalNoiseResolution);
        if (l <= 0) {
            return new VerticalBlockSample(i, field_28746);
        }
        BlockState[] blockStates = new BlockState[l * this.verticalNoiseResolution];
        this.sampleHeightmap(x, z, blockStates, null, k, l);
        return new VerticalBlockSample(i, blockStates);
    }

    private OptionalInt sampleHeightmap(int x, int z, @Nullable BlockState[] states, @Nullable Predicate<BlockState> predicate, int i, int j) {
        int k = Math.floorDiv(x, this.horizontalNoiseResolution);
        int l = Math.floorDiv(z, this.horizontalNoiseResolution);
        int m = Math.floorMod(x, this.horizontalNoiseResolution);
        int n = Math.floorMod(z, this.horizontalNoiseResolution);
        double d = (double)m / (double)this.horizontalNoiseResolution;
        double e = (double)n / (double)this.horizontalNoiseResolution;
        double[][] ds = new double[][]{this.sampleNoiseColumn(k, l, i, j), this.sampleNoiseColumn(k, l + 1, i, j), this.sampleNoiseColumn(k + 1, l, i, j), this.sampleNoiseColumn(k + 1, l + 1, i, j)};
        for (int o = j - 1; o >= 0; --o) {
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
                int aa = y + i * this.verticalNoiseResolution;
                BlockState blockState = this.method_33643(class_5817.field_28740, x, aa, z, w);
                if (states != null) {
                    states[y] = blockState;
                }
                if (predicate == null || !predicate.test(blockState)) continue;
                return OptionalInt.of(aa + 1);
            }
        }
        return OptionalInt.empty();
    }

    protected BlockState method_33643(class_5817 arg, int i, int j, int k, double d) {
        double e = MathHelper.clamp(d / 200.0, -1.0, 1.0);
        e = e / 2.0 - e * e * e / 24.0;
        BlockState blockState = (e += arg.method_33638(i, j, k)) > 0.0 ? this.defaultBlock : (j < this.getSeaLevel() ? this.defaultFluid : AIR);
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
        int k = chunkGeneratorSettings.getBedrockFloorY();
        int l = this.worldHeight - 1 - chunkGeneratorSettings.getBedrockCeilingY();
        int m = 5;
        boolean bl = l + 5 - 1 >= chunk.getBottomSectionLimit() && l < chunk.getTopHeightLimit();
        boolean bl3 = bl2 = k + 5 - 1 >= chunk.getBottomSectionLimit() && k < chunk.getTopHeightLimit();
        if (!bl && !bl2) {
            return;
        }
        for (BlockPos blockPos : BlockPos.iterate(i, 0, j, i + 15, 0, j + 15)) {
            int n;
            if (bl) {
                for (n = 0; n < 5; ++n) {
                    if (n > random.nextInt(5)) continue;
                    chunk.setBlockState(mutable.set(blockPos.getX(), l - n, blockPos.getZ()), Blocks.BEDROCK.getDefaultState(), false);
                }
            }
            if (!bl2) continue;
            for (n = 4; n >= 0; --n) {
                if (n > random.nextInt(5)) continue;
                chunk.setBlockState(mutable.set(blockPos.getX(), k + n, blockPos.getZ()), Blocks.BEDROCK.getDefaultState(), false);
            }
        }
    }

    @Override
    public void populateNoise(WorldAccess world, StructureAccessor accessor, Chunk chunk) {
        int s;
        int r;
        ChunkPos chunkPos = chunk.getPos();
        ProtoChunk protoChunk = (ProtoChunk)chunk;
        Heightmap heightmap = protoChunk.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
        Heightmap heightmap2 = protoChunk.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);
        int i = Math.max(this.settings.get().getGenerationShapeConfig().getMinimumY(), chunk.getBottomSectionLimit());
        int j = Math.min(this.settings.get().getGenerationShapeConfig().getMinimumY() + this.settings.get().getGenerationShapeConfig().getHeight(), chunk.getTopHeightLimit());
        int k = MathHelper.floorDiv(i, this.verticalNoiseResolution);
        int l = MathHelper.floorDiv(j - i, this.verticalNoiseResolution);
        if (l <= 0) {
            return;
        }
        int m = chunkPos.x;
        int n = chunkPos.z;
        int o = chunkPos.getStartX();
        int p = chunkPos.getStartZ();
        class_5817 lv = new class_5817(accessor, chunk);
        double[][][] ds = new double[2][this.noiseSizeZ + 1][l + 1];
        GenerationShapeConfig generationShapeConfig = this.settings.get().getGenerationShapeConfig();
        for (int q = 0; q < this.noiseSizeZ + 1; ++q) {
            ds[0][q] = new double[l + 1];
            double[] es = ds[0][q];
            r = m * this.noiseSizeX;
            s = n * this.noiseSizeZ + q;
            this.field_28748.method_33648(es, r, s, generationShapeConfig, this.getSeaLevel(), k, l);
            ds[1][q] = new double[l + 1];
        }
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int t = 0; t < this.noiseSizeX; ++t) {
            int u;
            r = m * this.noiseSizeX + t + 1;
            for (s = 0; s < this.noiseSizeZ + 1; ++s) {
                double[] fs = ds[1][s];
                u = n * this.noiseSizeZ + s;
                this.field_28748.method_33648(fs, r, u, generationShapeConfig, this.getSeaLevel(), k, l);
            }
            for (s = 0; s < this.noiseSizeZ; ++s) {
                ChunkSection chunkSection = protoChunk.getSection(protoChunk.getSections() - 1);
                chunkSection.lock();
                for (u = l - 1; u >= 0; --u) {
                    double d = ds[0][s][u];
                    double e = ds[0][s + 1][u];
                    double f = ds[1][s][u];
                    double g = ds[1][s + 1][u];
                    double h = ds[0][s][u + 1];
                    double v = ds[0][s + 1][u + 1];
                    double w = ds[1][s][u + 1];
                    double x = ds[1][s + 1][u + 1];
                    for (int y = this.verticalNoiseResolution - 1; y >= 0; --y) {
                        int z = u * this.verticalNoiseResolution + y + this.settings.get().getGenerationShapeConfig().getMinimumY();
                        int aa = z & 0xF;
                        int ab = protoChunk.getSectionIndex(z);
                        if (protoChunk.getSectionIndex(chunkSection.getYOffset()) != ab) {
                            chunkSection.unlock();
                            chunkSection = protoChunk.getSection(ab);
                            chunkSection.lock();
                        }
                        double ac = (double)y / (double)this.verticalNoiseResolution;
                        double ad = MathHelper.lerp(ac, d, h);
                        double ae = MathHelper.lerp(ac, f, w);
                        double af = MathHelper.lerp(ac, e, v);
                        double ag = MathHelper.lerp(ac, g, x);
                        for (int ah = 0; ah < this.horizontalNoiseResolution; ++ah) {
                            int ai = o + t * this.horizontalNoiseResolution + ah;
                            int aj = ai & 0xF;
                            double ak = (double)ah / (double)this.horizontalNoiseResolution;
                            double al = MathHelper.lerp(ak, ad, ae);
                            double am = MathHelper.lerp(ak, af, ag);
                            for (int an = 0; an < this.horizontalNoiseResolution; ++an) {
                                int ao = p + s * this.horizontalNoiseResolution + an;
                                int ap = ao & 0xF;
                                double aq = (double)an / (double)this.horizontalNoiseResolution;
                                double ar = MathHelper.lerp(aq, al, am);
                                BlockState blockState = this.method_33643(lv, ai, z, ao, ar);
                                if (blockState == AIR) continue;
                                if (blockState.getLuminance() != 0) {
                                    mutable.set(ai, z, ao);
                                    protoChunk.addLightSource(mutable);
                                }
                                chunkSection.setBlockState(aj, aa, ap, blockState, false);
                                heightmap.trackUpdate(aj, z, ap, blockState);
                                heightmap2.trackUpdate(aj, z, ap, blockState);
                            }
                        }
                    }
                }
                chunkSection.unlock();
            }
            this.method_33644((T[])ds);
        }
    }

    public <T> void method_33644(T[] objects) {
        T object = objects[0];
        objects[0] = objects[1];
        objects[1] = object;
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
        ChunkPos chunkPos = region.method_33561();
        Biome biome = region.getBiome(chunkPos.getStartPos());
        ChunkRandom chunkRandom = new ChunkRandom();
        chunkRandom.setPopulationSeed(region.getSeed(), chunkPos.getStartX(), chunkPos.getStartZ());
        SpawnHelper.populateEntities(region, biome, chunkPos, chunkRandom);
    }
}


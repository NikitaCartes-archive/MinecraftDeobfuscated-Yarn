/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.chunk;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.structure.JigsawJunction;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.NoiseSampler;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;
import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.world.BlockView;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.chunk.NoiseConfig;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.feature.StructureFeature;
import org.jetbrains.annotations.Nullable;

public final class SurfaceChunkGenerator
extends ChunkGenerator {
    public static final Codec<SurfaceChunkGenerator> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)BiomeSource.field_24713.fieldOf("biome_source")).forGetter(surfaceChunkGenerator -> surfaceChunkGenerator.biomeSource), ((MapCodec)Codec.LONG.fieldOf("seed")).stable().forGetter(surfaceChunkGenerator -> surfaceChunkGenerator.worldSeed), ((MapCodec)ChunkGeneratorType.field_24781.fieldOf("settings")).forGetter(surfaceChunkGenerator -> surfaceChunkGenerator.field_24774)).apply((Applicative<SurfaceChunkGenerator, ?>)instance, instance.stable(SurfaceChunkGenerator::new)));
    private static final float[] field_16649 = Util.make(new float[13824], array -> {
        for (int i = 0; i < 24; ++i) {
            for (int j = 0; j < 24; ++j) {
                for (int k = 0; k < 24; ++k) {
                    array[i * 24 * 24 + j * 24 + k] = (float)SurfaceChunkGenerator.method_16571(j - 12, k - 12, i - 12);
                }
            }
        }
    });
    private static final float[] field_24775 = Util.make(new float[25], fs -> {
        for (int i = -2; i <= 2; ++i) {
            for (int j = -2; j <= 2; ++j) {
                float f;
                fs[i + 2 + (j + 2) * 5] = f = 10.0f / MathHelper.sqrt((float)(i * i + j * j) + 0.2f);
            }
        }
    });
    private static final BlockState AIR = Blocks.AIR.getDefaultState();
    private final int verticalNoiseResolution;
    private final int horizontalNoiseResolution;
    private final int noiseSizeX;
    private final int noiseSizeY;
    private final int noiseSizeZ;
    protected final ChunkRandom random;
    private final OctavePerlinNoiseSampler lowerInterpolatedNoise;
    private final OctavePerlinNoiseSampler upperInterpolatedNoise;
    private final OctavePerlinNoiseSampler interpolationNoise;
    private final NoiseSampler surfaceDepthNoise;
    private final OctavePerlinNoiseSampler field_24776;
    @Nullable
    private final SimplexNoiseSampler field_24777;
    protected final BlockState defaultBlock;
    protected final BlockState defaultFluid;
    private final long worldSeed;
    protected final Supplier<ChunkGeneratorType> field_24774;
    private final int field_24779;

    public SurfaceChunkGenerator(BiomeSource biomeSource, long l, Supplier<ChunkGeneratorType> supplier) {
        this(biomeSource, biomeSource, l, supplier);
    }

    private SurfaceChunkGenerator(BiomeSource biomeSource, BiomeSource biomeSource2, long worldSeed, Supplier<ChunkGeneratorType> supplier) {
        super(biomeSource, biomeSource2, supplier.get().getConfig(), worldSeed);
        this.worldSeed = worldSeed;
        ChunkGeneratorType chunkGeneratorType = supplier.get();
        this.field_24774 = supplier;
        NoiseConfig noiseConfig = chunkGeneratorType.method_28559();
        this.field_24779 = noiseConfig.getHeight();
        this.verticalNoiseResolution = noiseConfig.getSizeVertical() * 4;
        this.horizontalNoiseResolution = noiseConfig.getSizeHorizontal() * 4;
        this.defaultBlock = chunkGeneratorType.getDefaultBlock();
        this.defaultFluid = chunkGeneratorType.getDefaultFluid();
        this.noiseSizeX = 16 / this.horizontalNoiseResolution;
        this.noiseSizeY = noiseConfig.getHeight() / this.verticalNoiseResolution;
        this.noiseSizeZ = 16 / this.horizontalNoiseResolution;
        this.random = new ChunkRandom(worldSeed);
        this.lowerInterpolatedNoise = new OctavePerlinNoiseSampler(this.random, IntStream.rangeClosed(-15, 0));
        this.upperInterpolatedNoise = new OctavePerlinNoiseSampler(this.random, IntStream.rangeClosed(-15, 0));
        this.interpolationNoise = new OctavePerlinNoiseSampler(this.random, IntStream.rangeClosed(-7, 0));
        this.surfaceDepthNoise = noiseConfig.hasSimplexSurfaceNoise() ? new OctaveSimplexNoiseSampler(this.random, IntStream.rangeClosed(-3, 0)) : new OctavePerlinNoiseSampler(this.random, IntStream.rangeClosed(-3, 0));
        this.random.consume(2620);
        this.field_24776 = new OctavePerlinNoiseSampler(this.random, IntStream.rangeClosed(-15, 0));
        if (noiseConfig.hasIslandNoiseOverride()) {
            ChunkRandom chunkRandom = new ChunkRandom(worldSeed);
            chunkRandom.consume(17292);
            this.field_24777 = new SimplexNoiseSampler(chunkRandom);
        } else {
            this.field_24777 = null;
        }
    }

    @Override
    protected Codec<? extends ChunkGenerator> method_28506() {
        return CODEC;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public ChunkGenerator withSeed(long seed) {
        return new SurfaceChunkGenerator(this.biomeSource.withSeed(seed), seed, this.field_24774);
    }

    public boolean method_28548(long l, ChunkGeneratorType chunkGeneratorType) {
        return this.worldSeed == l && this.field_24774.get().method_28555(chunkGeneratorType);
    }

    private double sampleNoise(int x, int y, int z, double horizontalScale, double verticalScale, double horizontalStretch, double verticalStretch) {
        double d = 0.0;
        double e = 0.0;
        double f = 0.0;
        boolean bl = true;
        double g = 1.0;
        for (int i = 0; i < 16; ++i) {
            PerlinNoiseSampler perlinNoiseSampler3;
            PerlinNoiseSampler perlinNoiseSampler2;
            double h = OctavePerlinNoiseSampler.maintainPrecision((double)x * horizontalScale * g);
            double j = OctavePerlinNoiseSampler.maintainPrecision((double)y * verticalScale * g);
            double k = OctavePerlinNoiseSampler.maintainPrecision((double)z * horizontalScale * g);
            double l = verticalScale * g;
            PerlinNoiseSampler perlinNoiseSampler = this.lowerInterpolatedNoise.getOctave(i);
            if (perlinNoiseSampler != null) {
                d += perlinNoiseSampler.sample(h, j, k, l, (double)y * l) / g;
            }
            if ((perlinNoiseSampler2 = this.upperInterpolatedNoise.getOctave(i)) != null) {
                e += perlinNoiseSampler2.sample(h, j, k, l, (double)y * l) / g;
            }
            if (i < 8 && (perlinNoiseSampler3 = this.interpolationNoise.getOctave(i)) != null) {
                f += perlinNoiseSampler3.sample(OctavePerlinNoiseSampler.maintainPrecision((double)x * horizontalStretch * g), OctavePerlinNoiseSampler.maintainPrecision((double)y * verticalStretch * g), OctavePerlinNoiseSampler.maintainPrecision((double)z * horizontalStretch * g), verticalStretch * g, (double)y * verticalStretch * g) / g;
            }
            g /= 2.0;
        }
        return MathHelper.clampedLerp(d / 512.0, e / 512.0, (f / 10.0 + 1.0) / 2.0);
    }

    private double[] sampleNoiseColumn(int x, int z) {
        double[] ds = new double[this.noiseSizeY + 1];
        this.sampleNoiseColumn(ds, x, z);
        return ds;
    }

    private void sampleNoiseColumn(double[] buffer, int x, int z) {
        double w;
        double v;
        double e;
        double d;
        NoiseConfig noiseConfig = this.field_24774.get().method_28559();
        if (this.field_24777 != null) {
            d = TheEndBiomeSource.getNoiseAt(this.field_24777, x, z) - 8.0f;
            e = d > 0.0 ? 0.25 : 1.0;
        } else {
            float f = 0.0f;
            float g = 0.0f;
            float h = 0.0f;
            int i = 2;
            int j = this.getSeaLevel();
            float k = this.biomeSource.getBiomeForNoiseGen(x, j, z).getDepth();
            for (int l = -2; l <= 2; ++l) {
                for (int m = -2; m <= 2; ++m) {
                    float q;
                    float p;
                    Biome biome = this.biomeSource.getBiomeForNoiseGen(x + l, j, z + m);
                    float n = biome.getDepth();
                    float o = biome.getScale();
                    if (noiseConfig.isAmplified() && n > 0.0f) {
                        p = 1.0f + n * 2.0f;
                        q = 1.0f + o * 4.0f;
                    } else {
                        p = n;
                        q = o;
                    }
                    float r = n > k ? 0.5f : 1.0f;
                    float s = r * field_24775[l + 2 + (m + 2) * 5] / (p + 2.0f);
                    f += q * s;
                    g += p * s;
                    h += s;
                }
            }
            float t = g / h;
            float u = f / h;
            v = t * 0.5f - 0.125f;
            w = u * 0.9f + 0.1f;
            d = v * 0.265625;
            e = 96.0 / w;
        }
        double y = 684.412 * noiseConfig.getSampling().getXZScale();
        double aa = 684.412 * noiseConfig.getSampling().getYScale();
        double ab = y / noiseConfig.getSampling().getXZFactor();
        double ac = aa / noiseConfig.getSampling().getYFactor();
        v = noiseConfig.getTopSlide().getTarget();
        w = noiseConfig.getTopSlide().getSize();
        double ad = noiseConfig.getTopSlide().getOffset();
        double ae = noiseConfig.getBottomSlide().getTarget();
        double af = noiseConfig.getBottomSlide().getSize();
        double ag = noiseConfig.getBottomSlide().getOffset();
        double ah = noiseConfig.hasRandomDensityOffset() ? this.method_28553(x, z) : 0.0;
        double ai = noiseConfig.getDensityFactor();
        double aj = noiseConfig.getDensityOffset();
        for (int ak = 0; ak <= this.noiseSizeY; ++ak) {
            double ap;
            double al = this.sampleNoise(x, ak, z, y, aa, ab, ac);
            double am = 1.0 - (double)ak * 2.0 / (double)this.noiseSizeY + ah;
            double an = am * ai + aj;
            double ao = (an + d) * e;
            al = ao > 0.0 ? (al += ao * 4.0) : (al += ao);
            if (w > 0.0) {
                ap = ((double)(this.noiseSizeY - ak) - ad) / w;
                al = MathHelper.clampedLerp(v, al, ap);
            }
            if (af > 0.0) {
                ap = ((double)ak - ag) / af;
                al = MathHelper.clampedLerp(ae, al, ap);
            }
            buffer[ak] = al;
        }
    }

    private double method_28553(int i, int j) {
        double d = this.field_24776.sample(i * 200, 10.0, j * 200, 1.0, 0.0, true);
        double e = d < 0.0 ? -d * 0.3 : d;
        double f = e * 24.575625 - 2.0;
        if (f < 0.0) {
            return f * 0.009486607142857142;
        }
        return Math.min(f, 1.0) * 0.006640625;
    }

    @Override
    public int getHeight(int x, int z, Heightmap.Type heightmapType) {
        return this.sampleHeightmap(x, z, null, heightmapType.getBlockPredicate());
    }

    @Override
    public BlockView getColumnSample(int x, int z) {
        BlockState[] blockStates = new BlockState[this.noiseSizeY * this.verticalNoiseResolution];
        this.sampleHeightmap(x, z, blockStates, null);
        return new VerticalBlockSample(blockStates);
    }

    private int sampleHeightmap(int x, int z, @Nullable BlockState[] states, @Nullable Predicate<BlockState> predicate) {
        int i = Math.floorDiv(x, this.horizontalNoiseResolution);
        int j = Math.floorDiv(z, this.horizontalNoiseResolution);
        int k = Math.floorMod(x, this.horizontalNoiseResolution);
        int l = Math.floorMod(z, this.horizontalNoiseResolution);
        double d = (double)k / (double)this.horizontalNoiseResolution;
        double e = (double)l / (double)this.horizontalNoiseResolution;
        double[][] ds = new double[][]{this.sampleNoiseColumn(i, j), this.sampleNoiseColumn(i, j + 1), this.sampleNoiseColumn(i + 1, j), this.sampleNoiseColumn(i + 1, j + 1)};
        for (int m = this.noiseSizeY - 1; m >= 0; --m) {
            double f = ds[0][m];
            double g = ds[1][m];
            double h = ds[2][m];
            double n = ds[3][m];
            double o = ds[0][m + 1];
            double p = ds[1][m + 1];
            double q = ds[2][m + 1];
            double r = ds[3][m + 1];
            for (int s = this.verticalNoiseResolution - 1; s >= 0; --s) {
                double t = (double)s / (double)this.verticalNoiseResolution;
                double u = MathHelper.lerp3(t, d, e, f, o, h, q, g, p, n, r);
                int v = m * this.verticalNoiseResolution + s;
                BlockState blockState = this.getBlockState(u, v);
                if (states != null) {
                    states[v] = blockState;
                }
                if (predicate == null || !predicate.test(blockState)) continue;
                return v + 1;
            }
        }
        return 0;
    }

    protected BlockState getBlockState(double density, int y) {
        BlockState blockState = density > 0.0 ? this.defaultBlock : (y < this.getSeaLevel() ? this.defaultFluid : AIR);
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
        ChunkGeneratorType chunkGeneratorType = this.field_24774.get();
        int k = chunkGeneratorType.getBedrockFloorY();
        int l = this.field_24779 - 1 - chunkGeneratorType.getBedrockCeilingY();
        int m = 5;
        boolean bl = l + 4 >= 0 && l < this.field_24779;
        boolean bl3 = bl2 = k + 4 >= 0 && k < this.field_24779;
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
        ObjectArrayList objectList = new ObjectArrayList(10);
        ObjectArrayList objectList2 = new ObjectArrayList(32);
        ChunkPos chunkPos = chunk.getPos();
        int i = chunkPos.x;
        int j = chunkPos.z;
        int k = i << 4;
        int l = j << 4;
        for (StructureFeature<?> structureFeature : StructureFeature.field_24861) {
            accessor.getStructuresWithChildren(ChunkSectionPos.from(chunkPos, 0), structureFeature).forEach(start -> {
                for (StructurePiece structurePiece : start.getChildren()) {
                    if (!structurePiece.intersectsChunk(chunkPos, 12)) continue;
                    if (structurePiece instanceof PoolStructurePiece) {
                        PoolStructurePiece poolStructurePiece = (PoolStructurePiece)structurePiece;
                        StructurePool.Projection projection = poolStructurePiece.getPoolElement().getProjection();
                        if (projection == StructurePool.Projection.RIGID) {
                            objectList.add(poolStructurePiece);
                        }
                        for (JigsawJunction jigsawJunction : poolStructurePiece.getJunctions()) {
                            int k = jigsawJunction.getSourceX();
                            int l = jigsawJunction.getSourceZ();
                            if (k <= k - 12 || l <= l - 12 || k >= k + 15 + 12 || l >= l + 15 + 12) continue;
                            objectList2.add(jigsawJunction);
                        }
                        continue;
                    }
                    objectList.add(structurePiece);
                }
            });
        }
        double[][][] ds = new double[2][this.noiseSizeZ + 1][this.noiseSizeY + 1];
        for (int m = 0; m < this.noiseSizeZ + 1; ++m) {
            ds[0][m] = new double[this.noiseSizeY + 1];
            this.sampleNoiseColumn(ds[0][m], i * this.noiseSizeX, j * this.noiseSizeZ + m);
            ds[1][m] = new double[this.noiseSizeY + 1];
        }
        ProtoChunk protoChunk = (ProtoChunk)chunk;
        Heightmap heightmap = protoChunk.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
        Heightmap heightmap2 = protoChunk.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        Iterator objectListIterator = objectList.iterator();
        Iterator objectListIterator2 = objectList2.iterator();
        for (int n = 0; n < this.noiseSizeX; ++n) {
            int o;
            for (o = 0; o < this.noiseSizeZ + 1; ++o) {
                this.sampleNoiseColumn(ds[1][o], i * this.noiseSizeX + n + 1, j * this.noiseSizeZ + o);
            }
            for (o = 0; o < this.noiseSizeZ; ++o) {
                ChunkSection chunkSection = protoChunk.getSection(15);
                chunkSection.lock();
                for (int p = this.noiseSizeY - 1; p >= 0; --p) {
                    double d = ds[0][o][p];
                    double e = ds[0][o + 1][p];
                    double f = ds[1][o][p];
                    double g = ds[1][o + 1][p];
                    double h = ds[0][o][p + 1];
                    double q = ds[0][o + 1][p + 1];
                    double r = ds[1][o][p + 1];
                    double s = ds[1][o + 1][p + 1];
                    for (int t = this.verticalNoiseResolution - 1; t >= 0; --t) {
                        int u = p * this.verticalNoiseResolution + t;
                        int v = u & 0xF;
                        int w = u >> 4;
                        if (chunkSection.getYOffset() >> 4 != w) {
                            chunkSection.unlock();
                            chunkSection = protoChunk.getSection(w);
                            chunkSection.lock();
                        }
                        double x = (double)t / (double)this.verticalNoiseResolution;
                        double y = MathHelper.lerp(x, d, h);
                        double z = MathHelper.lerp(x, f, r);
                        double aa = MathHelper.lerp(x, e, q);
                        double ab = MathHelper.lerp(x, g, s);
                        for (int ac = 0; ac < this.horizontalNoiseResolution; ++ac) {
                            int ad = k + n * this.horizontalNoiseResolution + ac;
                            int ae = ad & 0xF;
                            double af = (double)ac / (double)this.horizontalNoiseResolution;
                            double ag = MathHelper.lerp(af, y, z);
                            double ah = MathHelper.lerp(af, aa, ab);
                            for (int ai = 0; ai < this.horizontalNoiseResolution; ++ai) {
                                int ap;
                                int ao;
                                int aj = l + o * this.horizontalNoiseResolution + ai;
                                int ak = aj & 0xF;
                                double al = (double)ai / (double)this.horizontalNoiseResolution;
                                double am = MathHelper.lerp(al, ag, ah);
                                double an = MathHelper.clamp(am / 200.0, -1.0, 1.0);
                                an = an / 2.0 - an * an * an / 24.0;
                                while (objectListIterator.hasNext()) {
                                    StructurePiece structurePiece = (StructurePiece)objectListIterator.next();
                                    BlockBox blockBox = structurePiece.getBoundingBox();
                                    ao = Math.max(0, Math.max(blockBox.minX - ad, ad - blockBox.maxX));
                                    ap = u - (blockBox.minY + (structurePiece instanceof PoolStructurePiece ? ((PoolStructurePiece)structurePiece).getGroundLevelDelta() : 0));
                                    int aq = Math.max(0, Math.max(blockBox.minZ - aj, aj - blockBox.maxZ));
                                    an += SurfaceChunkGenerator.method_16572(ao, ap, aq) * 0.8;
                                }
                                objectListIterator.back(objectList.size());
                                while (objectListIterator2.hasNext()) {
                                    JigsawJunction jigsawJunction = (JigsawJunction)objectListIterator2.next();
                                    int ar = ad - jigsawJunction.getSourceX();
                                    ao = u - jigsawJunction.getSourceGroundY();
                                    ap = aj - jigsawJunction.getSourceZ();
                                    an += SurfaceChunkGenerator.method_16572(ar, ao, ap) * 0.4;
                                }
                                objectListIterator2.back(objectList2.size());
                                BlockState blockState = this.getBlockState(an, u);
                                if (blockState == AIR) continue;
                                if (blockState.getLuminance() != 0) {
                                    mutable.set(ad, u, aj);
                                    protoChunk.addLightSource(mutable);
                                }
                                chunkSection.setBlockState(ae, v, ak, blockState, false);
                                heightmap.trackUpdate(ae, u, ak, blockState);
                                heightmap2.trackUpdate(ae, u, ak, blockState);
                            }
                        }
                    }
                }
                chunkSection.unlock();
            }
            double[][] es = ds[0];
            ds[0] = ds[1];
            ds[1] = es;
        }
    }

    private static double method_16572(int i, int j, int k) {
        int l = i + 12;
        int m = j + 12;
        int n = k + 12;
        if (l < 0 || l >= 24) {
            return 0.0;
        }
        if (m < 0 || m >= 24) {
            return 0.0;
        }
        if (n < 0 || n >= 24) {
            return 0.0;
        }
        return field_16649[n * 24 * 24 + l * 24 + m];
    }

    private static double method_16571(int i, int j, int k) {
        double d = i * i + k * k;
        double e = (double)j + 0.5;
        double f = e * e;
        double g = Math.pow(Math.E, -(f / 16.0 + d / 16.0));
        double h = -e * MathHelper.fastInverseSqrt(f / 2.0 + d / 2.0) / 2.0;
        return h * g;
    }

    @Override
    public int getMaxY() {
        return this.field_24779;
    }

    @Override
    public int getSeaLevel() {
        return this.field_24774.get().method_28561();
    }

    @Override
    public List<Biome.SpawnEntry> getEntitySpawnList(Biome biome, StructureAccessor accessor, SpawnGroup group, BlockPos pos) {
        if (accessor.method_28388(pos, true, StructureFeature.SWAMP_HUT).hasChildren()) {
            if (group == SpawnGroup.MONSTER) {
                return StructureFeature.SWAMP_HUT.getMonsterSpawns();
            }
            if (group == SpawnGroup.CREATURE) {
                return StructureFeature.SWAMP_HUT.getCreatureSpawns();
            }
        }
        if (group == SpawnGroup.MONSTER) {
            if (accessor.method_28388(pos, false, StructureFeature.PILLAGER_OUTPOST).hasChildren()) {
                return StructureFeature.PILLAGER_OUTPOST.getMonsterSpawns();
            }
            if (accessor.method_28388(pos, false, StructureFeature.MONUMENT).hasChildren()) {
                return StructureFeature.MONUMENT.getMonsterSpawns();
            }
            if (accessor.method_28388(pos, true, StructureFeature.FORTRESS).hasChildren()) {
                return StructureFeature.FORTRESS.getMonsterSpawns();
            }
        }
        return super.getEntitySpawnList(biome, accessor, group, pos);
    }

    @Override
    public void populateEntities(ChunkRegion region) {
        if (this.field_24774.get().method_28562()) {
            return;
        }
        int i = region.getCenterChunkX();
        int j = region.getCenterChunkZ();
        Biome biome = region.getBiome(new ChunkPos(i, j).getCenterBlockPos());
        ChunkRandom chunkRandom = new ChunkRandom();
        chunkRandom.setPopulationSeed(region.getSeed(), i << 4, j << 4);
        SpawnHelper.populateEntities(region, biome, i, j, chunkRandom);
    }
}


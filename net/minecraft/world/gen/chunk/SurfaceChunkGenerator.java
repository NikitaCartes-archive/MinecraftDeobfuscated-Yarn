/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.chunk;

import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.JigsawJunction;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.NoiseSampler;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;
import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.StructureFeature;

public abstract class SurfaceChunkGenerator<T extends ChunkGeneratorConfig>
extends ChunkGenerator<T> {
    private static final float[] field_16649 = Util.make(new float[13824], fs -> {
        for (int i = 0; i < 24; ++i) {
            for (int j = 0; j < 24; ++j) {
                for (int k = 0; k < 24; ++k) {
                    fs[i * 24 * 24 + j * 24 + k] = (float)SurfaceChunkGenerator.method_16571(j - 12, k - 12, i - 12);
                }
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
    private final OctavePerlinNoiseSampler field_16574;
    private final OctavePerlinNoiseSampler field_16581;
    private final OctavePerlinNoiseSampler field_16575;
    private final NoiseSampler surfaceDepthNoise;
    protected final BlockState defaultBlock;
    protected final BlockState defaultFluid;

    public SurfaceChunkGenerator(IWorld world, BiomeSource biomeSource, int verticalNoiseResolution, int horizontalNoiseResolution, int worldHeight, T config, boolean useSimplexNoise) {
        super(world, biomeSource, config);
        this.verticalNoiseResolution = horizontalNoiseResolution;
        this.horizontalNoiseResolution = verticalNoiseResolution;
        this.defaultBlock = ((ChunkGeneratorConfig)config).getDefaultBlock();
        this.defaultFluid = ((ChunkGeneratorConfig)config).getDefaultFluid();
        this.noiseSizeX = 16 / this.horizontalNoiseResolution;
        this.noiseSizeY = worldHeight / this.verticalNoiseResolution;
        this.noiseSizeZ = 16 / this.horizontalNoiseResolution;
        this.random = new ChunkRandom(this.seed);
        this.field_16574 = new OctavePerlinNoiseSampler(this.random, 15, 0);
        this.field_16581 = new OctavePerlinNoiseSampler(this.random, 15, 0);
        this.field_16575 = new OctavePerlinNoiseSampler(this.random, 7, 0);
        this.surfaceDepthNoise = useSimplexNoise ? new OctaveSimplexNoiseSampler(this.random, 3, 0) : new OctavePerlinNoiseSampler(this.random, 3, 0);
    }

    private double sampleNoise(int x, int y, int z, double d, double e, double f, double g) {
        double h = 0.0;
        double i = 0.0;
        double j = 0.0;
        double k = 1.0;
        for (int l = 0; l < 16; ++l) {
            PerlinNoiseSampler perlinNoiseSampler3;
            PerlinNoiseSampler perlinNoiseSampler2;
            double m = OctavePerlinNoiseSampler.maintainPrecision((double)x * d * k);
            double n = OctavePerlinNoiseSampler.maintainPrecision((double)y * e * k);
            double o = OctavePerlinNoiseSampler.maintainPrecision((double)z * d * k);
            double p = e * k;
            PerlinNoiseSampler perlinNoiseSampler = this.field_16574.getOctave(l);
            if (perlinNoiseSampler != null) {
                h += perlinNoiseSampler.sample(m, n, o, p, (double)y * p) / k;
            }
            if ((perlinNoiseSampler2 = this.field_16581.getOctave(l)) != null) {
                i += perlinNoiseSampler2.sample(m, n, o, p, (double)y * p) / k;
            }
            if (l < 8 && (perlinNoiseSampler3 = this.field_16575.getOctave(l)) != null) {
                j += perlinNoiseSampler3.sample(OctavePerlinNoiseSampler.maintainPrecision((double)x * f * k), OctavePerlinNoiseSampler.maintainPrecision((double)y * g * k), OctavePerlinNoiseSampler.maintainPrecision((double)z * f * k), g * k, (double)y * g * k) / k;
            }
            k /= 2.0;
        }
        return MathHelper.clampedLerp(h / 512.0, i / 512.0, (j / 10.0 + 1.0) / 2.0);
    }

    protected double[] sampleNoiseColumn(int x, int z) {
        double[] ds = new double[this.noiseSizeY + 1];
        this.sampleNoiseColumn(ds, x, z);
        return ds;
    }

    protected void sampleNoiseColumn(double[] buffer, int x, int z, double d, double e, double f, double g, int i, int j) {
        double[] ds = this.computeNoiseRange(x, z);
        double h = ds[0];
        double k = ds[1];
        double l = this.method_16409();
        double m = this.method_16410();
        for (int n = 0; n < this.getNoiseSizeY(); ++n) {
            double o = this.sampleNoise(x, n, z, d, e, f, g);
            o -= this.computeNoiseFalloff(h, k, n);
            if ((double)n > l) {
                o = MathHelper.clampedLerp(o, j, ((double)n - l) / (double)i);
            } else if ((double)n < m) {
                o = MathHelper.clampedLerp(o, -30.0, (m - (double)n) / (m - 1.0));
            }
            buffer[n] = o;
        }
    }

    protected abstract double[] computeNoiseRange(int var1, int var2);

    protected abstract double computeNoiseFalloff(double var1, double var3, int var5);

    protected double method_16409() {
        return this.getNoiseSizeY() - 4;
    }

    protected double method_16410() {
        return 0.0;
    }

    @Override
    public int getHeightOnGround(int x, int z, Heightmap.Type heightmapType) {
        int i = Math.floorDiv(x, this.horizontalNoiseResolution);
        int j = Math.floorDiv(z, this.horizontalNoiseResolution);
        int k = Math.floorMod(x, this.horizontalNoiseResolution);
        int l = Math.floorMod(z, this.horizontalNoiseResolution);
        double d = (double)k / (double)this.horizontalNoiseResolution;
        double e = (double)l / (double)this.horizontalNoiseResolution;
        double[][] ds = new double[][]{this.sampleNoiseColumn(i, j), this.sampleNoiseColumn(i, j + 1), this.sampleNoiseColumn(i + 1, j), this.sampleNoiseColumn(i + 1, j + 1)};
        int m = this.getSeaLevel();
        for (int n = this.noiseSizeY - 1; n >= 0; --n) {
            double f = ds[0][n];
            double g = ds[1][n];
            double h = ds[2][n];
            double o = ds[3][n];
            double p = ds[0][n + 1];
            double q = ds[1][n + 1];
            double r = ds[2][n + 1];
            double s = ds[3][n + 1];
            for (int t = this.verticalNoiseResolution - 1; t >= 0; --t) {
                double u = (double)t / (double)this.verticalNoiseResolution;
                double v = MathHelper.lerp3(u, d, e, f, p, h, r, g, q, o, s);
                int w = n * this.verticalNoiseResolution + t;
                if (!(v > 0.0) && w >= m) continue;
                BlockState blockState = v > 0.0 ? this.defaultBlock : this.defaultFluid;
                if (!heightmapType.getBlockPredicate().test(blockState)) continue;
                return w + 1;
            }
        }
        return 0;
    }

    protected abstract void sampleNoiseColumn(double[] var1, int var2, int var3);

    public int getNoiseSizeY() {
        return this.noiseSizeY + 1;
    }

    @Override
    public void buildSurface(ChunkRegion chunkRegion, Chunk chunk) {
        ChunkPos chunkPos = chunk.getPos();
        int i = chunkPos.x;
        int j = chunkPos.z;
        ChunkRandom chunkRandom = new ChunkRandom();
        chunkRandom.setSeed(i, j);
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
                chunkRegion.getBiome(mutable.set(k + m, q, l + n)).buildSurface(chunkRandom, chunk, o, p, q, e, ((ChunkGeneratorConfig)this.getConfig()).getDefaultBlock(), ((ChunkGeneratorConfig)this.getConfig()).getDefaultFluid(), this.getSeaLevel(), this.world.getSeed());
            }
        }
        this.buildBedrock(chunk, chunkRandom);
    }

    protected void buildBedrock(Chunk chunk, Random random) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        int i = chunk.getPos().getStartX();
        int j = chunk.getPos().getStartZ();
        Object chunkGeneratorConfig = this.getConfig();
        int k = ((ChunkGeneratorConfig)chunkGeneratorConfig).getMinY();
        int l = ((ChunkGeneratorConfig)chunkGeneratorConfig).getMaxY();
        for (BlockPos blockPos : BlockPos.iterate(i, 0, j, i + 15, 0, j + 15)) {
            int m;
            if (l > 0) {
                for (m = l; m >= l - 4; --m) {
                    if (m < l - random.nextInt(5)) continue;
                    chunk.setBlockState(mutable.set(blockPos.getX(), m, blockPos.getZ()), Blocks.BEDROCK.getDefaultState(), false);
                }
            }
            if (k >= 256) continue;
            for (m = k + 4; m >= k; --m) {
                if (m > k + random.nextInt(5)) continue;
                chunk.setBlockState(mutable.set(blockPos.getX(), m, blockPos.getZ()), Blocks.BEDROCK.getDefaultState(), false);
            }
        }
    }

    @Override
    public void populateNoise(IWorld world, Chunk chunk) {
        int i = this.getSeaLevel();
        ObjectArrayList objectList = new ObjectArrayList(10);
        ObjectArrayList objectList2 = new ObjectArrayList(32);
        ChunkPos chunkPos = chunk.getPos();
        int j = chunkPos.x;
        int k = chunkPos.z;
        int l = j << 4;
        int m = k << 4;
        for (StructureFeature<?> structureFeature : Feature.JIGSAW_STRUCTURES) {
            String string = structureFeature.getName();
            LongIterator longIterator = chunk.getStructureReferences(string).iterator();
            while (longIterator.hasNext()) {
                long n = longIterator.nextLong();
                ChunkPos chunkPos2 = new ChunkPos(n);
                Chunk chunk2 = world.getChunk(chunkPos2.x, chunkPos2.z);
                StructureStart structureStart = chunk2.getStructureStart(string);
                if (structureStart == null || !structureStart.hasChildren()) continue;
                for (StructurePiece structurePiece : structureStart.getChildren()) {
                    if (!structurePiece.method_16654(chunkPos, 12) || !(structurePiece instanceof PoolStructurePiece)) continue;
                    PoolStructurePiece poolStructurePiece = (PoolStructurePiece)structurePiece;
                    StructurePool.Projection projection = poolStructurePiece.getPoolElement().getProjection();
                    if (projection == StructurePool.Projection.RIGID) {
                        objectList.add(poolStructurePiece);
                    }
                    for (JigsawJunction jigsawJunction : poolStructurePiece.getJunctions()) {
                        int o = jigsawJunction.getSourceX();
                        int p = jigsawJunction.getSourceZ();
                        if (o <= l - 12 || p <= m - 12 || o >= l + 15 + 12 || p >= m + 15 + 12) continue;
                        objectList2.add(jigsawJunction);
                    }
                }
            }
        }
        double[][][] ds = new double[2][this.noiseSizeZ + 1][this.noiseSizeY + 1];
        for (int q = 0; q < this.noiseSizeZ + 1; ++q) {
            ds[0][q] = new double[this.noiseSizeY + 1];
            this.sampleNoiseColumn(ds[0][q], j * this.noiseSizeX, k * this.noiseSizeZ + q);
            ds[1][q] = new double[this.noiseSizeY + 1];
        }
        ProtoChunk protoChunk = (ProtoChunk)chunk;
        Heightmap heightmap = protoChunk.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
        Heightmap heightmap2 = protoChunk.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        Iterator objectListIterator = objectList.iterator();
        Iterator objectListIterator2 = objectList2.iterator();
        for (int r = 0; r < this.noiseSizeX; ++r) {
            int s;
            for (s = 0; s < this.noiseSizeZ + 1; ++s) {
                this.sampleNoiseColumn(ds[1][s], j * this.noiseSizeX + r + 1, k * this.noiseSizeZ + s);
            }
            for (s = 0; s < this.noiseSizeZ; ++s) {
                ChunkSection chunkSection = protoChunk.getSection(15);
                chunkSection.lock();
                for (int t = this.noiseSizeY - 1; t >= 0; --t) {
                    double d = ds[0][s][t];
                    double e = ds[0][s + 1][t];
                    double f = ds[1][s][t];
                    double g = ds[1][s + 1][t];
                    double h = ds[0][s][t + 1];
                    double u = ds[0][s + 1][t + 1];
                    double v = ds[1][s][t + 1];
                    double w = ds[1][s + 1][t + 1];
                    for (int x = this.verticalNoiseResolution - 1; x >= 0; --x) {
                        int y = t * this.verticalNoiseResolution + x;
                        int z = y & 0xF;
                        int aa = y >> 4;
                        if (chunkSection.getYOffset() >> 4 != aa) {
                            chunkSection.unlock();
                            chunkSection = protoChunk.getSection(aa);
                            chunkSection.lock();
                        }
                        double ab = (double)x / (double)this.verticalNoiseResolution;
                        double ac = MathHelper.lerp(ab, d, h);
                        double ad = MathHelper.lerp(ab, f, v);
                        double ae = MathHelper.lerp(ab, e, u);
                        double af = MathHelper.lerp(ab, g, w);
                        for (int ag = 0; ag < this.horizontalNoiseResolution; ++ag) {
                            int ah = l + r * this.horizontalNoiseResolution + ag;
                            int ai = ah & 0xF;
                            double aj = (double)ag / (double)this.horizontalNoiseResolution;
                            double ak = MathHelper.lerp(aj, ac, ad);
                            double al = MathHelper.lerp(aj, ae, af);
                            for (int am = 0; am < this.horizontalNoiseResolution; ++am) {
                                int at;
                                int as;
                                int an = m + s * this.horizontalNoiseResolution + am;
                                int ao = an & 0xF;
                                double ap = (double)am / (double)this.horizontalNoiseResolution;
                                double aq = MathHelper.lerp(ap, ak, al);
                                double ar = MathHelper.clamp(aq / 200.0, -1.0, 1.0);
                                ar = ar / 2.0 - ar * ar * ar / 24.0;
                                while (objectListIterator.hasNext()) {
                                    PoolStructurePiece poolStructurePiece2 = (PoolStructurePiece)objectListIterator.next();
                                    BlockBox blockBox = poolStructurePiece2.getBoundingBox();
                                    as = Math.max(0, Math.max(blockBox.minX - ah, ah - blockBox.maxX));
                                    at = y - (blockBox.minY + poolStructurePiece2.getGroundLevelDelta());
                                    int au = Math.max(0, Math.max(blockBox.minZ - an, an - blockBox.maxZ));
                                    ar += SurfaceChunkGenerator.method_16572(as, at, au) * 0.8;
                                }
                                objectListIterator.back(objectList.size());
                                while (objectListIterator2.hasNext()) {
                                    JigsawJunction jigsawJunction2 = (JigsawJunction)objectListIterator2.next();
                                    int av = ah - jigsawJunction2.getSourceX();
                                    as = y - jigsawJunction2.getSourceGroundY();
                                    at = an - jigsawJunction2.getSourceZ();
                                    ar += SurfaceChunkGenerator.method_16572(av, as, at) * 0.4;
                                }
                                objectListIterator2.back(objectList2.size());
                                BlockState blockState = ar > 0.0 ? this.defaultBlock : (y < i ? this.defaultFluid : AIR);
                                if (blockState == AIR) continue;
                                if (blockState.getLuminance() != 0) {
                                    mutable.set(ah, y, an);
                                    protoChunk.addLightSource(mutable);
                                }
                                chunkSection.setBlockState(ai, z, ao, blockState, false);
                                heightmap.trackUpdate(ai, y, ao, blockState);
                                heightmap2.trackUpdate(ai, y, ao, blockState);
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
}


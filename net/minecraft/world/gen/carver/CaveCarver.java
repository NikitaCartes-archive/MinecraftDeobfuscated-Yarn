/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.carver;

import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.carver.CarverConfig;
import net.minecraft.world.gen.carver.CarverContext;

public class CaveCarver
extends Carver<CarverConfig> {
    public CaveCarver(Codec<CarverConfig> codec) {
        super(codec);
    }

    @Override
    public boolean shouldCarve(CarverConfig config, Random random) {
        return random.nextFloat() <= config.probability;
    }

    @Override
    public boolean carve(CarverContext context2, CarverConfig config, Chunk chunk, Function<BlockPos, Biome> posToBiome, Random random, int seaLevel, ChunkPos pos, BitSet carvingMask) {
        int i = ChunkSectionPos.getBlockCoord(this.getBranchFactor() * 2 - 1);
        int j = random.nextInt(random.nextInt(random.nextInt(this.getMaxCaveCount()) + 1) + 1);
        for (int k = 0; k < j; ++k) {
            float o;
            double d = pos.getOffsetX(random.nextInt(16));
            double e = this.getCaveY(context2, random);
            double f = pos.getOffsetZ(random.nextInt(16));
            double g = MathHelper.nextBetween(random, 0.2f, 1.8f);
            double h = MathHelper.nextBetween(random, 0.2f, 1.8f);
            double l = MathHelper.nextBetween(random, -1.0f, 0.0f);
            Carver.SkipPredicate skipPredicate = (context, scaledRelativeX, scaledRelativeY, scaledRelativeZ, y) -> CaveCarver.isPositionExcluded(scaledRelativeX, scaledRelativeY, scaledRelativeZ, l);
            int m = 1;
            if (random.nextInt(4) == 0) {
                double n = MathHelper.nextBetween(random, 0.1f, 0.9f);
                o = 1.0f + random.nextFloat() * 6.0f;
                this.carveCave(context2, config, chunk, posToBiome, random.nextLong(), seaLevel, d, e, f, o, n, carvingMask, skipPredicate);
                m += random.nextInt(4);
            }
            for (int p = 0; p < m; ++p) {
                float q = random.nextFloat() * ((float)Math.PI * 2);
                o = (random.nextFloat() - 0.5f) / 4.0f;
                float r = this.getTunnelSystemWidth(random);
                int s = i - random.nextInt(i / 4);
                boolean t = false;
                this.carveTunnels(context2, config, chunk, posToBiome, random.nextLong(), seaLevel, d, e, f, g, h, r, q, o, 0, s, this.getTunnelSystemHeightWidthRatio(), carvingMask, skipPredicate);
            }
        }
        return true;
    }

    protected int getMaxCaveCount() {
        return 15;
    }

    protected float getTunnelSystemWidth(Random random) {
        float f = random.nextFloat() * 2.0f + random.nextFloat();
        if (random.nextInt(10) == 0) {
            f *= random.nextFloat() * random.nextFloat() * 3.0f + 1.0f;
        }
        return f;
    }

    protected double getTunnelSystemHeightWidthRatio() {
        return 1.0;
    }

    protected int getCaveY(CarverContext context, Random random) {
        int i = context.getMinY() + 8;
        int j = 126;
        if (i > 126) {
            return i;
        }
        return MathHelper.nextBetween(random, i, 126);
    }

    protected void carveCave(CarverContext context, CarverConfig config, Chunk chunk, Function<BlockPos, Biome> posToBiome, long seed, int seaLevel, double x, double y, double z, float yaw, double yawPitchRatio, BitSet carvingMask, Carver.SkipPredicate skipPredicate) {
        double d = 1.5 + (double)(MathHelper.sin(1.5707964f) * yaw);
        double e = d * yawPitchRatio;
        this.carveRegion(context, config, chunk, posToBiome, seed, seaLevel, x + 1.0, y, z, d, e, carvingMask, skipPredicate);
    }

    protected void carveTunnels(CarverContext context, CarverConfig config, Chunk chunk, Function<BlockPos, Biome> posToBiome, long seed, int seaLevel, double x, double y, double z, double horizontalScale, double verticalScale, float width, float yaw, float pitch, int branchStartIndex, int branchCount, double yawPitchRatio, BitSet carvingMask, Carver.SkipPredicate skipPredicate) {
        Random random = new Random(seed);
        int i = random.nextInt(branchCount / 2) + branchCount / 4;
        boolean bl = random.nextInt(6) == 0;
        float f = 0.0f;
        float g = 0.0f;
        for (int j = branchStartIndex; j < branchCount; ++j) {
            double d = 1.5 + (double)(MathHelper.sin((float)Math.PI * (float)j / (float)branchCount) * width);
            double e = d * yawPitchRatio;
            float h = MathHelper.cos(pitch);
            x += (double)(MathHelper.cos(yaw) * h);
            y += (double)MathHelper.sin(pitch);
            z += (double)(MathHelper.sin(yaw) * h);
            pitch *= bl ? 0.92f : 0.7f;
            pitch += g * 0.1f;
            yaw += f * 0.1f;
            g *= 0.9f;
            f *= 0.75f;
            g += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0f;
            f += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0f;
            if (j == i && width > 1.0f) {
                this.carveTunnels(context, config, chunk, posToBiome, random.nextLong(), seaLevel, x, y, z, horizontalScale, verticalScale, random.nextFloat() * 0.5f + 0.5f, yaw - 1.5707964f, pitch / 3.0f, j, branchCount, 1.0, carvingMask, skipPredicate);
                this.carveTunnels(context, config, chunk, posToBiome, random.nextLong(), seaLevel, x, y, z, horizontalScale, verticalScale, random.nextFloat() * 0.5f + 0.5f, yaw + 1.5707964f, pitch / 3.0f, j, branchCount, 1.0, carvingMask, skipPredicate);
                return;
            }
            if (random.nextInt(4) == 0) continue;
            if (!CaveCarver.canCarveBranch(chunk.getPos(), x, z, j, branchCount, width)) {
                return;
            }
            this.carveRegion(context, config, chunk, posToBiome, seed, seaLevel, x, y, z, d * horizontalScale, e * verticalScale, carvingMask, skipPredicate);
        }
    }

    private static boolean isPositionExcluded(double scaledRelativeX, double scaledRelativeY, double scaledRelativeZ, double floorY) {
        if (scaledRelativeY <= floorY) {
            return true;
        }
        return scaledRelativeX * scaledRelativeX + scaledRelativeY * scaledRelativeY + scaledRelativeZ * scaledRelativeZ >= 1.0;
    }
}


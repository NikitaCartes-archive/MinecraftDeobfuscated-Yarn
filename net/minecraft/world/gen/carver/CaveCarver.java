/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.carver;

import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.class_6350;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.carver.CarverContext;
import net.minecraft.world.gen.carver.CaveCarverConfig;

public class CaveCarver
extends Carver<CaveCarverConfig> {
    public CaveCarver(Codec<CaveCarverConfig> codec) {
        super(codec);
    }

    @Override
    public boolean shouldCarve(CaveCarverConfig caveCarverConfig, Random random) {
        return random.nextFloat() <= caveCarverConfig.probability;
    }

    @Override
    public boolean carve(CarverContext carverContext, CaveCarverConfig caveCarverConfig, Chunk chunk, Function<BlockPos, Biome> function, Random random, class_6350 arg, ChunkPos chunkPos, BitSet bitSet) {
        int i = ChunkSectionPos.getBlockCoord(this.getBranchFactor() * 2 - 1);
        int j = random.nextInt(random.nextInt(random.nextInt(this.getMaxCaveCount()) + 1) + 1);
        for (int k = 0; k < j; ++k) {
            float o;
            double d = chunkPos.getOffsetX(random.nextInt(16));
            double e = caveCarverConfig.y.get(random, carverContext);
            double f = chunkPos.getOffsetZ(random.nextInt(16));
            double g = caveCarverConfig.horizontalRadiusMultiplier.get(random);
            double h = caveCarverConfig.verticalRadiusMultiplier.get(random);
            double l = caveCarverConfig.floorLevel.get(random);
            Carver.SkipPredicate skipPredicate = (context, scaledRelativeX, scaledRelativeY, scaledRelativeZ, y) -> CaveCarver.isPositionExcluded(scaledRelativeX, scaledRelativeY, scaledRelativeZ, l);
            int m = 1;
            if (random.nextInt(4) == 0) {
                double n = caveCarverConfig.yScale.get(random);
                o = 1.0f + random.nextFloat() * 6.0f;
                this.carveCave(carverContext, caveCarverConfig, chunk, function, random.nextLong(), arg, d, e, f, o, n, bitSet, skipPredicate);
                m += random.nextInt(4);
            }
            for (int p = 0; p < m; ++p) {
                float q = random.nextFloat() * ((float)Math.PI * 2);
                o = (random.nextFloat() - 0.5f) / 4.0f;
                float r = this.getTunnelSystemWidth(random);
                int s = i - random.nextInt(i / 4);
                boolean t = false;
                this.carveTunnels(carverContext, caveCarverConfig, chunk, function, random.nextLong(), arg, d, e, f, g, h, r, q, o, 0, s, this.getTunnelSystemHeightWidthRatio(), bitSet, skipPredicate);
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

    protected void carveCave(CarverContext context, CaveCarverConfig config, Chunk chunk, Function<BlockPos, Biome> posToBiome, long seed, class_6350 arg, double x, double y, double z, float yaw, double yawPitchRatio, BitSet carvingMask, Carver.SkipPredicate skipPredicate) {
        double d = 1.5 + (double)(MathHelper.sin(1.5707964f) * yaw);
        double e = d * yawPitchRatio;
        this.carveRegion(context, config, chunk, posToBiome, seed, arg, x + 1.0, y, z, d, e, carvingMask, skipPredicate);
    }

    protected void carveTunnels(CarverContext context, CaveCarverConfig config, Chunk chunk, Function<BlockPos, Biome> posToBiome, long seed, class_6350 arg, double x, double y, double z, double horizontalScale, double verticalScale, float width, float yaw, float pitch, int branchStartIndex, int branchCount, double yawPitchRatio, BitSet carvingMask, Carver.SkipPredicate skipPredicate) {
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
                this.carveTunnels(context, config, chunk, posToBiome, random.nextLong(), arg, x, y, z, horizontalScale, verticalScale, random.nextFloat() * 0.5f + 0.5f, yaw - 1.5707964f, pitch / 3.0f, j, branchCount, 1.0, carvingMask, skipPredicate);
                this.carveTunnels(context, config, chunk, posToBiome, random.nextLong(), arg, x, y, z, horizontalScale, verticalScale, random.nextFloat() * 0.5f + 0.5f, yaw + 1.5707964f, pitch / 3.0f, j, branchCount, 1.0, carvingMask, skipPredicate);
                return;
            }
            if (random.nextInt(4) == 0) continue;
            if (!CaveCarver.canCarveBranch(chunk.getPos(), x, z, j, branchCount, width)) {
                return;
            }
            this.carveRegion(context, config, chunk, posToBiome, seed, arg, x, y, z, d * horizontalScale, e * verticalScale, carvingMask, skipPredicate);
        }
    }

    private static boolean isPositionExcluded(double scaledRelativeX, double scaledRelativeY, double scaledRelativeZ, double floorY) {
        if (scaledRelativeY <= floorY) {
            return true;
        }
        return scaledRelativeX * scaledRelativeX + scaledRelativeY * scaledRelativeY + scaledRelativeZ * scaledRelativeZ >= 1.0;
    }
}


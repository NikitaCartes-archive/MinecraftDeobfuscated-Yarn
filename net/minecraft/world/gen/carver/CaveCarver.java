/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.carver;

import com.mojang.serialization.Codec;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.carver.CarverContext;
import net.minecraft.world.gen.carver.CarvingMask;
import net.minecraft.world.gen.carver.CaveCarverConfig;
import net.minecraft.world.gen.chunk.AquiferSampler;

public class CaveCarver
extends Carver<CaveCarverConfig> {
    public CaveCarver(Codec<CaveCarverConfig> codec) {
        super(codec);
    }

    @Override
    public boolean shouldCarve(CaveCarverConfig caveCarverConfig, AbstractRandom abstractRandom) {
        return abstractRandom.nextFloat() <= caveCarverConfig.probability;
    }

    @Override
    public boolean carve(CarverContext carverContext, CaveCarverConfig caveCarverConfig, Chunk chunk, Function<BlockPos, RegistryEntry<Biome>> function, AbstractRandom abstractRandom, AquiferSampler aquiferSampler, ChunkPos chunkPos, CarvingMask carvingMask) {
        int i = ChunkSectionPos.getBlockCoord(this.getBranchFactor() * 2 - 1);
        int j = abstractRandom.nextInt(abstractRandom.nextInt(abstractRandom.nextInt(this.getMaxCaveCount()) + 1) + 1);
        for (int k = 0; k < j; ++k) {
            float o;
            double d = chunkPos.getOffsetX(abstractRandom.nextInt(16));
            double e = caveCarverConfig.y.get(abstractRandom, carverContext);
            double f = chunkPos.getOffsetZ(abstractRandom.nextInt(16));
            double g = caveCarverConfig.horizontalRadiusMultiplier.get(abstractRandom);
            double h = caveCarverConfig.verticalRadiusMultiplier.get(abstractRandom);
            double l = caveCarverConfig.floorLevel.get(abstractRandom);
            Carver.SkipPredicate skipPredicate = (context, scaledRelativeX, scaledRelativeY, scaledRelativeZ, y) -> CaveCarver.isPositionExcluded(scaledRelativeX, scaledRelativeY, scaledRelativeZ, l);
            int m = 1;
            if (abstractRandom.nextInt(4) == 0) {
                double n = caveCarverConfig.yScale.get(abstractRandom);
                o = 1.0f + abstractRandom.nextFloat() * 6.0f;
                this.carveCave(carverContext, caveCarverConfig, chunk, function, aquiferSampler, d, e, f, o, n, carvingMask, skipPredicate);
                m += abstractRandom.nextInt(4);
            }
            for (int p = 0; p < m; ++p) {
                float q = abstractRandom.nextFloat() * ((float)Math.PI * 2);
                o = (abstractRandom.nextFloat() - 0.5f) / 4.0f;
                float r = this.getTunnelSystemWidth(abstractRandom);
                int s = i - abstractRandom.nextInt(i / 4);
                boolean t = false;
                this.carveTunnels(carverContext, caveCarverConfig, chunk, function, abstractRandom.nextLong(), aquiferSampler, d, e, f, g, h, r, q, o, 0, s, this.getTunnelSystemHeightWidthRatio(), carvingMask, skipPredicate);
            }
        }
        return true;
    }

    protected int getMaxCaveCount() {
        return 15;
    }

    protected float getTunnelSystemWidth(AbstractRandom random) {
        float f = random.nextFloat() * 2.0f + random.nextFloat();
        if (random.nextInt(10) == 0) {
            f *= random.nextFloat() * random.nextFloat() * 3.0f + 1.0f;
        }
        return f;
    }

    protected double getTunnelSystemHeightWidthRatio() {
        return 1.0;
    }

    protected void carveCave(CarverContext context, CaveCarverConfig config, Chunk chunk, Function<BlockPos, RegistryEntry<Biome>> posToBiome, AquiferSampler aquiferSampler, double d, double e, double f, float g, double h, CarvingMask mask, Carver.SkipPredicate skipPredicate) {
        double i = 1.5 + (double)(MathHelper.sin(1.5707964f) * g);
        double j = i * h;
        this.carveRegion(context, config, chunk, posToBiome, aquiferSampler, d + 1.0, e, f, i, j, mask, skipPredicate);
    }

    protected void carveTunnels(CarverContext context, CaveCarverConfig config, Chunk chunk, Function<BlockPos, RegistryEntry<Biome>> posToBiome, long seed, AquiferSampler aquiferSampler, double x, double y, double z, double horizontalScale, double verticalScale, float width, float yaw, float pitch, int branchStartIndex, int branchCount, double yawPitchRatio, CarvingMask mask, Carver.SkipPredicate skipPredicate) {
        AbstractRandom abstractRandom = AbstractRandom.createAtomic(seed);
        int i = abstractRandom.nextInt(branchCount / 2) + branchCount / 4;
        boolean bl = abstractRandom.nextInt(6) == 0;
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
            g += (abstractRandom.nextFloat() - abstractRandom.nextFloat()) * abstractRandom.nextFloat() * 2.0f;
            f += (abstractRandom.nextFloat() - abstractRandom.nextFloat()) * abstractRandom.nextFloat() * 4.0f;
            if (j == i && width > 1.0f) {
                this.carveTunnels(context, config, chunk, posToBiome, abstractRandom.nextLong(), aquiferSampler, x, y, z, horizontalScale, verticalScale, abstractRandom.nextFloat() * 0.5f + 0.5f, yaw - 1.5707964f, pitch / 3.0f, j, branchCount, 1.0, mask, skipPredicate);
                this.carveTunnels(context, config, chunk, posToBiome, abstractRandom.nextLong(), aquiferSampler, x, y, z, horizontalScale, verticalScale, abstractRandom.nextFloat() * 0.5f + 0.5f, yaw + 1.5707964f, pitch / 3.0f, j, branchCount, 1.0, mask, skipPredicate);
                return;
            }
            if (abstractRandom.nextInt(4) == 0) continue;
            if (!CaveCarver.canCarveBranch(chunk.getPos(), x, z, j, branchCount, width)) {
                return;
            }
            this.carveRegion(context, config, chunk, posToBiome, aquiferSampler, x, y, z, d * horizontalScale, e * verticalScale, mask, skipPredicate);
        }
    }

    private static boolean isPositionExcluded(double scaledRelativeX, double scaledRelativeY, double scaledRelativeZ, double floorY) {
        if (scaledRelativeY <= floorY) {
            return true;
        }
        return scaledRelativeX * scaledRelativeX + scaledRelativeY * scaledRelativeY + scaledRelativeZ * scaledRelativeZ >= 1.0;
    }
}


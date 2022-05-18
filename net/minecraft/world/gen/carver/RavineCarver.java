/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.carver;

import com.mojang.serialization.Codec;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.carver.CarverContext;
import net.minecraft.world.gen.carver.CarvingMask;
import net.minecraft.world.gen.carver.RavineCarverConfig;
import net.minecraft.world.gen.chunk.AquiferSampler;

public class RavineCarver
extends Carver<RavineCarverConfig> {
    public RavineCarver(Codec<RavineCarverConfig> codec) {
        super(codec);
    }

    @Override
    public boolean shouldCarve(RavineCarverConfig ravineCarverConfig, Random random) {
        return random.nextFloat() <= ravineCarverConfig.probability;
    }

    @Override
    public boolean carve(CarverContext carverContext, RavineCarverConfig ravineCarverConfig, Chunk chunk, Function<BlockPos, RegistryEntry<Biome>> function, Random random, AquiferSampler aquiferSampler, ChunkPos chunkPos, CarvingMask carvingMask) {
        int i = (this.getBranchFactor() * 2 - 1) * 16;
        double d = chunkPos.getOffsetX(random.nextInt(16));
        int j = ravineCarverConfig.y.get(random, carverContext);
        double e = chunkPos.getOffsetZ(random.nextInt(16));
        float f = random.nextFloat() * ((float)Math.PI * 2);
        float g = ravineCarverConfig.verticalRotation.get(random);
        double h = ravineCarverConfig.yScale.get(random);
        float k = ravineCarverConfig.shape.thickness.get(random);
        int l = (int)((float)i * ravineCarverConfig.shape.distanceFactor.get(random));
        boolean m = false;
        this.carveRavine(carverContext, ravineCarverConfig, chunk, function, random.nextLong(), aquiferSampler, d, j, e, k, f, g, 0, l, h, carvingMask);
        return true;
    }

    private void carveRavine(CarverContext context2, RavineCarverConfig config, Chunk chunk, Function<BlockPos, RegistryEntry<Biome>> posToBiome, long seed, AquiferSampler aquiferSampler, double x, double y2, double z, float width, float yaw, float pitch, int branchStartIndex, int branchCount, double yawPitchRatio, CarvingMask mask) {
        Random random = Random.create(seed);
        float[] fs = this.createHorizontalStretchFactors(context2, config, random);
        float f = 0.0f;
        float g = 0.0f;
        for (int i = branchStartIndex; i < branchCount; ++i) {
            double d = 1.5 + (double)(MathHelper.sin((float)i * (float)Math.PI / (float)branchCount) * width);
            double e = d * yawPitchRatio;
            d *= (double)config.shape.horizontalRadiusFactor.get(random);
            e = this.getVerticalScale(config, random, e, branchCount, i);
            float h = MathHelper.cos(pitch);
            float j = MathHelper.sin(pitch);
            x += (double)(MathHelper.cos(yaw) * h);
            y2 += (double)j;
            z += (double)(MathHelper.sin(yaw) * h);
            pitch *= 0.7f;
            pitch += g * 0.05f;
            yaw += f * 0.05f;
            g *= 0.8f;
            f *= 0.5f;
            g += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0f;
            f += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0f;
            if (random.nextInt(4) == 0) continue;
            if (!RavineCarver.canCarveBranch(chunk.getPos(), x, z, i, branchCount, width)) {
                return;
            }
            this.carveRegion(context2, config, chunk, posToBiome, aquiferSampler, x, y2, z, d, e, mask, (context, scaledRelativeX, scaledRelativeY, scaledRelativeZ, y) -> this.isPositionExcluded(context, fs, scaledRelativeX, scaledRelativeY, scaledRelativeZ, y));
        }
    }

    private float[] createHorizontalStretchFactors(CarverContext context, RavineCarverConfig config, Random random) {
        int i = context.getHeight();
        float[] fs = new float[i];
        float f = 1.0f;
        for (int j = 0; j < i; ++j) {
            if (j == 0 || random.nextInt(config.shape.widthSmoothness) == 0) {
                f = 1.0f + random.nextFloat() * random.nextFloat();
            }
            fs[j] = f * f;
        }
        return fs;
    }

    private double getVerticalScale(RavineCarverConfig config, Random random, double pitch, float branchCount, float branchIndex) {
        float f = 1.0f - MathHelper.abs(0.5f - branchIndex / branchCount) * 2.0f;
        float g = config.shape.verticalRadiusDefaultFactor + config.shape.verticalRadiusCenterFactor * f;
        return (double)g * pitch * (double)MathHelper.nextBetween(random, 0.75f, 1.0f);
    }

    private boolean isPositionExcluded(CarverContext context, float[] horizontalStretchFactors, double scaledRelativeX, double scaledRelativeY, double scaledRelativeZ, int y) {
        int i = y - context.getMinY();
        return (scaledRelativeX * scaledRelativeX + scaledRelativeZ * scaledRelativeZ) * (double)horizontalStretchFactors[i - 1] + scaledRelativeY * scaledRelativeY / 6.0 >= 1.0;
    }
}


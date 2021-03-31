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
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.carver.CarverContext;
import net.minecraft.world.gen.carver.RavineCarverConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RavineCarver
extends Carver<RavineCarverConfig> {
    private static final Logger LOGGER = LogManager.getLogger();

    public RavineCarver(Codec<RavineCarverConfig> codec) {
        super(codec);
    }

    @Override
    public boolean shouldCarve(RavineCarverConfig ravineCarverConfig, Random random) {
        return random.nextFloat() <= ravineCarverConfig.probability;
    }

    @Override
    public boolean carve(CarverContext carverContext, RavineCarverConfig ravineCarverConfig, Chunk chunk, Function<BlockPos, Biome> function, Random random, int i, ChunkPos chunkPos, BitSet bitSet) {
        int j = (this.getBranchFactor() * 2 - 1) * 16;
        double d = chunkPos.getOffsetX(random.nextInt(16));
        int k = ravineCarverConfig.field_31488.method_35391(random, carverContext);
        double e = chunkPos.getOffsetZ(random.nextInt(16));
        float f = random.nextFloat() * ((float)Math.PI * 2);
        float g = ravineCarverConfig.field_31479.get(random);
        double h = ravineCarverConfig.field_31489.get(random);
        float l = ravineCarverConfig.field_31480.field_31483.get(random);
        int m = (int)((float)j * ravineCarverConfig.field_31480.field_31482.get(random));
        boolean n = false;
        this.carveRavine(carverContext, ravineCarverConfig, chunk, function, random.nextLong(), i, d, k, e, l, f, g, 0, m, h, bitSet);
        return true;
    }

    private void carveRavine(CarverContext context2, RavineCarverConfig config, Chunk chunk, Function<BlockPos, Biome> posToBiome, long seed, int seaLevel, double x, double y2, double z, float width, float yaw, float pitch, int branchStartIndex, int branchCount, double yawPitchRatio, BitSet carvingMask) {
        Random random = new Random(seed);
        float[] fs = this.createHorizontalStretchFactors(context2, config, random);
        float f2 = 0.0f;
        float g = 0.0f;
        for (int i = branchStartIndex; i < branchCount; ++i) {
            double d2 = 1.5 + (double)(MathHelper.sin((float)i * (float)Math.PI / (float)branchCount) * width);
            double e2 = d2 * yawPitchRatio;
            d2 *= (double)config.field_31480.field_31485.get(random);
            e2 = this.getVerticalScale(config, random, e2, branchCount, i);
            float h = MathHelper.cos(pitch);
            float j = MathHelper.sin(pitch);
            x += (double)(MathHelper.cos(yaw) * h);
            y2 += (double)j;
            z += (double)(MathHelper.sin(yaw) * h);
            pitch *= 0.7f;
            pitch += g * 0.05f;
            yaw += f2 * 0.05f;
            g *= 0.8f;
            f2 *= 0.5f;
            g += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0f;
            f2 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0f;
            if (random.nextInt(4) == 0) continue;
            if (!RavineCarver.canCarveBranch(chunk.getPos(), x, z, i, branchCount, width)) {
                return;
            }
            this.carveRegion(context2, config, chunk, posToBiome, seed, seaLevel, x, y2, z, d2, e2, carvingMask, (context, d, e, f, y) -> this.isPositionExcluded(context, fs, d, e, f, y));
        }
    }

    private float[] createHorizontalStretchFactors(CarverContext context, RavineCarverConfig config, Random random) {
        int i = context.getMaxY();
        float[] fs = new float[i];
        float f = 1.0f;
        for (int j = 0; j < i; ++j) {
            if (j == 0 || random.nextInt(config.field_31480.field_31484) == 0) {
                f = 1.0f + random.nextFloat() * random.nextFloat();
            }
            fs[j] = f * f;
        }
        return fs;
    }

    private double getVerticalScale(RavineCarverConfig config, Random random, double pitch, float branchCount, float branchIndex) {
        float f = 1.0f - MathHelper.abs(0.5f - branchIndex / branchCount) * 2.0f;
        float g = config.field_31480.field_31486 + config.field_31480.field_31487 * f;
        return (double)g * pitch * (double)MathHelper.nextBetween(random, 0.75f, 1.0f);
    }

    private boolean isPositionExcluded(CarverContext context, float[] horizontalStretchFactors, double scaledRelativeX, double scaledRelativeY, double scaledRelativeZ, int y) {
        int i = y - context.getMinY();
        return (scaledRelativeX * scaledRelativeX + scaledRelativeZ * scaledRelativeZ) * (double)horizontalStretchFactors[i - 1] + scaledRelativeY * scaledRelativeY / 6.0 >= 1.0;
    }
}


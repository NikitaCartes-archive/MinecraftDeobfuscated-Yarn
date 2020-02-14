/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.BranchedTreeFeatureConfig;

public abstract class BranchedTreeFeature<T extends BranchedTreeFeatureConfig>
extends AbstractTreeFeature<T> {
    public BranchedTreeFeature(Function<Dynamic<?>, ? extends T> function) {
        super(function);
    }

    protected void generate(ModifiableTestableWorld world, Random random, int height, BlockPos pos, int trunkTopOffset, Set<BlockPos> logPositions, BlockBox blockBox, BranchedTreeFeatureConfig config) {
        for (int i = 0; i < height - trunkTopOffset; ++i) {
            this.setLogBlockState(world, random, pos.up(i), logPositions, blockBox, config);
        }
    }

    public Optional<BlockPos> findPositionToGenerate(ModifiableTestableWorld world, int height, int i, int j, BlockPos pos, BranchedTreeFeatureConfig config) {
        BlockPos blockPos;
        int l;
        int k;
        if (!config.field_21593) {
            k = world.getTopPosition(Heightmap.Type.OCEAN_FLOOR, pos).getY();
            l = world.getTopPosition(Heightmap.Type.WORLD_SURFACE, pos).getY();
            blockPos = new BlockPos(pos.getX(), k, pos.getZ());
            if (l - k > config.maxWaterDepth) {
                return Optional.empty();
            }
        } else {
            blockPos = pos;
        }
        if (blockPos.getY() < 1 || blockPos.getY() + height + 1 > 256) {
            return Optional.empty();
        }
        for (k = 0; k <= height + 1; ++k) {
            l = config.foliagePlacer.method_23447(i, height, j, k);
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            for (int m = -l; m <= l; ++m) {
                for (int n = -l; n <= l; ++n) {
                    if (k + blockPos.getY() >= 0 && k + blockPos.getY() < 256) {
                        mutable.set(m + blockPos.getX(), k + blockPos.getY(), n + blockPos.getZ());
                        if (BranchedTreeFeature.canTreeReplace(world, mutable) && (config.noVines || !BranchedTreeFeature.isVine(world, mutable))) continue;
                        return Optional.empty();
                    }
                    return Optional.empty();
                }
            }
        }
        if (!BranchedTreeFeature.isDirtOrGrass(world, blockPos.down()) || blockPos.getY() >= 256 - height - 1) {
            return Optional.empty();
        }
        return Optional.of(blockPos);
    }
}


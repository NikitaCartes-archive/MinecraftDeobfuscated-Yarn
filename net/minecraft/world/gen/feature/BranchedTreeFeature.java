/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Optional;
import java.util.function.Function;
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

    public Optional<BlockPos> findPositionToGenerate(ModifiableTestableWorld world, int trunkHeight, int baseHeight, BlockPos pos, BranchedTreeFeatureConfig config) {
        BlockPos blockPos;
        int j;
        int i;
        if (!config.skipFluidCheck) {
            i = world.getTopPosition(Heightmap.Type.OCEAN_FLOOR, pos).getY();
            j = world.getTopPosition(Heightmap.Type.WORLD_SURFACE, pos).getY();
            blockPos = new BlockPos(pos.getX(), i, pos.getZ());
            if (j - i > config.maxFluidDepth) {
                return Optional.empty();
            }
        } else {
            blockPos = pos;
        }
        if (blockPos.getY() < 1 || blockPos.getY() + trunkHeight + 1 > 256) {
            return Optional.empty();
        }
        for (i = 0; i <= trunkHeight + 1; ++i) {
            j = config.foliagePlacer.getRadiusForPlacement(trunkHeight, baseHeight, i);
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            for (int k = -j; k <= j; ++k) {
                for (int l = -j; l <= j; ++l) {
                    if (i + blockPos.getY() >= 0 && i + blockPos.getY() < 256) {
                        mutable.set(k + blockPos.getX(), i + blockPos.getY(), l + blockPos.getZ());
                        if (BranchedTreeFeature.canTreeReplace(world, mutable) && (config.noVines || !BranchedTreeFeature.isVine(world, mutable))) continue;
                        return Optional.empty();
                    }
                    return Optional.empty();
                }
            }
        }
        if (!BranchedTreeFeature.isDirtOrGrass(world, blockPos.down()) || blockPos.getY() >= 256 - trunkHeight - 1) {
            return Optional.empty();
        }
        return Optional.of(blockPos);
    }
}


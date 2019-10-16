/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

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
import net.minecraft.world.gen.feature.NormalTreeFeatureConfig;

public abstract class class_4626<T extends NormalTreeFeatureConfig>
extends AbstractTreeFeature<T> {
    public class_4626(Function<Dynamic<?>, ? extends T> function) {
        super(function);
    }

    protected void method_23379(ModifiableTestableWorld modifiableTestableWorld, Random random, int i, BlockPos blockPos, int j, Set<BlockPos> set, BlockBox blockBox, NormalTreeFeatureConfig normalTreeFeatureConfig) {
        for (int k = 0; k < i - j; ++k) {
            this.method_23382(modifiableTestableWorld, random, blockPos.up(k), set, blockBox, normalTreeFeatureConfig);
        }
    }

    public Optional<BlockPos> method_23378(ModifiableTestableWorld modifiableTestableWorld, int i, int j, int k, BlockPos blockPos, NormalTreeFeatureConfig normalTreeFeatureConfig) {
        int l = modifiableTestableWorld.getTopPosition(Heightmap.Type.OCEAN_FLOOR, blockPos).getY();
        int m = modifiableTestableWorld.getTopPosition(Heightmap.Type.WORLD_SURFACE, blockPos).getY();
        BlockPos blockPos2 = new BlockPos(blockPos.getX(), l, blockPos.getZ());
        if (m - l > normalTreeFeatureConfig.field_21268) {
            return Optional.empty();
        }
        if (blockPos2.getY() < 1 || blockPos2.getY() + i + 1 > 256) {
            return Optional.empty();
        }
        for (int n = 0; n <= i + 1; ++n) {
            int o = normalTreeFeatureConfig.foliagePlacer.method_23447(j, i, k, n);
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            for (int p = -o; p <= o; ++p) {
                for (int q = -o; q <= o; ++q) {
                    if (n + blockPos2.getY() >= 0 && n + blockPos2.getY() < 256) {
                        mutable.set(p + blockPos2.getX(), n + blockPos2.getY(), q + blockPos2.getZ());
                        if (class_4626.canTreeReplace(modifiableTestableWorld, mutable) && (normalTreeFeatureConfig.field_21269 || !class_4626.isLeaves(modifiableTestableWorld, mutable))) continue;
                        return Optional.empty();
                    }
                    return Optional.empty();
                }
            }
        }
        if (!class_4626.isDirtOrGrass(modifiableTestableWorld, blockPos2.method_10074()) || blockPos2.getY() >= 256 - i - 1) {
            return Optional.empty();
        }
        return Optional.of(blockPos2);
    }
}


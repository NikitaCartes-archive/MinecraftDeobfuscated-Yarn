/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.foliage;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.DynamicSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.BranchedTreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacerType;

public abstract class FoliagePlacer
implements DynamicSerializable {
    protected final int radius;
    protected final int randomRadius;
    protected final FoliagePlacerType<?> type;

    public FoliagePlacer(int radius, int randomRadius, FoliagePlacerType<?> type) {
        this.radius = radius;
        this.randomRadius = randomRadius;
        this.type = type;
    }

    public abstract void generate(ModifiableTestableWorld var1, Random var2, BranchedTreeFeatureConfig var3, int var4, int var5, int var6, BlockPos var7, Set<BlockPos> var8);

    public abstract int getRadius(Random var1, int var2, int var3, BranchedTreeFeatureConfig var4);

    protected abstract boolean method_23451(Random var1, int var2, int var3, int var4, int var5, int var6);

    public abstract int method_23447(int var1, int var2, int var3, int var4);

    protected void generate(ModifiableTestableWorld modifiableTestableWorld, Random random, BranchedTreeFeatureConfig branchedTreeFeatureConfig, int i, BlockPos blockPos, int j, int k, Set<BlockPos> set) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int l = -k; l <= k; ++l) {
            for (int m = -k; m <= k; ++m) {
                if (this.method_23451(random, i, l, j, m, k)) continue;
                mutable.set(l + blockPos.getX(), j + blockPos.getY(), m + blockPos.getZ());
                this.method_23450(modifiableTestableWorld, random, mutable, branchedTreeFeatureConfig, set);
            }
        }
    }

    protected void method_23450(ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos, BranchedTreeFeatureConfig branchedTreeFeatureConfig, Set<BlockPos> set) {
        if (AbstractTreeFeature.isAirOrLeaves(modifiableTestableWorld, blockPos) || AbstractTreeFeature.isReplaceablePlant(modifiableTestableWorld, blockPos) || AbstractTreeFeature.isWater(modifiableTestableWorld, blockPos)) {
            modifiableTestableWorld.setBlockState(blockPos, branchedTreeFeatureConfig.leavesProvider.getBlockState(random, blockPos), 19);
            set.add(blockPos.toImmutable());
        }
    }

    @Override
    public <T> T serialize(DynamicOps<T> ops) {
        ImmutableMap.Builder<T, T> builder = ImmutableMap.builder();
        builder.put(ops.createString("type"), ops.createString(Registry.FOLIAGE_PLACER_TYPE.getId(this.type).toString())).put(ops.createString("radius"), ops.createInt(this.radius)).put(ops.createString("radius_random"), ops.createInt(this.randomRadius));
        return new Dynamic<T>(ops, ops.createMap(builder.build())).getValue();
    }
}


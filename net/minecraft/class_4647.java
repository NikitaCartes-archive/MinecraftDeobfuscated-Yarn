/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import java.util.Set;
import net.minecraft.class_4640;
import net.minecraft.class_4648;
import net.minecraft.util.DynamicSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.AbstractTreeFeature;

public abstract class class_4647
implements DynamicSerializable {
    protected final int field_21296;
    protected final int field_21297;
    protected final class_4648<?> field_21298;

    public class_4647(int i, int j, class_4648<?> arg) {
        this.field_21296 = i;
        this.field_21297 = j;
        this.field_21298 = arg;
    }

    public abstract void method_23448(ModifiableTestableWorld var1, Random var2, class_4640 var3, int var4, int var5, int var6, BlockPos var7, Set<BlockPos> var8);

    public abstract int method_23452(Random var1, int var2, int var3, class_4640 var4);

    protected abstract boolean method_23451(Random var1, int var2, int var3, int var4, int var5, int var6);

    public abstract int method_23447(int var1, int var2, int var3, int var4);

    protected void method_23449(ModifiableTestableWorld modifiableTestableWorld, Random random, class_4640 arg, int i, BlockPos blockPos, int j, int k, Set<BlockPos> set) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int l = -k; l <= k; ++l) {
            for (int m = -k; m <= k; ++m) {
                if (this.method_23451(random, i, l, j, m, k)) continue;
                mutable.set(l + blockPos.getX(), j + blockPos.getY(), m + blockPos.getZ());
                this.method_23450(modifiableTestableWorld, random, mutable, arg, set);
            }
        }
    }

    protected void method_23450(ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos, class_4640 arg, Set<BlockPos> set) {
        if (AbstractTreeFeature.isAirOrLeaves(modifiableTestableWorld, blockPos) || AbstractTreeFeature.isReplaceablePlant(modifiableTestableWorld, blockPos) || AbstractTreeFeature.isWater(modifiableTestableWorld, blockPos)) {
            modifiableTestableWorld.setBlockState(blockPos, arg.field_21289.method_23455(random, blockPos), 19);
            set.add(blockPos);
        }
    }

    @Override
    public <T> T serialize(DynamicOps<T> dynamicOps) {
        ImmutableMap.Builder<T, T> builder = ImmutableMap.builder();
        builder.put(dynamicOps.createString("type"), dynamicOps.createString(Registry.FOLIAGE_PLACER_TYPE.getId(this.field_21298).toString())).put(dynamicOps.createString("radius"), dynamicOps.createInt(this.field_21296)).put(dynamicOps.createString("radius_random"), dynamicOps.createInt(this.field_21296));
        return new Dynamic<T>(dynamicOps, dynamicOps.createMap(builder.build())).getValue();
    }
}


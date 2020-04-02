/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.trunk;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.BranchedTreeFeatureConfig;
import net.minecraft.world.gen.trunk.TrunkPlacerType;

public abstract class TrunkPlacer {
    private final int baseHeight;
    private final int firstRandomHeight;
    private final int secondRandomHeight;
    protected final TrunkPlacerType<?> type;

    public TrunkPlacer(int baseHeight, int firstRandomHeight, int secondRandomHeight, TrunkPlacerType<?> type) {
        this.baseHeight = baseHeight;
        this.firstRandomHeight = firstRandomHeight;
        this.secondRandomHeight = secondRandomHeight;
        this.type = type;
    }

    /**
     * Generates the trunk blocks and return a map from branch tops to the
     * radius allowed for foliage generation on that branch top.
     */
    public abstract Map<BlockPos, Integer> generate(ModifiableTestableWorld var1, Random var2, int var3, BlockPos var4, int var5, Set<BlockPos> var6, BlockBox var7, BranchedTreeFeatureConfig var8);

    public int getBaseHeight() {
        return this.baseHeight;
    }

    public int getHeight(Random random, BranchedTreeFeatureConfig config) {
        return this.baseHeight + random.nextInt(this.firstRandomHeight + 1) + random.nextInt(this.secondRandomHeight + 1);
    }

    public <T> T serialize(DynamicOps<T> ops) {
        ImmutableMap.Builder<T, T> builder = ImmutableMap.builder();
        builder.put(ops.createString("type"), ops.createString(Registry.TRUNK_PLACER_TYPE.getId(this.type).toString())).put(ops.createString("base_height"), ops.createInt(this.baseHeight)).put(ops.createString("height_rand_a"), ops.createInt(this.firstRandomHeight)).put(ops.createString("height_rand_b"), ops.createInt(this.secondRandomHeight));
        return new Dynamic<T>(ops, ops.createMap(builder.build())).getValue();
    }
}


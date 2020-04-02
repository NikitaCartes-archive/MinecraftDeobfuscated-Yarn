/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.foliage;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.dynamic.DynamicSerializable;
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
    protected final int offset;
    protected final int randomOffset;
    protected final FoliagePlacerType<?> type;

    public FoliagePlacer(int radius, int randomRadius, int offset, int randomOffset, FoliagePlacerType<?> type) {
        this.radius = radius;
        this.randomRadius = randomRadius;
        this.offset = offset;
        this.randomOffset = randomOffset;
        this.type = type;
    }

    /**
     * This is the main method used to generate foliage.
     */
    public abstract void generate(ModifiableTestableWorld var1, Random var2, BranchedTreeFeatureConfig var3, int var4, BlockPos var5, int var6, int var7, Set<BlockPos> var8);

    public abstract int getHeight(Random var1, int var2);

    public abstract int getRadius(Random var1, int var2, BranchedTreeFeatureConfig var3);

    protected abstract boolean isInvalidForLeaves(Random var1, int var2, int var3, int var4, int var5, int var6);

    /**
     * This method is used to ensure that a tree can place foliage when it generates.
     * 
     * <p>It runs for every y-level of the tree being generated.
     * 
     * @param trunkHeight the height of the trunk
     * @param baseHeight the height of the full tree
     * @param radius the radius of the foliage
     */
    public abstract int getRadiusForPlacement(int var1, int var2, int var3);

    protected void generate(ModifiableTestableWorld world, Random random, BranchedTreeFeatureConfig config, BlockPos pos, int baseHeight, int y, int radius, Set<BlockPos> leaves) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int i = -radius; i <= radius; ++i) {
            for (int j = -radius; j <= radius; ++j) {
                if (this.isInvalidForLeaves(random, baseHeight, i, y, j, radius)) continue;
                mutable.set(i + pos.getX(), y + pos.getY() - baseHeight, j + pos.getZ());
                this.placeLeaves(world, random, mutable, config, leaves);
            }
        }
    }

    protected void placeLeaves(ModifiableTestableWorld world, Random random, BlockPos pos, BranchedTreeFeatureConfig config, Set<BlockPos> leaves) {
        if (AbstractTreeFeature.isAirOrLeaves(world, pos) || AbstractTreeFeature.isReplaceablePlant(world, pos) || AbstractTreeFeature.isWater(world, pos)) {
            world.setBlockState(pos, config.leavesProvider.getBlockState(random, pos), 19);
            leaves.add(pos.toImmutable());
        }
    }

    @Override
    public <T> T serialize(DynamicOps<T> ops) {
        ImmutableMap.Builder<T, T> builder = ImmutableMap.builder();
        builder.put(ops.createString("type"), ops.createString(Registry.FOLIAGE_PLACER_TYPE.getId(this.type).toString())).put(ops.createString("radius"), ops.createInt(this.radius)).put(ops.createString("radius_random"), ops.createInt(this.randomRadius)).put(ops.createString("offset"), ops.createInt(this.offset)).put(ops.createString("offset_random"), ops.createInt(this.randomOffset));
        return new Dynamic<T>(ops, ops.createMap(builder.build())).getValue();
    }
}


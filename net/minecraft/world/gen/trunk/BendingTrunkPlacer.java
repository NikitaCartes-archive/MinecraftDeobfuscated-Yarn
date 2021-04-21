/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.trunk;

import com.google.common.collect.Lists;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import net.minecraft.block.BlockState;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.trunk.TrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacerType;

public class BendingTrunkPlacer
extends TrunkPlacer {
    public static final Codec<BendingTrunkPlacer> CODEC = RecordCodecBuilder.create(instance -> BendingTrunkPlacer.fillTrunkPlacerFields(instance).and(instance.group(Codecs.field_33442.optionalFieldOf("min_height_for_leaves", 1).forGetter(placer -> placer.minHeightForLeaves), ((MapCodec)IntProvider.createValidatingCodec(1, 64).fieldOf("bend_length")).forGetter(placer -> placer.bendLength))).apply((Applicative<BendingTrunkPlacer, ?>)instance, BendingTrunkPlacer::new));
    private final int minHeightForLeaves;
    private final IntProvider bendLength;

    public BendingTrunkPlacer(int baseHeight, int firstRandomHeight, int secondRandomHeight, int minHeightForLeaves, IntProvider bendLength) {
        super(baseHeight, firstRandomHeight, secondRandomHeight);
        this.minHeightForLeaves = minHeightForLeaves;
        this.bendLength = bendLength;
    }

    @Override
    protected TrunkPlacerType<?> getType() {
        return TrunkPlacerType.BENDING_TRUNK_PLACER;
    }

    @Override
    public List<FoliagePlacer.TreeNode> generate(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, int height, BlockPos startPos, TreeFeatureConfig config) {
        int j;
        Direction direction = Direction.Type.HORIZONTAL.random(random);
        int i = height - 1;
        BlockPos.Mutable mutable = startPos.mutableCopy();
        Vec3i blockPos = mutable.down();
        BendingTrunkPlacer.setToDirt(world, replacer, random, (BlockPos)blockPos, config);
        ArrayList<FoliagePlacer.TreeNode> list = Lists.newArrayList();
        for (j = 0; j <= i; ++j) {
            if (j + 1 >= i + random.nextInt(2)) {
                mutable.move(direction);
            }
            if (TreeFeature.canReplace(world, mutable)) {
                BendingTrunkPlacer.getAndSetState(world, replacer, random, mutable, config);
            }
            if (j >= this.minHeightForLeaves) {
                list.add(new FoliagePlacer.TreeNode(mutable.toImmutable(), 0, false));
            }
            mutable.move(Direction.UP);
        }
        j = this.bendLength.get(random);
        for (int k = 0; k <= j; ++k) {
            if (TreeFeature.canReplace(world, mutable)) {
                BendingTrunkPlacer.getAndSetState(world, replacer, random, mutable, config);
            }
            list.add(new FoliagePlacer.TreeNode(mutable.toImmutable(), 0, false));
            mutable.move(direction);
        }
        return list;
    }
}


/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.trunk;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.trunk.TrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacerType;

public class CherryTrunkPlacer
extends TrunkPlacer {
    private static final Codec<UniformIntProvider> BRANCH_START_OFFSET_FROM_TOP_CODEC = Codecs.validate(UniformIntProvider.CODEC, branchStartOffsetFromTop -> {
        if (branchStartOffsetFromTop.getMax() - branchStartOffsetFromTop.getMin() < 1) {
            return DataResult.error("Need at least 2 blocks variation for the branch starts to fit both branches");
        }
        return DataResult.success(branchStartOffsetFromTop);
    });
    public static final Codec<CherryTrunkPlacer> CODEC = RecordCodecBuilder.create(instance -> CherryTrunkPlacer.fillTrunkPlacerFields(instance).and(instance.group(((MapCodec)IntProvider.createValidatingCodec(1, 3).fieldOf("branch_count")).forGetter(trunkPlacer -> trunkPlacer.branchCount), ((MapCodec)IntProvider.createValidatingCodec(2, 16).fieldOf("branch_horizontal_length")).forGetter(trunkPlacer -> trunkPlacer.branchHorizontalLength), ((MapCodec)IntProvider.createValidatingCodec(-16, 0, BRANCH_START_OFFSET_FROM_TOP_CODEC).fieldOf("branch_start_offset_from_top")).forGetter(trunkPlacer -> trunkPlacer.branchStartOffsetFromTop), ((MapCodec)IntProvider.createValidatingCodec(-16, 16).fieldOf("branch_end_offset_from_top")).forGetter(trunkPlacer -> trunkPlacer.branchEndOffsetFromTop))).apply((Applicative<CherryTrunkPlacer, ?>)instance, CherryTrunkPlacer::new));
    private final IntProvider branchCount;
    private final IntProvider branchHorizontalLength;
    private final UniformIntProvider branchStartOffsetFromTop;
    private final UniformIntProvider secondBranchStartOffsetFromTop;
    private final IntProvider branchEndOffsetFromTop;

    public CherryTrunkPlacer(int baseHeight, int firstRandomHeight, int secondRandomHeight, IntProvider branchCount, IntProvider branchHorizontalLength, UniformIntProvider branchStartOffsetFromTop, IntProvider branchEndOffsetFromTop) {
        super(baseHeight, firstRandomHeight, secondRandomHeight);
        this.branchCount = branchCount;
        this.branchHorizontalLength = branchHorizontalLength;
        this.branchStartOffsetFromTop = branchStartOffsetFromTop;
        this.secondBranchStartOffsetFromTop = UniformIntProvider.create(branchStartOffsetFromTop.getMin(), branchStartOffsetFromTop.getMax() - 1);
        this.branchEndOffsetFromTop = branchEndOffsetFromTop;
    }

    @Override
    protected TrunkPlacerType<?> getType() {
        return TrunkPlacerType.CHERRY_TRUNK_PLACER;
    }

    @Override
    public List<FoliagePlacer.TreeNode> generate(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, int height, BlockPos startPos, TreeFeatureConfig config) {
        boolean bl2;
        int k;
        CherryTrunkPlacer.setToDirt(world, replacer, random, startPos.down(), config);
        int i = Math.max(0, height - 1 + this.branchStartOffsetFromTop.get(random));
        int j = Math.max(0, height - 1 + this.secondBranchStartOffsetFromTop.get(random));
        if (j >= i) {
            ++j;
        }
        boolean bl = (k = this.branchCount.get(random)) == 3;
        boolean bl3 = bl2 = k >= 2;
        int l = bl ? height : (bl2 ? Math.max(i, j) + 1 : i + 1);
        for (int m = 0; m < l; ++m) {
            this.getAndSetState(world, replacer, random, startPos.up(m), config);
        }
        ArrayList<FoliagePlacer.TreeNode> list = new ArrayList<FoliagePlacer.TreeNode>();
        if (bl) {
            list.add(new FoliagePlacer.TreeNode(startPos.up(l), 0, false));
        }
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        Direction direction = Direction.Type.HORIZONTAL.random(random);
        Function<BlockState, BlockState> function = state -> (BlockState)state.withIfExists(PillarBlock.AXIS, direction.getAxis());
        list.add(this.generateBranch(world, replacer, random, height, startPos, config, function, direction, i, i < l - 1, mutable));
        if (bl2) {
            list.add(this.generateBranch(world, replacer, random, height, startPos, config, function, direction.getOpposite(), j, j < l - 1, mutable));
        }
        return list;
    }

    private FoliagePlacer.TreeNode generateBranch(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, int height, BlockPos startPos, TreeFeatureConfig config, Function<BlockState, BlockState> withAxisFunction, Direction direction, int branchStartOffset, boolean branchBelowHeight, BlockPos.Mutable mutablePos) {
        int m;
        Direction direction2;
        mutablePos.set(startPos).move(Direction.UP, branchStartOffset);
        int i = height - 1 + this.branchEndOffsetFromTop.get(random);
        boolean bl = branchBelowHeight || i < branchStartOffset;
        int j = this.branchHorizontalLength.get(random) + (bl ? 1 : 0);
        BlockPos blockPos = startPos.offset(direction, j).up(i);
        int k = bl ? 2 : 1;
        for (int l = 0; l < k; ++l) {
            this.getAndSetState(world, replacer, random, mutablePos.move(direction), config, withAxisFunction);
        }
        Direction direction3 = direction2 = blockPos.getY() > mutablePos.getY() ? Direction.UP : Direction.DOWN;
        while ((m = mutablePos.getManhattanDistance(blockPos)) != 0) {
            float f = (float)Math.abs(blockPos.getY() - mutablePos.getY()) / (float)m;
            boolean bl2 = random.nextFloat() < f;
            mutablePos.move(bl2 ? direction2 : direction);
            this.getAndSetState(world, replacer, random, mutablePos, config, bl2 ? Function.identity() : withAxisFunction);
        }
        return new FoliagePlacer.TreeNode(blockPos.up(), 0, false);
    }
}


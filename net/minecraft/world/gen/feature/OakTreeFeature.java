/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CocoaBlock;
import net.minecraft.block.VineBlock;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.ModifiableWorld;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;

public class OakTreeFeature
extends AbstractTreeFeature<DefaultFeatureConfig> {
    private static final BlockState LOG = Blocks.OAK_LOG.getDefaultState();
    private static final BlockState LEAVES = Blocks.OAK_LEAVES.getDefaultState();
    protected final int height;
    private final boolean hasVinesAndCocoa;
    private final BlockState log;
    private final BlockState leaves;

    public OakTreeFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function, boolean bl) {
        this(function, bl, 4, LOG, LEAVES, false);
    }

    public OakTreeFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function, boolean bl, int i, BlockState blockState, BlockState blockState2, boolean bl2) {
        super(function, bl);
        this.height = i;
        this.log = blockState;
        this.leaves = blockState2;
        this.hasVinesAndCocoa = bl2;
    }

    @Override
    public boolean generate(Set<BlockPos> set, ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos, MutableIntBoundingBox mutableIntBoundingBox) {
        BlockPos blockPos2;
        int q;
        int p;
        int n;
        int m;
        int l;
        int k;
        int j;
        int i = this.getTreeHeight(random);
        boolean bl = true;
        if (blockPos.getY() < 1 || blockPos.getY() + i + 1 > 256) {
            return false;
        }
        for (j = blockPos.getY(); j <= blockPos.getY() + 1 + i; ++j) {
            k = 1;
            if (j == blockPos.getY()) {
                k = 0;
            }
            if (j >= blockPos.getY() + 1 + i - 2) {
                k = 2;
            }
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            for (l = blockPos.getX() - k; l <= blockPos.getX() + k && bl; ++l) {
                for (m = blockPos.getZ() - k; m <= blockPos.getZ() + k && bl; ++m) {
                    if (j >= 0 && j < 256) {
                        if (OakTreeFeature.canTreeReplace(modifiableTestableWorld, mutable.set(l, j, m))) continue;
                        bl = false;
                        continue;
                    }
                    bl = false;
                }
            }
        }
        if (!bl) {
            return false;
        }
        if (!OakTreeFeature.isDirtOrGrass(modifiableTestableWorld, blockPos.down()) || blockPos.getY() >= 256 - i - 1) {
            return false;
        }
        this.setToDirt(modifiableTestableWorld, blockPos.down());
        j = 3;
        k = 0;
        for (n = blockPos.getY() - 3 + i; n <= blockPos.getY() + i; ++n) {
            l = n - (blockPos.getY() + i);
            m = 1 - l / 2;
            for (int o = blockPos.getX() - m; o <= blockPos.getX() + m; ++o) {
                p = o - blockPos.getX();
                for (q = blockPos.getZ() - m; q <= blockPos.getZ() + m; ++q) {
                    int r = q - blockPos.getZ();
                    if (Math.abs(p) == m && Math.abs(r) == m && (random.nextInt(2) == 0 || l == 0) || !OakTreeFeature.isAirOrLeaves(modifiableTestableWorld, blockPos2 = new BlockPos(o, n, q)) && !OakTreeFeature.isReplaceablePlant(modifiableTestableWorld, blockPos2)) continue;
                    this.setBlockState(set, modifiableTestableWorld, blockPos2, this.leaves, mutableIntBoundingBox);
                }
            }
        }
        for (n = 0; n < i; ++n) {
            if (!OakTreeFeature.isAirOrLeaves(modifiableTestableWorld, blockPos.up(n)) && !OakTreeFeature.isReplaceablePlant(modifiableTestableWorld, blockPos.up(n))) continue;
            this.setBlockState(set, modifiableTestableWorld, blockPos.up(n), this.log, mutableIntBoundingBox);
            if (!this.hasVinesAndCocoa || n <= 0) continue;
            if (random.nextInt(3) > 0 && OakTreeFeature.isAir(modifiableTestableWorld, blockPos.add(-1, n, 0))) {
                this.makeVine(modifiableTestableWorld, blockPos.add(-1, n, 0), VineBlock.EAST);
            }
            if (random.nextInt(3) > 0 && OakTreeFeature.isAir(modifiableTestableWorld, blockPos.add(1, n, 0))) {
                this.makeVine(modifiableTestableWorld, blockPos.add(1, n, 0), VineBlock.WEST);
            }
            if (random.nextInt(3) > 0 && OakTreeFeature.isAir(modifiableTestableWorld, blockPos.add(0, n, -1))) {
                this.makeVine(modifiableTestableWorld, blockPos.add(0, n, -1), VineBlock.SOUTH);
            }
            if (random.nextInt(3) <= 0 || !OakTreeFeature.isAir(modifiableTestableWorld, blockPos.add(0, n, 1))) continue;
            this.makeVine(modifiableTestableWorld, blockPos.add(0, n, 1), VineBlock.NORTH);
        }
        if (this.hasVinesAndCocoa) {
            for (n = blockPos.getY() - 3 + i; n <= blockPos.getY() + i; ++n) {
                l = n - (blockPos.getY() + i);
                m = 2 - l / 2;
                BlockPos.Mutable mutable2 = new BlockPos.Mutable();
                for (p = blockPos.getX() - m; p <= blockPos.getX() + m; ++p) {
                    for (q = blockPos.getZ() - m; q <= blockPos.getZ() + m; ++q) {
                        mutable2.set(p, n, q);
                        if (!OakTreeFeature.isLeaves(modifiableTestableWorld, mutable2)) continue;
                        BlockPos blockPos3 = mutable2.west();
                        blockPos2 = mutable2.east();
                        BlockPos blockPos4 = mutable2.north();
                        BlockPos blockPos5 = mutable2.south();
                        if (random.nextInt(4) == 0 && OakTreeFeature.isAir(modifiableTestableWorld, blockPos3)) {
                            this.makeVineColumn(modifiableTestableWorld, blockPos3, VineBlock.EAST);
                        }
                        if (random.nextInt(4) == 0 && OakTreeFeature.isAir(modifiableTestableWorld, blockPos2)) {
                            this.makeVineColumn(modifiableTestableWorld, blockPos2, VineBlock.WEST);
                        }
                        if (random.nextInt(4) == 0 && OakTreeFeature.isAir(modifiableTestableWorld, blockPos4)) {
                            this.makeVineColumn(modifiableTestableWorld, blockPos4, VineBlock.SOUTH);
                        }
                        if (random.nextInt(4) != 0 || !OakTreeFeature.isAir(modifiableTestableWorld, blockPos5)) continue;
                        this.makeVineColumn(modifiableTestableWorld, blockPos5, VineBlock.NORTH);
                    }
                }
            }
            if (random.nextInt(5) == 0 && i > 5) {
                for (n = 0; n < 2; ++n) {
                    for (Direction direction : Direction.Type.HORIZONTAL) {
                        if (random.nextInt(4 - n) != 0) continue;
                        Direction direction2 = direction.getOpposite();
                        this.makeCocoa(modifiableTestableWorld, random.nextInt(3), blockPos.add(direction2.getOffsetX(), i - 5 + n, direction2.getOffsetZ()), direction);
                    }
                }
            }
        }
        return true;
    }

    protected int getTreeHeight(Random random) {
        return this.height + random.nextInt(3);
    }

    private void makeCocoa(ModifiableWorld modifiableWorld, int i, BlockPos blockPos, Direction direction) {
        this.setBlockState(modifiableWorld, blockPos, (BlockState)((BlockState)Blocks.COCOA.getDefaultState().with(CocoaBlock.AGE, i)).with(CocoaBlock.FACING, direction));
    }

    private void makeVine(ModifiableWorld modifiableWorld, BlockPos blockPos, BooleanProperty booleanProperty) {
        this.setBlockState(modifiableWorld, blockPos, (BlockState)Blocks.VINE.getDefaultState().with(booleanProperty, true));
    }

    private void makeVineColumn(ModifiableTestableWorld modifiableTestableWorld, BlockPos blockPos, BooleanProperty booleanProperty) {
        this.makeVine(modifiableTestableWorld, blockPos, booleanProperty);
        blockPos = blockPos.down();
        for (int i = 4; OakTreeFeature.isAir(modifiableTestableWorld, blockPos) && i > 0; --i) {
            this.makeVine(modifiableTestableWorld, blockPos, booleanProperty);
            blockPos = blockPos.down();
        }
    }
}


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
import net.minecraft.block.VineBlock;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;

public class SwampTreeFeature
extends AbstractTreeFeature<DefaultFeatureConfig> {
    private static final BlockState LOG = Blocks.OAK_LOG.getDefaultState();
    private static final BlockState LEAVES = Blocks.OAK_LEAVES.getDefaultState();

    public SwampTreeFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
        super(function, false);
    }

    @Override
    public boolean generate(Set<BlockPos> set, ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos) {
        BlockPos blockPos2;
        int o;
        int m;
        int l;
        int k;
        int j;
        int i = random.nextInt(4) + 5;
        blockPos = modifiableTestableWorld.getTopPosition(Heightmap.Type.OCEAN_FLOOR, blockPos);
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
                k = 3;
            }
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            for (l = blockPos.getX() - k; l <= blockPos.getX() + k && bl; ++l) {
                for (m = blockPos.getZ() - k; m <= blockPos.getZ() + k && bl; ++m) {
                    if (j >= 0 && j < 256) {
                        mutable.set(l, j, m);
                        if (SwampTreeFeature.isAirOrLeaves(modifiableTestableWorld, mutable)) continue;
                        if (SwampTreeFeature.isWater(modifiableTestableWorld, mutable)) {
                            if (j <= blockPos.getY()) continue;
                            bl = false;
                            continue;
                        }
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
        if (!SwampTreeFeature.isNaturalDirtOrGrass(modifiableTestableWorld, blockPos.down()) || blockPos.getY() >= 256 - i - 1) {
            return false;
        }
        this.setToDirt(modifiableTestableWorld, blockPos.down());
        for (j = blockPos.getY() - 3 + i; j <= blockPos.getY() + i; ++j) {
            k = j - (blockPos.getY() + i);
            int n = 2 - k / 2;
            for (l = blockPos.getX() - n; l <= blockPos.getX() + n; ++l) {
                m = l - blockPos.getX();
                for (o = blockPos.getZ() - n; o <= blockPos.getZ() + n; ++o) {
                    int p = o - blockPos.getZ();
                    if (Math.abs(m) == n && Math.abs(p) == n && (random.nextInt(2) == 0 || k == 0) || !SwampTreeFeature.isAirOrLeaves(modifiableTestableWorld, blockPos2 = new BlockPos(l, j, o)) && !SwampTreeFeature.isReplaceablePlant(modifiableTestableWorld, blockPos2)) continue;
                    this.setBlockState(modifiableTestableWorld, blockPos2, LEAVES);
                }
            }
        }
        for (j = 0; j < i; ++j) {
            BlockPos blockPos3 = blockPos.up(j);
            if (!SwampTreeFeature.isAirOrLeaves(modifiableTestableWorld, blockPos3) && !SwampTreeFeature.isWater(modifiableTestableWorld, blockPos3)) continue;
            this.setBlockState(set, modifiableTestableWorld, blockPos3, LOG);
        }
        for (j = blockPos.getY() - 3 + i; j <= blockPos.getY() + i; ++j) {
            int k2 = j - (blockPos.getY() + i);
            int n = 2 - k2 / 2;
            BlockPos.Mutable mutable2 = new BlockPos.Mutable();
            for (m = blockPos.getX() - n; m <= blockPos.getX() + n; ++m) {
                for (o = blockPos.getZ() - n; o <= blockPos.getZ() + n; ++o) {
                    mutable2.set(m, j, o);
                    if (!SwampTreeFeature.isLeaves(modifiableTestableWorld, mutable2)) continue;
                    BlockPos blockPos4 = mutable2.west();
                    blockPos2 = mutable2.east();
                    BlockPos blockPos5 = mutable2.north();
                    BlockPos blockPos6 = mutable2.south();
                    if (random.nextInt(4) == 0 && SwampTreeFeature.isAir(modifiableTestableWorld, blockPos4)) {
                        this.makeVines(modifiableTestableWorld, blockPos4, VineBlock.EAST);
                    }
                    if (random.nextInt(4) == 0 && SwampTreeFeature.isAir(modifiableTestableWorld, blockPos2)) {
                        this.makeVines(modifiableTestableWorld, blockPos2, VineBlock.WEST);
                    }
                    if (random.nextInt(4) == 0 && SwampTreeFeature.isAir(modifiableTestableWorld, blockPos5)) {
                        this.makeVines(modifiableTestableWorld, blockPos5, VineBlock.SOUTH);
                    }
                    if (random.nextInt(4) != 0 || !SwampTreeFeature.isAir(modifiableTestableWorld, blockPos6)) continue;
                    this.makeVines(modifiableTestableWorld, blockPos6, VineBlock.NORTH);
                }
            }
        }
        return true;
    }

    private void makeVines(ModifiableTestableWorld modifiableTestableWorld, BlockPos blockPos, BooleanProperty booleanProperty) {
        BlockState blockState = (BlockState)Blocks.VINE.getDefaultState().with(booleanProperty, true);
        this.setBlockState(modifiableTestableWorld, blockPos, blockState);
        blockPos = blockPos.down();
        for (int i = 4; SwampTreeFeature.isAir(modifiableTestableWorld, blockPos) && i > 0; --i) {
            this.setBlockState(modifiableTestableWorld, blockPos, blockState);
            blockPos = blockPos.down();
        }
    }
}


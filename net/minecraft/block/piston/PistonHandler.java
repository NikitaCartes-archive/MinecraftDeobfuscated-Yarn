/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.piston;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class PistonHandler {
    private final World world;
    private final BlockPos posFrom;
    private final boolean field_12247;
    private final BlockPos posTo;
    private final Direction direction;
    private final List<BlockPos> movedBlocks = Lists.newArrayList();
    private final List<BlockPos> brokenBlocks = Lists.newArrayList();
    private final Direction field_12248;

    public PistonHandler(World world, BlockPos blockPos, Direction direction, boolean bl) {
        this.world = world;
        this.posFrom = blockPos;
        this.field_12248 = direction;
        this.field_12247 = bl;
        if (bl) {
            this.direction = direction;
            this.posTo = blockPos.offset(direction);
        } else {
            this.direction = direction.getOpposite();
            this.posTo = blockPos.method_10079(direction, 2);
        }
    }

    public boolean calculatePush() {
        this.movedBlocks.clear();
        this.brokenBlocks.clear();
        BlockState blockState = this.world.getBlockState(this.posTo);
        if (!PistonBlock.isMovable(blockState, this.world, this.posTo, this.direction, false, this.field_12248)) {
            if (this.field_12247 && blockState.getPistonBehavior() == PistonBehavior.DESTROY) {
                this.brokenBlocks.add(this.posTo);
                return true;
            }
            return false;
        }
        if (!this.tryMove(this.posTo, this.direction)) {
            return false;
        }
        for (int i = 0; i < this.movedBlocks.size(); ++i) {
            BlockPos blockPos = this.movedBlocks.get(i);
            if (!this.method_23367(this.world.getBlockState(blockPos).getBlock()) || this.method_11538(blockPos)) continue;
            return false;
        }
        return true;
    }

    private boolean method_23367(Block block) {
        return block == Blocks.SLIME_BLOCK || block == Blocks.HONEY_BLOCK;
    }

    private boolean tryMove(BlockPos blockPos, Direction direction) {
        int k;
        BlockState blockState = this.world.getBlockState(blockPos);
        Block block = blockState.getBlock();
        if (blockState.isAir()) {
            return true;
        }
        if (!PistonBlock.isMovable(blockState, this.world, blockPos, this.direction, false, direction)) {
            return true;
        }
        if (blockPos.equals(this.posFrom)) {
            return true;
        }
        if (this.movedBlocks.contains(blockPos)) {
            return true;
        }
        int i = 1;
        if (i + this.movedBlocks.size() > 12) {
            return false;
        }
        while (this.method_23367(block)) {
            BlockPos blockPos2 = blockPos.method_10079(this.direction.getOpposite(), i);
            blockState = this.world.getBlockState(blockPos2);
            block = blockState.getBlock();
            if (blockState.isAir() || !PistonBlock.isMovable(blockState, this.world, blockPos2, this.direction, false, this.direction.getOpposite()) || blockPos2.equals(this.posFrom)) break;
            if (++i + this.movedBlocks.size() <= 12) continue;
            return false;
        }
        int j = 0;
        for (k = i - 1; k >= 0; --k) {
            this.movedBlocks.add(blockPos.method_10079(this.direction.getOpposite(), k));
            ++j;
        }
        k = 1;
        while (true) {
            BlockPos blockPos3;
            int l;
            if ((l = this.movedBlocks.indexOf(blockPos3 = blockPos.method_10079(this.direction, k))) > -1) {
                this.method_11539(j, l);
                for (int m = 0; m <= l + j; ++m) {
                    BlockPos blockPos4 = this.movedBlocks.get(m);
                    if (!this.method_23367(this.world.getBlockState(blockPos4).getBlock()) || this.method_11538(blockPos4)) continue;
                    return false;
                }
                return true;
            }
            blockState = this.world.getBlockState(blockPos3);
            if (blockState.isAir()) {
                return true;
            }
            if (!PistonBlock.isMovable(blockState, this.world, blockPos3, this.direction, true, this.direction) || blockPos3.equals(this.posFrom)) {
                return false;
            }
            if (blockState.getPistonBehavior() == PistonBehavior.DESTROY) {
                this.brokenBlocks.add(blockPos3);
                return true;
            }
            if (this.movedBlocks.size() >= 12) {
                return false;
            }
            this.movedBlocks.add(blockPos3);
            ++j;
            ++k;
        }
    }

    private void method_11539(int i, int j) {
        ArrayList<BlockPos> list = Lists.newArrayList();
        ArrayList<BlockPos> list2 = Lists.newArrayList();
        ArrayList<BlockPos> list3 = Lists.newArrayList();
        list.addAll(this.movedBlocks.subList(0, j));
        list2.addAll(this.movedBlocks.subList(this.movedBlocks.size() - i, this.movedBlocks.size()));
        list3.addAll(this.movedBlocks.subList(j, this.movedBlocks.size() - i));
        this.movedBlocks.clear();
        this.movedBlocks.addAll(list);
        this.movedBlocks.addAll(list2);
        this.movedBlocks.addAll(list3);
    }

    private boolean method_11538(BlockPos blockPos) {
        for (Direction direction : Direction.values()) {
            if (direction.getAxis() == this.direction.getAxis() || this.tryMove(blockPos.offset(direction), direction)) continue;
            return false;
        }
        return true;
    }

    public List<BlockPos> getMovedBlocks() {
        return this.movedBlocks;
    }

    public List<BlockPos> getBrokenBlocks() {
        return this.brokenBlocks;
    }
}


/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.goal;

import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.BedPart;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.CollisionView;

public class CatSitOnBlockGoal
extends MoveToTargetPosGoal {
    private final CatEntity cat;

    public CatSitOnBlockGoal(CatEntity catEntity, double d) {
        super(catEntity, d, 8);
        this.cat = catEntity;
    }

    @Override
    public boolean canStart() {
        return this.cat.isTamed() && !this.cat.isSitting() && super.canStart();
    }

    @Override
    public void start() {
        super.start();
        this.cat.getSitGoal().setEnabledWithOwner(false);
    }

    @Override
    public void stop() {
        super.stop();
        this.cat.setSitting(false);
    }

    @Override
    public void tick() {
        super.tick();
        this.cat.getSitGoal().setEnabledWithOwner(false);
        if (!this.hasReached()) {
            this.cat.setSitting(false);
        } else if (!this.cat.isSitting()) {
            this.cat.setSitting(true);
        }
    }

    @Override
    protected boolean isTargetPos(CollisionView collisionView, BlockPos blockPos) {
        if (!collisionView.isAir(blockPos.up())) {
            return false;
        }
        BlockState blockState = collisionView.getBlockState(blockPos);
        Block block = blockState.getBlock();
        if (block == Blocks.CHEST) {
            return ChestBlockEntity.getPlayersLookingInChestCount(collisionView, blockPos) < 1;
        }
        if (block == Blocks.FURNACE && blockState.get(FurnaceBlock.LIT).booleanValue()) {
            return true;
        }
        return block.matches(BlockTags.BEDS) && blockState.get(BedBlock.PART) != BedPart.HEAD;
    }
}


/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.goal;

import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.BedPart;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public class CatSitOnBlockGoal
extends MoveToTargetPosGoal {
    private final CatEntity cat;

    public CatSitOnBlockGoal(CatEntity cat, double speed) {
        super(cat, speed, 8);
        this.cat = cat;
    }

    @Override
    public boolean canStart() {
        return this.cat.isTamed() && !this.cat.isSitting() && super.canStart();
    }

    @Override
    public void start() {
        super.start();
        this.cat.setInSittingPose(false);
    }

    @Override
    public void stop() {
        super.stop();
        this.cat.setInSittingPose(false);
    }

    @Override
    public void tick() {
        super.tick();
        this.cat.setInSittingPose(this.hasReached());
    }

    @Override
    protected boolean isTargetPos(WorldView world, BlockPos pos) {
        if (!world.isAir(pos.up())) {
            return false;
        }
        BlockState blockState = world.getBlockState(pos);
        if (blockState.isOf(Blocks.CHEST)) {
            return ChestBlockEntity.getPlayersLookingInChestCount(world, pos) < 1;
        }
        if (blockState.isOf(Blocks.FURNACE) && blockState.get(FurnaceBlock.LIT).booleanValue()) {
            return true;
        }
        return blockState.isIn(BlockTags.BEDS, state -> state.getOrEmpty(BedBlock.PART).map(part -> part != BedPart.HEAD).orElse(true));
    }
}


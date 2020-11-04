/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.class_5575;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

/**
 * Handles the viewer count for chest-like block entities.
 */
public abstract class ChestStateManager {
    private int viewerCount;

    /**
     * Run when this chest is opened (when the viewer count becomes nonzero).
     */
    protected abstract void onChestOpened(World var1, BlockPos var2, BlockState var3);

    /**
     * Run when this chest closes (when the viewer count reaches zero).
     */
    protected abstract void onChestClosed(World var1, BlockPos var2, BlockState var3);

    /**
     * Run when a player interacts with this chest.
     */
    protected abstract void onInteracted(World var1, BlockPos var2, BlockState var3, int var4, int var5);

    /**
     * Determines whether the given player is currently viewing this chest.
     */
    protected abstract boolean isPlayerViewing(PlayerEntity var1);

    public void openChest(World world, BlockPos pos, BlockState state) {
        int i;
        if ((i = this.viewerCount++) == 0) {
            this.onChestOpened(world, pos, state);
            ChestStateManager.scheduleBlockTick(world, pos, state);
        }
        this.onInteracted(world, pos, state, i, this.viewerCount);
    }

    public void closeChest(World world, BlockPos pos, BlockState state) {
        int i = this.viewerCount--;
        if (this.viewerCount == 0) {
            this.onChestClosed(world, pos, state);
        }
        this.onInteracted(world, pos, state, i, this.viewerCount);
    }

    private int getInRangeViewerCount(World world, BlockPos pos) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        float f = 5.0f;
        Box box = new Box((float)i - 5.0f, (float)j - 5.0f, (float)k - 5.0f, (float)(i + 1) + 5.0f, (float)(j + 1) + 5.0f, (float)(k + 1) + 5.0f);
        return world.getEntitiesByType(class_5575.method_31795(PlayerEntity.class), box, this::isPlayerViewing).size();
    }

    public void updateViewerCount(World world, BlockPos pos, BlockState state) {
        int j = this.viewerCount;
        int i = this.getInRangeViewerCount(world, pos);
        if (j != i) {
            boolean bl2;
            boolean bl = i != 0;
            boolean bl3 = bl2 = j != 0;
            if (bl && !bl2) {
                this.onChestOpened(world, pos, state);
            } else if (!bl) {
                this.onChestClosed(world, pos, state);
            }
            this.viewerCount = i;
        }
        this.onInteracted(world, pos, state, j, i);
        if (i > 0) {
            ChestStateManager.scheduleBlockTick(world, pos, state);
        }
    }

    public int getViewerCount() {
        return this.viewerCount;
    }

    private static void scheduleBlockTick(World world, BlockPos pos, BlockState state) {
        world.getBlockTickScheduler().schedule(pos, state.getBlock(), 5);
    }
}


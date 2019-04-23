/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.entity;

import net.minecraft.block.Block;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface Hopper
extends Inventory {
    public static final VoxelShape INSIDE_SHAPE = Block.createCuboidShape(2.0, 11.0, 2.0, 14.0, 16.0, 14.0);
    public static final VoxelShape ABOVE_SHAPE = Block.createCuboidShape(0.0, 16.0, 0.0, 16.0, 32.0, 16.0);
    public static final VoxelShape INPUT_AREA_SHAPE = VoxelShapes.union(INSIDE_SHAPE, ABOVE_SHAPE);

    default public VoxelShape getInputAreaShape() {
        return INPUT_AREA_SHAPE;
    }

    @Nullable
    public World getWorld();

    public double getHopperX();

    public double getHopperY();

    public double getHopperZ();
}


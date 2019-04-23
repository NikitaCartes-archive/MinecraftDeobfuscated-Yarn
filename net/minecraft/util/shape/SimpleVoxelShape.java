/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.shape;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.FractionalDoubleList;
import net.minecraft.util.shape.VoxelSet;
import net.minecraft.util.shape.VoxelShape;

final class SimpleVoxelShape
extends VoxelShape {
    SimpleVoxelShape(VoxelSet voxelSet) {
        super(voxelSet);
    }

    @Override
    protected DoubleList getIncludedPoints(Direction.Axis axis) {
        return new FractionalDoubleList(this.voxels.getSize(axis));
    }

    @Override
    protected int getCoordIndex(Direction.Axis axis, double d) {
        int i = this.voxels.getSize(axis);
        return MathHelper.clamp(MathHelper.floor(d * (double)i), -1, i);
    }
}


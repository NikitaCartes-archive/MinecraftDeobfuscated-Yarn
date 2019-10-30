/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.shape;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.Arrays;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelSet;
import net.minecraft.util.shape.VoxelShape;

public final class ArrayVoxelShape
extends VoxelShape {
    private final DoubleList xPoints;
    private final DoubleList yPoints;
    private final DoubleList zPoints;

    protected ArrayVoxelShape(VoxelSet voxelSet, double[] ds, double[] es, double[] fs) {
        this(voxelSet, DoubleArrayList.wrap(Arrays.copyOf(ds, voxelSet.getXSize() + 1)), DoubleArrayList.wrap(Arrays.copyOf(es, voxelSet.getYSize() + 1)), DoubleArrayList.wrap(Arrays.copyOf(fs, voxelSet.getZSize() + 1)));
    }

    ArrayVoxelShape(VoxelSet voxelSet, DoubleList doubleList, DoubleList doubleList2, DoubleList doubleList3) {
        super(voxelSet);
        int i = voxelSet.getXSize() + 1;
        int j = voxelSet.getYSize() + 1;
        int k = voxelSet.getZSize() + 1;
        if (i != doubleList.size() || j != doubleList2.size() || k != doubleList3.size()) {
            throw Util.throwOrPause(new IllegalArgumentException("Lengths of point arrays must be consistent with the size of the VoxelShape."));
        }
        this.xPoints = doubleList;
        this.yPoints = doubleList2;
        this.zPoints = doubleList3;
    }

    @Override
    protected DoubleList getPointPositions(Direction.Axis axis) {
        switch (axis) {
            case X: {
                return this.xPoints;
            }
            case Y: {
                return this.yPoints;
            }
            case Z: {
                return this.zPoints;
            }
        }
        throw new IllegalArgumentException();
    }
}


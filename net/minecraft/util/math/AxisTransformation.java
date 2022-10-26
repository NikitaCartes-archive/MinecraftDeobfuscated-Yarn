/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.math;

import java.util.Arrays;
import net.minecraft.util.Util;
import org.joml.Matrix3f;

public enum AxisTransformation {
    P123(0, 1, 2),
    P213(1, 0, 2),
    P132(0, 2, 1),
    P231(1, 2, 0),
    P312(2, 0, 1),
    P321(2, 1, 0);

    private final int[] mappings;
    private final Matrix3f matrix;
    private static final int field_33113 = 3;
    private static final AxisTransformation[][] COMBINATIONS;

    private AxisTransformation(int xMapping, int yMapping, int zMapping) {
        this.mappings = new int[]{xMapping, yMapping, zMapping};
        this.matrix = new Matrix3f();
        this.matrix.set(this.map(0), 0, 1.0f);
        this.matrix.set(this.map(1), 1, 1.0f);
        this.matrix.set(this.map(2), 2, 1.0f);
    }

    public AxisTransformation prepend(AxisTransformation transformation) {
        return COMBINATIONS[this.ordinal()][transformation.ordinal()];
    }

    public int map(int oldAxis) {
        return this.mappings[oldAxis];
    }

    public Matrix3f getMatrix() {
        return this.matrix;
    }

    static {
        COMBINATIONS = Util.make(new AxisTransformation[AxisTransformation.values().length][AxisTransformation.values().length], axisTransformations -> {
            for (AxisTransformation axisTransformation2 : AxisTransformation.values()) {
                for (AxisTransformation axisTransformation22 : AxisTransformation.values()) {
                    AxisTransformation axisTransformation3;
                    int[] is = new int[3];
                    for (int i = 0; i < 3; ++i) {
                        is[i] = axisTransformation2.mappings[axisTransformation22.mappings[i]];
                    }
                    axisTransformations[axisTransformation2.ordinal()][axisTransformation22.ordinal()] = axisTransformation3 = Arrays.stream(AxisTransformation.values()).filter(axisTransformation -> Arrays.equals(axisTransformation.mappings, is)).findFirst().get();
                }
            }
        });
    }
}


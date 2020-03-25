/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import java.util.Arrays;
import net.minecraft.client.util.math.Matrix3f;
import net.minecraft.util.Util;

public enum class_4998 {
    field_23362(0, 1, 2),
    field_23363(1, 0, 2),
    field_23364(0, 2, 1),
    field_23365(1, 2, 0),
    field_23366(2, 0, 1),
    field_23367(2, 1, 0);

    private final int[] field_23368;
    private final Matrix3f field_23369;
    private static final class_4998[][] field_23370;

    private class_4998(int j, int k, int l) {
        this.field_23368 = new int[]{j, k, l};
        this.field_23369 = new Matrix3f();
        this.field_23369.method_26288(0, this.method_26417(0), 1.0f);
        this.field_23369.method_26288(1, this.method_26417(1), 1.0f);
        this.field_23369.method_26288(2, this.method_26417(2), 1.0f);
    }

    public class_4998 method_26418(class_4998 arg) {
        return field_23370[this.ordinal()][arg.ordinal()];
    }

    public int method_26417(int i) {
        return this.field_23368[i];
    }

    public Matrix3f method_26416() {
        return this.field_23369;
    }

    static {
        field_23370 = Util.make(new class_4998[class_4998.values().length][class_4998.values().length], args -> {
            for (class_4998 lv : class_4998.values()) {
                for (class_4998 lv2 : class_4998.values()) {
                    class_4998 lv3;
                    int[] is = new int[3];
                    for (int i = 0; i < 3; ++i) {
                        is[i] = lv.field_23368[lv2.field_23368[i]];
                    }
                    args[lv.ordinal()][lv2.ordinal()] = lv3 = Arrays.stream(class_4998.values()).filter(arg -> Arrays.equals(arg.field_23368, is)).findFirst().get();
                }
            }
        });
    }
}


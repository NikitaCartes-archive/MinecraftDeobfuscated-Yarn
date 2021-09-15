/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import java.util.function.IntConsumer;
import net.minecraft.class_6490;
import org.apache.commons.lang3.Validate;

public class class_6502
implements class_6490 {
    public static final long[] field_34401 = new long[0];
    private final int field_34402;

    public class_6502(int i) {
        this.field_34402 = i;
    }

    @Override
    public int setAndGetOldValue(int i, int j) {
        Validate.inclusiveBetween(0L, this.field_34402 - 1, i);
        Validate.inclusiveBetween(0L, 0L, j);
        return 0;
    }

    @Override
    public void set(int i, int j) {
        Validate.inclusiveBetween(0L, this.field_34402 - 1, i);
        Validate.inclusiveBetween(0L, 0L, j);
    }

    @Override
    public int get(int i) {
        Validate.inclusiveBetween(0L, this.field_34402 - 1, i);
        return 0;
    }

    @Override
    public long[] getStorage() {
        return field_34401;
    }

    @Override
    public int getSize() {
        return this.field_34402;
    }

    @Override
    public int getElementBits() {
        return 0;
    }

    @Override
    public void forEach(IntConsumer intConsumer) {
        for (int i = 0; i < this.field_34402; ++i) {
            intConsumer.accept(0);
        }
    }
}


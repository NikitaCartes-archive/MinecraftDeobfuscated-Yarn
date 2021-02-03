/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.class_5798;
import net.minecraft.class_5819;

public class class_5820
implements class_5819 {
    private final AtomicLong field_28766 = new AtomicLong();
    private boolean field_28767 = false;

    public class_5820(long l) {
        this.setSeed(l);
    }

    public void setSeed(long l) {
        if (!this.field_28766.compareAndSet(this.field_28766.get(), (l ^ 0x5DEECE66DL) & 0xFFFFFFFFFFFFL)) {
            throw class_5798.method_33564("SimpleRandomSource");
        }
    }

    private int method_33651(int i) {
        long m;
        long l = this.field_28766.get();
        if (!this.field_28766.compareAndSet(l, m = l * 25214903917L + 11L & 0xFFFFFFFFFFFFL)) {
            throw class_5798.method_33564("SimpleRandomSource");
        }
        return (int)(m >> 48 - i);
    }

    @Override
    public int nextInt() {
        return this.method_33651(32);
    }

    @Override
    public int nextInt(int i) {
        int k;
        int j;
        if (i <= 0) {
            throw new IllegalArgumentException("Bound must be positive");
        }
        if ((i & i - 1) == 0) {
            return (int)((long)i * (long)this.method_33651(31) >> 31);
        }
        while ((j = this.method_33651(31)) - (k = j % i) + (i - 1) < 0) {
        }
        return k;
    }

    @Override
    public double nextDouble() {
        return (double)(((long)this.method_33651(26) << 27) + (long)this.method_33651(27)) * (double)1.110223E-16f;
    }
}


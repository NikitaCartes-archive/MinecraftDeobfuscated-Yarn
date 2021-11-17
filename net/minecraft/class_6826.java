/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import java.io.IOException;
import java.io.InputStream;

public class class_6826
extends InputStream {
    private static final int field_36209 = 8192;
    private final InputStream field_36210;
    private final byte[] field_36211;
    private int field_36212;
    private int field_36213;

    public class_6826(InputStream inputStream) {
        this(inputStream, 8192);
    }

    public class_6826(InputStream inputStream, int i) {
        this.field_36210 = inputStream;
        this.field_36211 = new byte[i];
    }

    @Override
    public int read() throws IOException {
        if (this.field_36213 >= this.field_36212) {
            this.method_39780();
            if (this.field_36213 >= this.field_36212) {
                return -1;
            }
        }
        return Byte.toUnsignedInt(this.field_36211[this.field_36213++]);
    }

    @Override
    public int read(byte[] bs, int i, int j) throws IOException {
        int k = this.method_39779();
        if (k <= 0) {
            if (j >= this.field_36211.length) {
                return this.field_36210.read(bs, i, j);
            }
            this.method_39780();
            k = this.method_39779();
            if (k <= 0) {
                return -1;
            }
        }
        if (j > k) {
            j = k;
        }
        System.arraycopy(this.field_36211, this.field_36213, bs, i, j);
        this.field_36213 += j;
        return j;
    }

    @Override
    public long skip(long l) throws IOException {
        if (l <= 0L) {
            return 0L;
        }
        long m = this.method_39779();
        if (m <= 0L) {
            return this.field_36210.skip(l);
        }
        if (l > m) {
            l = m;
        }
        this.field_36213 = (int)((long)this.field_36213 + l);
        return l;
    }

    @Override
    public int available() throws IOException {
        return this.method_39779() + this.field_36210.available();
    }

    @Override
    public void close() throws IOException {
        this.field_36210.close();
    }

    private int method_39779() {
        return this.field_36212 - this.field_36213;
    }

    private void method_39780() throws IOException {
        this.field_36212 = 0;
        this.field_36213 = 0;
        int i = this.field_36210.read(this.field_36211, 0, this.field_36211.length);
        if (i > 0) {
            this.field_36212 = i;
        }
    }
}


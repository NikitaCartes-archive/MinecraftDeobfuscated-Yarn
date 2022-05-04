/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data;

import com.google.common.hash.HashCode;
import java.io.IOException;
import java.nio.file.Path;

public interface DataWriter {
    public void write(Path var1, byte[] var2, HashCode var3) throws IOException;
}


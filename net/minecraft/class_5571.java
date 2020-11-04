/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import net.minecraft.class_5566;
import net.minecraft.util.math.ChunkPos;

public interface class_5571<T>
extends AutoCloseable {
    public CompletableFuture<class_5566<T>> method_31759(ChunkPos var1);

    public void method_31760(class_5566<T> var1);

    public void method_31758();

    @Override
    default public void close() throws IOException {
    }
}


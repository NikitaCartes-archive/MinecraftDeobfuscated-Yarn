/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.class_5568;
import net.minecraft.class_5575;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.Nullable;

public interface class_5577<T extends class_5568> {
    @Nullable
    public T method_31804(int var1);

    @Nullable
    public T method_31808(UUID var1);

    public Iterable<T> method_31803();

    public <U extends T> void method_31806(class_5575<T, U> var1, Consumer<U> var2);

    public void method_31807(Box var1, Consumer<T> var2);

    public <U extends T> void method_31805(class_5575<T, U> var1, Box var2, Consumer<U> var3);
}


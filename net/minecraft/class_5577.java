/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.class_5575;
import net.minecraft.entity.EntityLike;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.Nullable;

public interface class_5577<T extends EntityLike> {
    @Nullable
    public T getById(int var1);

    @Nullable
    public T getByUuid(UUID var1);

    public Iterable<T> iterate();

    public <U extends T> void method_31806(class_5575<T, U> var1, Consumer<U> var2);

    public void method_31807(Box var1, Consumer<T> var2);

    public <U extends T> void method_31805(class_5575<T, U> var1, Box var2, Consumer<U> var3);
}


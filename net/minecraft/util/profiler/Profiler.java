/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.profiler;

import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public interface Profiler {
    public void startTick();

    public void endTick();

    public void push(String var1);

    public void push(Supplier<String> var1);

    public void pop();

    public void swap(String var1);

    @Environment(value=EnvType.CLIENT)
    public void swap(Supplier<String> var1);

    public void method_24270(String var1);

    public void method_24271(Supplier<String> var1);
}


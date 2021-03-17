/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.profiler;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.profiler.ProfileResult;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.ProfilerSystem;
import org.jetbrains.annotations.Nullable;

public interface ReadableProfiler
extends Profiler {
    public ProfileResult getResult();

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public ProfilerSystem.LocatedInfo method_34696(String var1);
}


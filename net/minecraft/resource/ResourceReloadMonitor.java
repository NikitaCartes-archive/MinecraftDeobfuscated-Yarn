/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.resource;

import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Unit;

public interface ResourceReloadMonitor {
    public CompletableFuture<Unit> whenComplete();

    @Environment(value=EnvType.CLIENT)
    public float getProgress();

    @Environment(value=EnvType.CLIENT)
    public boolean isLoadStageComplete();

    @Environment(value=EnvType.CLIENT)
    public boolean isApplyStageComplete();

    @Environment(value=EnvType.CLIENT)
    public void throwExceptions();
}


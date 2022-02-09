/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.resource;

import net.minecraft.resource.ResourceManager;

/**
 * A lifecycled resource manager is available until it is {@linkplain #close()
 * closed}. In principle, it should not be accessed any more after closing;
 * use another resource manager instead.
 */
public interface LifecycledResourceManager
extends ResourceManager,
AutoCloseable {
    @Override
    public void close();
}


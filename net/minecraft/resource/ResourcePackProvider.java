/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.resource;

import java.util.function.Consumer;
import net.minecraft.resource.ResourcePackProfile;

public interface ResourcePackProvider {
    public void register(Consumer<ResourcePackProfile> var1, ResourcePackProfile.Factory var2);
}


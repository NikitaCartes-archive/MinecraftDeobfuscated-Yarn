/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.resource;

import java.util.Map;
import net.minecraft.resource.ResourcePackContainer;

public interface ResourcePackCreator {
    public <T extends ResourcePackContainer> void registerContainer(Map<String, T> var1, ResourcePackContainer.Factory<T> var2);
}


/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.resource;

import java.util.function.Consumer;
import net.minecraft.resource.ResourcePackProfile;

public interface ResourcePackProvider {
    public <T extends ResourcePackProfile> void register(Consumer<T> var1, ResourcePackProfile.class_5351<T> var2);
}


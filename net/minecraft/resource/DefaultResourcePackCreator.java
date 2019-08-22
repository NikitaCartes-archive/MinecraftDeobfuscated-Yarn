/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.resource;

import java.util.Map;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.ResourcePackContainer;
import net.minecraft.resource.ResourcePackCreator;

public class DefaultResourcePackCreator
implements ResourcePackCreator {
    private final DefaultResourcePack pack = new DefaultResourcePack("minecraft");

    @Override
    public <T extends ResourcePackContainer> void registerContainer(Map<String, T> map, ResourcePackContainer.Factory<T> factory) {
        T resourcePackContainer = ResourcePackContainer.of("vanilla", false, () -> this.pack, factory, ResourcePackContainer.InsertionPosition.BOTTOM);
        if (resourcePackContainer != null) {
            map.put("vanilla", resourcePackContainer);
        }
    }
}


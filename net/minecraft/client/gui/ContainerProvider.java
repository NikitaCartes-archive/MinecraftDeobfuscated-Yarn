/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.container.Container;

@Environment(value=EnvType.CLIENT)
public interface ContainerProvider<T extends Container> {
    public T getContainer();
}


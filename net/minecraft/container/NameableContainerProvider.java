/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.container;

import net.minecraft.container.ContainerProvider;
import net.minecraft.network.chat.Component;

public interface NameableContainerProvider
extends ContainerProvider {
    public Component getDisplayName();
}


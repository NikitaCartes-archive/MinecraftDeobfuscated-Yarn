/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.container;

import net.minecraft.container.ContainerFactory;
import net.minecraft.text.Text;

public interface NameableContainerFactory
extends ContainerFactory {
    public Text getDisplayName();
}


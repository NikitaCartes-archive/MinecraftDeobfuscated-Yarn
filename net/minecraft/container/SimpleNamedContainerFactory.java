/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.container;

import net.minecraft.container.Container;
import net.minecraft.container.ContainerFactory;
import net.minecraft.container.NameableContainerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public final class SimpleNamedContainerFactory
implements NameableContainerFactory {
    private final Text name;
    private final ContainerFactory baseFactory;

    public SimpleNamedContainerFactory(ContainerFactory containerFactory, Text text) {
        this.baseFactory = containerFactory;
        this.name = text;
    }

    @Override
    public Text getDisplayName() {
        return this.name;
    }

    @Override
    public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return this.baseFactory.createMenu(i, playerInventory, playerEntity);
    }
}


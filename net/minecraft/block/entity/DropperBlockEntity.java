/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.entity;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public class DropperBlockEntity
extends DispenserBlockEntity {
    public DropperBlockEntity() {
        super(BlockEntityType.DROPPER);
    }

    @Override
    protected Component getContainerName() {
        return new TranslatableComponent("container.dropper", new Object[0]);
    }
}


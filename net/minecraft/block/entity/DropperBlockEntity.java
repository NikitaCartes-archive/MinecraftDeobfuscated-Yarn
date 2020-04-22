/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.entity;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class DropperBlockEntity
extends DispenserBlockEntity {
    public DropperBlockEntity() {
        super(BlockEntityType.DROPPER);
    }

    @Override
    protected Text getContainerName() {
        return new TranslatableText("container.dropper");
    }
}


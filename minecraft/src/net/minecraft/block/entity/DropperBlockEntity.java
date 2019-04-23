package net.minecraft.block.entity;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public class DropperBlockEntity extends DispenserBlockEntity {
	public DropperBlockEntity() {
		super(BlockEntityType.field_11899);
	}

	@Override
	protected Component getContainerName() {
		return new TranslatableComponent("container.dropper");
	}
}

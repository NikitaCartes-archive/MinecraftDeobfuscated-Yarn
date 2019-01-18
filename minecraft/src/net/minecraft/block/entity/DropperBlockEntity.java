package net.minecraft.block.entity;

import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;

public class DropperBlockEntity extends DispenserBlockEntity {
	public DropperBlockEntity() {
		super(BlockEntityType.DROPPER);
	}

	@Override
	protected TextComponent getContainerName() {
		return new TranslatableTextComponent("container.dropper");
	}
}

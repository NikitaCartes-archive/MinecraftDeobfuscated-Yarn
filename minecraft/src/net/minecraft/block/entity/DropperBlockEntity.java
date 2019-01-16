package net.minecraft.block.entity;

import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;

public class DropperBlockEntity extends DispenserBlockEntity {
	public DropperBlockEntity() {
		super(BlockEntityType.DROPPER);
	}

	@Override
	protected TextComponent method_17823() {
		return new TranslatableTextComponent("container.dropper");
	}
}

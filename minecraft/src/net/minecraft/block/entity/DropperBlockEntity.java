package net.minecraft.block.entity;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class DropperBlockEntity extends DispenserBlockEntity {
	public DropperBlockEntity() {
		super(BlockEntityType.field_11899);
	}

	@Override
	protected Text getContainerName() {
		return new TranslatableText("container.dropper");
	}
}

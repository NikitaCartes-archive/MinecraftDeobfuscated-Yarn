package net.minecraft.block.entity;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class DropperBlockEntity extends DispenserBlockEntity {
	public DropperBlockEntity() {
		super(BlockEntityType.field_11899);
	}

	@Override
	protected Text method_17823() {
		return new TranslatableText("container.dropper");
	}
}

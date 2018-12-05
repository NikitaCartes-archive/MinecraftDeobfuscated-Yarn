package net.minecraft.block.entity;

import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;

public class DropperBlockEntity extends DispenserBlockEntity {
	public DropperBlockEntity() {
		super(BlockEntityType.DROPPER);
	}

	@Override
	public TextComponent getName() {
		TextComponent textComponent = this.getCustomName();
		return (TextComponent)(textComponent != null ? textComponent : new TranslatableTextComponent("container.dropper"));
	}

	@Override
	public String getContainerId() {
		return "minecraft:dropper";
	}
}

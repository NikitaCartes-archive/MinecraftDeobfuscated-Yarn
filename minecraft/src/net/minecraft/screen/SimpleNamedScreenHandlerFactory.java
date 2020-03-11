package net.minecraft.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public final class SimpleNamedScreenHandlerFactory implements NamedScreenHandlerFactory {
	private final Text name;
	private final ScreenHandlerFactory baseFactory;

	public SimpleNamedScreenHandlerFactory(ScreenHandlerFactory baseFactory, Text name) {
		this.baseFactory = baseFactory;
		this.name = name;
	}

	@Override
	public Text getDisplayName() {
		return this.name;
	}

	@Override
	public ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
		return this.baseFactory.createMenu(i, playerInventory, playerEntity);
	}
}

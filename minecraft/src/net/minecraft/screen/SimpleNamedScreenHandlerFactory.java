package net.minecraft.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

/**
 * An implementation of {@link NamedScreenHandlerFactory} that can be used
 * without the use of anonymous class. This delegates the creation to {@link
 * #baseFactory}.
 * 
 * <p>An instance is passed to {@link net.minecraft.entity.player.PlayerEntity#openHandledScreen}
 * to open a screen handler.
 */
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

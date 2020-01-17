package net.minecraft.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public final class SimpleNamedContainerFactory implements NameableContainerFactory {
	private final Text name;
	private final ContainerFactory baseFactory;

	public SimpleNamedContainerFactory(ContainerFactory baseFactory, Text name) {
		this.baseFactory = baseFactory;
		this.name = name;
	}

	@Override
	public Text getDisplayName() {
		return this.name;
	}

	@Override
	public Container createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
		return this.baseFactory.createMenu(syncId, playerInventory, playerEntity);
	}
}

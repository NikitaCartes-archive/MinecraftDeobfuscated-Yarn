package net.minecraft.client.network;

import net.minecraft.container.Container;
import net.minecraft.container.ContainerProvider;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.TextComponent;

public final class ClientDummyContainerProvider implements NameableContainerProvider {
	private final TextComponent name;
	private final ContainerProvider containerProvider;

	public ClientDummyContainerProvider(ContainerProvider containerProvider, TextComponent textComponent) {
		this.containerProvider = containerProvider;
		this.name = textComponent;
	}

	@Override
	public TextComponent getDisplayName() {
		return this.name;
	}

	@Override
	public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
		return this.containerProvider.createMenu(i, playerInventory, playerEntity);
	}
}

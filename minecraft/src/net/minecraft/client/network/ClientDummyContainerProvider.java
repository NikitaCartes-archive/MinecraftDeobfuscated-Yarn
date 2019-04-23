package net.minecraft.client.network;

import net.minecraft.container.Container;
import net.minecraft.container.ContainerProvider;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.chat.Component;

public final class ClientDummyContainerProvider implements NameableContainerProvider {
	private final Component name;
	private final ContainerProvider containerProvider;

	public ClientDummyContainerProvider(ContainerProvider containerProvider, Component component) {
		this.containerProvider = containerProvider;
		this.name = component;
	}

	@Override
	public Component getDisplayName() {
		return this.name;
	}

	@Override
	public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
		return this.containerProvider.createMenu(i, playerInventory, playerEntity);
	}
}

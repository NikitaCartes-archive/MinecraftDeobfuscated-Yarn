package net.minecraft.client.network;

import net.minecraft.container.Container;
import net.minecraft.container.ContainerProvider;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public final class ClientDummyContainerProvider implements NameableContainerProvider {
	private final Text field_3947;
	private final ContainerProvider containerProvider;

	public ClientDummyContainerProvider(ContainerProvider containerProvider, Text text) {
		this.containerProvider = containerProvider;
		this.field_3947 = text;
	}

	@Override
	public Text method_5476() {
		return this.field_3947;
	}

	@Override
	public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
		return this.containerProvider.createMenu(i, playerInventory, playerEntity);
	}
}

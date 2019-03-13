package net.minecraft.client.network;

import net.minecraft.container.Container;
import net.minecraft.container.ContainerProvider;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.TextComponent;

public final class ClientDummyContainerProvider implements NameableContainerProvider {
	private final TextComponent field_3947;
	private final ContainerProvider field_17280;

	public ClientDummyContainerProvider(ContainerProvider containerProvider, TextComponent textComponent) {
		this.field_17280 = containerProvider;
		this.field_3947 = textComponent;
	}

	@Override
	public TextComponent method_5476() {
		return this.field_3947;
	}

	@Override
	public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
		return this.field_17280.createMenu(i, playerInventory, playerEntity);
	}
}

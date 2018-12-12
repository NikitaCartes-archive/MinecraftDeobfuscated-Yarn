package net.minecraft.client.network;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.container.Container;
import net.minecraft.container.ContainerProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.TextComponent;

@Environment(EnvType.CLIENT)
public class ClientDummyContainerProvider implements ContainerProvider {
	private final String id;
	private final TextComponent name;

	public ClientDummyContainerProvider(String string, TextComponent textComponent) {
		this.id = string;
		this.name = textComponent;
	}

	@Override
	public Container createContainer(PlayerInventory playerInventory, PlayerEntity playerEntity) {
		throw new UnsupportedOperationException();
	}

	@Override
	public TextComponent getName() {
		return this.name;
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public String getContainerId() {
		return this.id;
	}

	@Nullable
	@Override
	public TextComponent getCustomName() {
		return this.name;
	}
}

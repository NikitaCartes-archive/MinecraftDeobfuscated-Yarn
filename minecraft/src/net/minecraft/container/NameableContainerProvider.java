package net.minecraft.container;

import net.minecraft.network.chat.Component;

public interface NameableContainerProvider extends ContainerProvider {
	Component getDisplayName();
}

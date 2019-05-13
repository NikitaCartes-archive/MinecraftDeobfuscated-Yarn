package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.container.Container;

@Environment(EnvType.CLIENT)
public interface ContainerProvider<T extends Container> {
	T getContainer();
}

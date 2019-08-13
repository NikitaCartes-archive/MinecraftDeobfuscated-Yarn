package net.minecraft.container;

import net.minecraft.text.Text;

public interface NameableContainerProvider extends ContainerProvider {
	Text getDisplayName();
}

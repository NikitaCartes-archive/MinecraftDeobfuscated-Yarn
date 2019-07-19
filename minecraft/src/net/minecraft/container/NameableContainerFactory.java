package net.minecraft.container;

import net.minecraft.text.Text;

public interface NameableContainerFactory extends ContainerFactory {
	Text getDisplayName();
}

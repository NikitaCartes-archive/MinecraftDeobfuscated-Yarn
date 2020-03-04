package net.minecraft.screen;

import net.minecraft.text.Text;

public interface NameableScreenHandlerFactory extends ScreenHandlerFactory {
	Text getDisplayName();
}

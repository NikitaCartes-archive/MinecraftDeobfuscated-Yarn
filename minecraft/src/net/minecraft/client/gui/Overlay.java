package net.minecraft.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class Overlay extends DrawableHelper implements Drawable {
	public boolean pausesGame() {
		return true;
	}
}

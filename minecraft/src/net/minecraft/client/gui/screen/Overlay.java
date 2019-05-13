package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;

@Environment(EnvType.CLIENT)
public abstract class Overlay extends DrawableHelper implements Drawable {
	public boolean pausesGame() {
		return true;
	}
}

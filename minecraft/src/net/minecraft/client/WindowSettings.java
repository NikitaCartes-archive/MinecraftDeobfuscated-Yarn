package net.minecraft.client;

import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class WindowSettings {
	public final int width;
	public final int height;
	public final Optional<Integer> fullscreenWidth;
	public final Optional<Integer> fullscreenHeight;
	public final boolean fullscreen;

	public WindowSettings(int i, int j, Optional<Integer> optional, Optional<Integer> optional2, boolean bl) {
		this.width = i;
		this.height = j;
		this.fullscreenWidth = optional;
		this.fullscreenHeight = optional2;
		this.fullscreen = bl;
	}
}

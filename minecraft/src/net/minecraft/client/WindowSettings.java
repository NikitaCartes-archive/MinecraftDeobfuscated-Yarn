package net.minecraft.client;

import java.util.OptionalInt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class WindowSettings {
	public final int width;
	public final int height;
	public final OptionalInt fullscreenWidth;
	public final OptionalInt fullscreenHeight;
	public final boolean fullscreen;

	public WindowSettings(int i, int j, OptionalInt optionalInt, OptionalInt optionalInt2, boolean bl) {
		this.width = i;
		this.height = j;
		this.fullscreenWidth = optionalInt;
		this.fullscreenHeight = optionalInt2;
		this.fullscreen = bl;
	}
}

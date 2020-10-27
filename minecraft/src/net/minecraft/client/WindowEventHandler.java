package net.minecraft.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface WindowEventHandler {
	void onWindowFocusChanged(boolean focused);

	void onResolutionChanged();

	void onCursorEnterChanged();
}

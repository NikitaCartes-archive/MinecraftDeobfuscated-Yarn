package net.minecraft.client.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface YesNoCallback {
	void confirmResult(boolean bl, int i);
}

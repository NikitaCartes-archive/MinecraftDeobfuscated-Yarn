package com.mojang.blaze3d.platform;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class GlDebugInfo {
	public static String getVendor() {
		return GlStateManager.getString(7936);
	}

	public static String getCpuInfo() {
		return GLX._getCpuInfo();
	}

	public static String getRenderer() {
		return GlStateManager.getString(7937);
	}

	public static String getVersion() {
		return GlStateManager.getString(7938);
	}
}

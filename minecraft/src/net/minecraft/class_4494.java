package net.minecraft;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_4494 {
	public static String method_22088() {
		return RenderSystem.getString(7936);
	}

	public static String method_22089() {
		return GLX._getCpuInfo();
	}

	public static String method_22090() {
		return RenderSystem.getString(7937);
	}

	public static String method_22091() {
		return RenderSystem.getString(7938);
	}
}

package net.minecraft.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class GuiLighting {
	public static void disable() {
		RenderSystem.disableDiffuseLighting();
	}

	public static void enable() {
		RenderSystem.enableUsualDiffuseLighting();
	}

	public static void enableForItems() {
		RenderSystem.enableGuiDiffuseLighting();
	}
}

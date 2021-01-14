package net.minecraft.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Util;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public class DiffuseLighting {
	private static final Vec3f field_24426 = Util.make(new Vec3f(0.2F, 1.0F, -0.7F), Vec3f::normalize);
	private static final Vec3f field_24427 = Util.make(new Vec3f(-0.2F, 1.0F, 0.7F), Vec3f::normalize);
	private static final Vec3f field_24428 = Util.make(new Vec3f(0.2F, 1.0F, -0.7F), Vec3f::normalize);
	private static final Vec3f field_24429 = Util.make(new Vec3f(-0.2F, -1.0F, 0.7F), Vec3f::normalize);

	public static void enable() {
		RenderSystem.enableLighting();
		RenderSystem.enableColorMaterial();
		RenderSystem.colorMaterial(1032, 5634);
	}

	public static void disable() {
		RenderSystem.disableLighting();
		RenderSystem.disableColorMaterial();
	}

	public static void enableForLevel(Matrix4f modelMatrix) {
		RenderSystem.setupLevelDiffuseLighting(field_24428, field_24429, modelMatrix);
	}

	public static void disableForLevel(Matrix4f modelMatrix) {
		RenderSystem.setupLevelDiffuseLighting(field_24426, field_24427, modelMatrix);
	}

	public static void disableGuiDepthLighting() {
		RenderSystem.setupGuiFlatDiffuseLighting(field_24426, field_24427);
	}

	public static void enableGuiDepthLighting() {
		RenderSystem.setupGui3DDiffuseLighting(field_24426, field_24427);
	}
}

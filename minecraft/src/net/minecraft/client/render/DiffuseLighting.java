package net.minecraft.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Vector3f;

@Environment(EnvType.CLIENT)
public class DiffuseLighting {
	private static Matrix4f field_21797 = new Matrix4f();
	private static Matrix4f field_21798 = new Matrix4f();

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
		RenderSystem.setupLevelDiffuseLighting(modelMatrix);
	}

	public static void enableForGui(Matrix4f modelMatrix) {
		field_21797 = modelMatrix.copy();
		field_21798 = modelMatrix.copy();
		field_21798.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(62.0F));
		field_21798.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(185.5F));
		RenderSystem.setupGuiDiffuseLighting(modelMatrix);
	}

	public static void method_24210() {
		RenderSystem.setupGuiDiffuseLighting(field_21797);
	}

	public static void method_24211() {
		RenderSystem.setupGuiDiffuseLighting(field_21798);
	}
}

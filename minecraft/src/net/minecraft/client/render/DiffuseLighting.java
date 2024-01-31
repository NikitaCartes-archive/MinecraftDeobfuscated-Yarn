package net.minecraft.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class DiffuseLighting {
	private static final Vector3f field_24426 = new Vector3f(0.2F, 1.0F, -0.7F).normalize();
	private static final Vector3f field_24427 = new Vector3f(-0.2F, 1.0F, 0.7F).normalize();
	private static final Vector3f field_24428 = new Vector3f(0.2F, 1.0F, -0.7F).normalize();
	private static final Vector3f field_24429 = new Vector3f(-0.2F, -1.0F, 0.7F).normalize();
	private static final Vector3f field_29567 = new Vector3f(0.2F, -1.0F, 1.0F).normalize();
	private static final Vector3f field_29568 = new Vector3f(-0.2F, -1.0F, 0.0F).normalize();

	public static void enableForLevel() {
		RenderSystem.setupLevelDiffuseLighting(field_24428, field_24429);
	}

	public static void disableForLevel() {
		RenderSystem.setupLevelDiffuseLighting(field_24426, field_24427);
	}

	public static void disableGuiDepthLighting() {
		RenderSystem.setupGuiFlatDiffuseLighting(field_24426, field_24427);
	}

	public static void enableGuiDepthLighting() {
		RenderSystem.setupGui3DDiffuseLighting(field_24426, field_24427);
	}

	public static void method_34742() {
		RenderSystem.setShaderLights(field_29567, field_29568);
	}

	public static void method_56819(Quaternionf quaternionf) {
		RenderSystem.setShaderLights(quaternionf.transform(field_29567, new Vector3f()), quaternionf.transform(field_29568, new Vector3f()));
	}
}

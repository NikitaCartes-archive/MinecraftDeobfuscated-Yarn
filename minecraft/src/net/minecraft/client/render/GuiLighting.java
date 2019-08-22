package net.minecraft.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.FloatBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.client.util.math.Vector3f;

@Environment(EnvType.CLIENT)
public class GuiLighting {
	private static final FloatBuffer buffer = GlAllocationUtils.allocateFloatBuffer(4);
	private static final Vector3f towardLight = createNormalVector(0.2F, 1.0F, -0.7F);
	private static final Vector3f oppositeLight = createNormalVector(-0.2F, 1.0F, 0.7F);

	private static Vector3f createNormalVector(float f, float g, float h) {
		Vector3f vector3f = new Vector3f(f, g, h);
		vector3f.reciprocal();
		return vector3f;
	}

	public static void disable() {
		RenderSystem.disableLighting();
		RenderSystem.disableLight(0);
		RenderSystem.disableLight(1);
		RenderSystem.disableColorMaterial();
	}

	public static void enable() {
		RenderSystem.enableLighting();
		RenderSystem.enableLight(0);
		RenderSystem.enableLight(1);
		RenderSystem.enableColorMaterial();
		RenderSystem.colorMaterial(1032, 5634);
		RenderSystem.light(16384, 4611, singletonBuffer(towardLight.getX(), towardLight.getY(), towardLight.getZ(), 0.0F));
		float f = 0.6F;
		RenderSystem.light(16384, 4609, singletonBuffer(0.6F, 0.6F, 0.6F, 1.0F));
		RenderSystem.light(16384, 4608, singletonBuffer(0.0F, 0.0F, 0.0F, 1.0F));
		RenderSystem.light(16384, 4610, singletonBuffer(0.0F, 0.0F, 0.0F, 1.0F));
		RenderSystem.light(16385, 4611, singletonBuffer(oppositeLight.getX(), oppositeLight.getY(), oppositeLight.getZ(), 0.0F));
		RenderSystem.light(16385, 4609, singletonBuffer(0.6F, 0.6F, 0.6F, 1.0F));
		RenderSystem.light(16385, 4608, singletonBuffer(0.0F, 0.0F, 0.0F, 1.0F));
		RenderSystem.light(16385, 4610, singletonBuffer(0.0F, 0.0F, 0.0F, 1.0F));
		RenderSystem.shadeModel(7424);
		float g = 0.4F;
		RenderSystem.lightModel(2899, singletonBuffer(0.4F, 0.4F, 0.4F, 1.0F));
	}

	public static FloatBuffer singletonBuffer(float f, float g, float h, float i) {
		buffer.clear();
		buffer.put(f).put(g).put(h).put(i);
		buffer.flip();
		return buffer;
	}

	public static void enableForItems() {
		RenderSystem.pushMatrix();
		RenderSystem.rotatef(-30.0F, 0.0F, 1.0F, 0.0F);
		RenderSystem.rotatef(165.0F, 1.0F, 0.0F, 0.0F);
		enable();
		RenderSystem.popMatrix();
	}
}

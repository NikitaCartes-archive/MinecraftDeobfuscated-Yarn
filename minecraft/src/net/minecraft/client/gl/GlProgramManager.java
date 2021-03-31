package net.minecraft.client.gl;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class GlProgramManager {
	private static final Logger LOGGER = LogManager.getLogger();

	public static void useProgram(int i) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GlStateManager._glUseProgram(i);
	}

	public static void deleteProgram(GlShader glShader) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		glShader.getFragmentShader().release();
		glShader.getVertexShader().release();
		GlStateManager.glDeleteProgram(glShader.getProgramRef());
	}

	public static int createProgram() throws IOException {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		int i = GlStateManager.glCreateProgram();
		if (i <= 0) {
			throw new IOException("Could not create shader program (returned program ID " + i + ")");
		} else {
			return i;
		}
	}

	public static void linkProgram(GlShader glShader) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		glShader.attachReferencedShaders();
		GlStateManager.glLinkProgram(glShader.getProgramRef());
		int i = GlStateManager.glGetProgrami(glShader.getProgramRef(), 35714);
		if (i == 0) {
			LOGGER.warn(
				"Error encountered when linking program containing VS {} and FS {}. Log output:",
				glShader.getVertexShader().getName(),
				glShader.getFragmentShader().getName()
			);
			LOGGER.warn(GlStateManager.glGetProgramInfoLog(glShader.getProgramRef(), 32768));
		}
	}
}

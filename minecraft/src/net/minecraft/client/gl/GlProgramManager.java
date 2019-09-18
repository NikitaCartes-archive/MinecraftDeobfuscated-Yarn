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
		GlStateManager.useProgram(i);
	}

	public static void deleteProgram(GlProgram glProgram) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		glProgram.getFragmentShader().release();
		glProgram.getVertexShader().release();
		GlStateManager.deleteProgram(glProgram.getProgramRef());
	}

	public static int createProgram() throws IOException {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		int i = GlStateManager.createProgram();
		if (i <= 0) {
			throw new IOException("Could not create shader program (returned program ID " + i + ")");
		} else {
			return i;
		}
	}

	public static void linkProgram(GlProgram glProgram) throws IOException {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		glProgram.getFragmentShader().attachTo(glProgram);
		glProgram.getVertexShader().attachTo(glProgram);
		GlStateManager.linkProgram(glProgram.getProgramRef());
		int i = GlStateManager.getProgram(glProgram.getProgramRef(), 35714);
		if (i == 0) {
			LOGGER.warn(
				"Error encountered when linking program containing VS {} and FS {}. Log output:",
				glProgram.getVertexShader().getName(),
				glProgram.getFragmentShader().getName()
			);
			LOGGER.warn(GlStateManager.getProgramInfoLog(glProgram.getProgramRef(), 32768));
		}
	}
}

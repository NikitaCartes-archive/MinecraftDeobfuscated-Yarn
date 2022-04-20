package net.minecraft.client.gl;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class GlProgramManager {
	private static final Logger LOGGER = LogUtils.getLogger();

	public static void useProgram(int program) {
		RenderSystem.assertOnRenderThread();
		GlStateManager._glUseProgram(program);
	}

	public static void deleteProgram(GlShader shader) {
		RenderSystem.assertOnRenderThread();
		shader.getFragmentShader().release();
		shader.getVertexShader().release();
		GlStateManager.glDeleteProgram(shader.getProgramRef());
	}

	public static int createProgram() throws IOException {
		RenderSystem.assertOnRenderThread();
		int i = GlStateManager.glCreateProgram();
		if (i <= 0) {
			throw new IOException("Could not create shader program (returned program ID " + i + ")");
		} else {
			return i;
		}
	}

	public static void linkProgram(GlShader shader) {
		RenderSystem.assertOnRenderThread();
		shader.attachReferencedShaders();
		GlStateManager.glLinkProgram(shader.getProgramRef());
		int i = GlStateManager.glGetProgrami(shader.getProgramRef(), GlConst.GL_LINK_STATUS);
		if (i == 0) {
			LOGGER.warn(
				"Error encountered when linking program containing VS {} and FS {}. Log output:", shader.getVertexShader().getName(), shader.getFragmentShader().getName()
			);
			LOGGER.warn(GlStateManager.glGetProgramInfoLog(shader.getProgramRef(), 32768));
		}
	}
}

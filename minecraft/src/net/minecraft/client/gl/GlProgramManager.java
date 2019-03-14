package net.minecraft.client.gl;

import com.mojang.blaze3d.platform.GLX;
import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class GlProgramManager {
	private static final Logger LOGGER = LogManager.getLogger();
	private static GlProgramManager INSTANCE;

	public static void init() {
		INSTANCE = new GlProgramManager();
	}

	public static GlProgramManager getInstance() {
		return INSTANCE;
	}

	private GlProgramManager() {
	}

	public void deleteProgram(GlProgram glProgram) {
		glProgram.getFragmentShader().method_1282();
		glProgram.getVertexShader().method_1282();
		GLX.glDeleteProgram(glProgram.getProgramRef());
	}

	public int createProgram() throws IOException {
		int i = GLX.glCreateProgram();
		if (i <= 0) {
			throw new IOException("Could not create shader program (returned program ID " + i + ")");
		} else {
			return i;
		}
	}

	public void linkProgram(GlProgram glProgram) throws IOException {
		glProgram.getFragmentShader().attachTo(glProgram);
		glProgram.getVertexShader().attachTo(glProgram);
		GLX.glLinkProgram(glProgram.getProgramRef());
		int i = GLX.glGetProgrami(glProgram.getProgramRef(), GLX.GL_LINK_STATUS);
		if (i == 0) {
			LOGGER.warn(
				"Error encountered when linking program containing VS {} and FS {}. Log output:",
				glProgram.getVertexShader().getName(),
				glProgram.getFragmentShader().getName()
			);
			LOGGER.warn(GLX.glGetProgramInfoLog(glProgram.getProgramRef(), 32768));
		}
	}
}

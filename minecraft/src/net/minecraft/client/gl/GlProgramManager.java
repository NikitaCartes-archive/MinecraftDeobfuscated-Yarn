package net.minecraft.client.gl;

import com.mojang.blaze3d.platform.GLX;
import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3679;
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

	public void deleteProgram(class_3679 arg) {
		arg.getFragmentShader().method_1282();
		arg.getVertexShader().method_1282();
		GLX.glDeleteProgram(arg.getProgramRef());
	}

	public int createProgram() throws IOException {
		int i = GLX.glCreateProgram();
		if (i <= 0) {
			throw new IOException("Could not create shader program (returned program ID " + i + ")");
		} else {
			return i;
		}
	}

	public void linkProgram(class_3679 arg) throws IOException {
		arg.getFragmentShader().attachTo(arg);
		arg.getVertexShader().attachTo(arg);
		GLX.glLinkProgram(arg.getProgramRef());
		int i = GLX.glGetProgrami(arg.getProgramRef(), GLX.GL_LINK_STATUS);
		if (i == 0) {
			LOGGER.warn(
				"Error encountered when linking program containing VS {} and FS {}. Log output:", arg.getVertexShader().getName(), arg.getFragmentShader().getName()
			);
			LOGGER.warn(GLX.glGetProgramInfoLog(arg.getProgramRef(), 32768));
		}
	}
}

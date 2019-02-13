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
		arg.method_1278().method_1282();
		arg.method_1274().method_1282();
		GLX.glDeleteProgram(arg.method_1270());
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
		arg.method_1278().attachTo(arg);
		arg.method_1274().attachTo(arg);
		GLX.glLinkProgram(arg.method_1270());
		int i = GLX.glGetProgrami(arg.method_1270(), GLX.GL_LINK_STATUS);
		if (i == 0) {
			LOGGER.warn("Error encountered when linking program containing VS {} and FS {}. Log output:", arg.method_1274().getName(), arg.method_1278().getName());
			LOGGER.warn(GLX.glGetProgramInfoLog(arg.method_1270(), 32768));
		}
	}
}

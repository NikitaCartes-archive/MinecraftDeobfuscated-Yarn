package net.minecraft;

import com.mojang.blaze3d.platform.GLX;
import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_285 {
	private static final Logger field_1551 = LogManager.getLogger();
	private static class_285 field_1550;

	public static void method_1305() {
		field_1550 = new class_285();
	}

	public static class_285 method_1308() {
		return field_1550;
	}

	private class_285() {
	}

	public void method_1304(class_3679 arg) {
		arg.method_1278().method_1282();
		arg.method_1274().method_1282();
		GLX.glDeleteProgram(arg.method_1270());
	}

	public int method_1306() throws IOException {
		int i = GLX.glCreateProgram();
		if (i <= 0) {
			throw new IOException("Could not create shader program (returned program ID " + i + ")");
		} else {
			return i;
		}
	}

	public void method_1307(class_3679 arg) throws IOException {
		arg.method_1278().method_1281(arg);
		arg.method_1274().method_1281(arg);
		GLX.glLinkProgram(arg.method_1270());
		int i = GLX.glGetProgrami(arg.method_1270(), GLX.GL_LINK_STATUS);
		if (i == 0) {
			field_1551.warn(
				"Error encountered when linking program containing VS {} and FS {}. Log output:", arg.method_1274().method_1280(), arg.method_1278().method_1280()
			);
			field_1551.warn(GLX.glGetProgramInfoLog(arg.method_1270(), 32768));
		}
	}
}

package net.minecraft;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.TextureUtil;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.lang3.StringUtils;

@Environment(EnvType.CLIENT)
public class class_281 {
	private final class_281.class_282 field_1524;
	private final String field_1525;
	private final int field_1523;
	private int field_1522;

	private class_281(class_281.class_282 arg, int i, String string) {
		this.field_1524 = arg;
		this.field_1523 = i;
		this.field_1525 = string;
	}

	public void method_1281(class_3679 arg) {
		this.field_1522++;
		GLX.glAttachShader(arg.method_1270(), this.field_1523);
	}

	public void method_1282() {
		this.field_1522--;
		if (this.field_1522 <= 0) {
			GLX.glDeleteShader(this.field_1523);
			this.field_1524.method_1289().remove(this.field_1525);
		}
	}

	public String method_1280() {
		return this.field_1525;
	}

	public static class_281 method_1283(class_281.class_282 arg, String string, InputStream inputStream) throws IOException {
		String string2 = TextureUtil.readResourceAsString(inputStream);
		if (string2 == null) {
			throw new IOException("Could not load program " + arg.method_1286());
		} else {
			int i = GLX.glCreateShader(arg.method_1287());
			GLX.glShaderSource(i, string2);
			GLX.glCompileShader(i);
			if (GLX.glGetShaderi(i, GLX.GL_COMPILE_STATUS) == 0) {
				String string3 = StringUtils.trim(GLX.glGetShaderInfoLog(i, 32768));
				throw new IOException("Couldn't compile " + arg.method_1286() + " program: " + string3);
			} else {
				class_281 lv = new class_281(arg, i, string);
				arg.method_1289().put(string, lv);
				return lv;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum class_282 {
		field_1530("vertex", ".vsh", GLX.GL_VERTEX_SHADER),
		field_1531("fragment", ".fsh", GLX.GL_FRAGMENT_SHADER);

		private final String field_1526;
		private final String field_1528;
		private final int field_1529;
		private final Map<String, class_281> field_1527 = Maps.<String, class_281>newHashMap();

		private class_282(String string2, String string3, int j) {
			this.field_1526 = string2;
			this.field_1528 = string3;
			this.field_1529 = j;
		}

		public String method_1286() {
			return this.field_1526;
		}

		public String method_1284() {
			return this.field_1528;
		}

		private int method_1287() {
			return this.field_1529;
		}

		public Map<String, class_281> method_1289() {
			return this.field_1527;
		}
	}
}

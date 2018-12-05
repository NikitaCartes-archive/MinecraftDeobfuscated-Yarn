package net.minecraft.client.gl;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.TextureUtil;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3679;
import org.apache.commons.lang3.StringUtils;

@Environment(EnvType.CLIENT)
public class GlShader {
	private final GlShader.Type shaderType;
	private final String name;
	private final int shaderRef;
	private int refCount;

	private GlShader(GlShader.Type type, int i, String string) {
		this.shaderType = type;
		this.shaderRef = i;
		this.name = string;
	}

	public void attachTo(class_3679 arg) {
		this.refCount++;
		GLX.glAttachShader(arg.getProgramRef(), this.shaderRef);
	}

	public void method_1282() {
		this.refCount--;
		if (this.refCount <= 0) {
			GLX.glDeleteShader(this.shaderRef);
			this.shaderType.getNameObjectMap().remove(this.name);
		}
	}

	public String getName() {
		return this.name;
	}

	public static GlShader createFromResource(GlShader.Type type, String string, InputStream inputStream) throws IOException {
		String string2 = TextureUtil.readResourceAsString(inputStream);
		if (string2 == null) {
			throw new IOException("Could not load program " + type.getName());
		} else {
			int i = GLX.glCreateShader(type.getShaderType());
			GLX.glShaderSource(i, string2);
			GLX.glCompileShader(i);
			if (GLX.glGetShaderi(i, GLX.GL_COMPILE_STATUS) == 0) {
				String string3 = StringUtils.trim(GLX.glGetShaderInfoLog(i, 32768));
				throw new IOException("Couldn't compile " + type.getName() + " program: " + string3);
			} else {
				GlShader glShader = new GlShader(type, i, string);
				type.getNameObjectMap().put(string, glShader);
				return glShader;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum Type {
		VERTEX("vertex", ".vsh", GLX.GL_VERTEX_SHADER),
		FRAGMENT("fragment", ".fsh", GLX.GL_FRAGMENT_SHADER);

		private final String name;
		private final String fileExtension;
		private final int shaderType;
		private final Map<String, GlShader> nameObjectMap = Maps.<String, GlShader>newHashMap();

		private Type(String string2, String string3, int j) {
			this.name = string2;
			this.fileExtension = string3;
			this.shaderType = j;
		}

		public String getName() {
			return this.name;
		}

		public String getFileExtension() {
			return this.fileExtension;
		}

		private int getShaderType() {
			return this.shaderType;
		}

		public Map<String, GlShader> getNameObjectMap() {
			return this.nameObjectMap;
		}
	}
}

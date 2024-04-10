package net.minecraft.client.gl;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Represents a programmable shader stage (a vertex or a fragment shader)
 * in the graphics pipeline. Also known as a shader object that can be
 * created with {@code glCreateShader}.
 * 
 * <p><strong>Warning:</strong> This class is referred to as a program in
 * strings. However, this does NOT represent a program object that can be
 * created with {@code glCreateProgram}. {@link ShaderProgram} is what
 * represents a program object.
 * 
 * @see <a href="https://www.khronos.org/opengl/wiki/Shader">
 * Shader - OpenGL Wiki</a>
 * @see <a href="https://www.khronos.org/opengl/wiki/GLSL_Object#Shader_objects">
 * GLSL Object - OpenGL Wiki (Shader objects)</a>
 */
@Environment(EnvType.CLIENT)
public class ShaderStage {
	private static final int MAX_INFO_LOG_LENGTH = 32768;
	private final ShaderStage.Type type;
	private final String name;
	private int glRef;

	protected ShaderStage(ShaderStage.Type type, int glRef, String name) {
		this.type = type;
		this.glRef = glRef;
		this.name = name;
	}

	public void attachTo(ShaderProgramSetupView program) {
		RenderSystem.assertOnRenderThread();
		GlStateManager.glAttachShader(program.getGlRef(), this.getGlRef());
	}

	public void release() {
		if (this.glRef != -1) {
			RenderSystem.assertOnRenderThread();
			GlStateManager.glDeleteShader(this.glRef);
			this.glRef = -1;
			this.type.getLoadedShaders().remove(this.name);
		}
	}

	public String getName() {
		return this.name;
	}

	public static ShaderStage createFromResource(ShaderStage.Type type, String name, InputStream stream, String domain, GlImportProcessor loader) throws IOException {
		RenderSystem.assertOnRenderThread();
		int i = load(type, name, stream, domain, loader);
		ShaderStage shaderStage = new ShaderStage(type, i, name);
		type.getLoadedShaders().put(name, shaderStage);
		return shaderStage;
	}

	protected static int load(ShaderStage.Type type, String name, InputStream stream, String domain, GlImportProcessor loader) throws IOException {
		String string = IOUtils.toString(stream, StandardCharsets.UTF_8);
		if (string == null) {
			throw new IOException("Could not load program " + type.getName());
		} else {
			int i = GlStateManager.glCreateShader(type.getGlType());
			GlStateManager.glShaderSource(i, loader.readSource(string));
			GlStateManager.glCompileShader(i);
			if (GlStateManager.glGetShaderi(i, GlConst.GL_COMPILE_STATUS) == 0) {
				String string2 = StringUtils.trim(GlStateManager.glGetShaderInfoLog(i, 32768));
				throw new IOException("Couldn't compile " + type.getName() + " program (" + domain + ", " + name + ") : " + string2);
			} else {
				return i;
			}
		}
	}

	protected int getGlRef() {
		return this.glRef;
	}

	/**
	 * Whether a vertex shader or a fragment shader.
	 */
	@Environment(EnvType.CLIENT)
	public static enum Type {
		VERTEX("vertex", ".vsh", 35633),
		FRAGMENT("fragment", ".fsh", 35632);

		private final String name;
		private final String fileExtension;
		private final int glType;
		private final Map<String, ShaderStage> loadedShaders = Maps.<String, ShaderStage>newHashMap();

		private Type(final String name, final String extension, final int glType) {
			this.name = name;
			this.fileExtension = extension;
			this.glType = glType;
		}

		public String getName() {
			return this.name;
		}

		public String getFileExtension() {
			return this.fileExtension;
		}

		int getGlType() {
			return this.glType;
		}

		/**
		 * {@return all loaded shaders of this type, keyed by their names}
		 */
		public Map<String, ShaderStage> getLoadedShaders() {
			return this.loadedShaders;
		}
	}
}

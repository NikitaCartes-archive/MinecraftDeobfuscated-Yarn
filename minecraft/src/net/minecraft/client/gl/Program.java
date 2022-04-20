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

@Environment(EnvType.CLIENT)
public class Program {
	private static final int MAX_SHADER_INFO_LOG_LENGTH = 32768;
	private final Program.Type shaderType;
	private final String name;
	private int shaderRef;

	protected Program(Program.Type shaderType, int shaderRef, String name) {
		this.shaderType = shaderType;
		this.shaderRef = shaderRef;
		this.name = name;
	}

	public void attachTo(GlShader program) {
		RenderSystem.assertOnRenderThread();
		GlStateManager.glAttachShader(program.getProgramRef(), this.getShaderRef());
	}

	public void release() {
		if (this.shaderRef != -1) {
			RenderSystem.assertOnRenderThread();
			GlStateManager.glDeleteShader(this.shaderRef);
			this.shaderRef = -1;
			this.shaderType.getProgramCache().remove(this.name);
		}
	}

	public String getName() {
		return this.name;
	}

	public static Program createFromResource(Program.Type type, String name, InputStream stream, String domain, GLImportProcessor loader) throws IOException {
		RenderSystem.assertOnRenderThread();
		int i = loadProgram(type, name, stream, domain, loader);
		Program program = new Program(type, i, name);
		type.getProgramCache().put(name, program);
		return program;
	}

	protected static int loadProgram(Program.Type type, String name, InputStream stream, String domain, GLImportProcessor loader) throws IOException {
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

	protected int getShaderRef() {
		return this.shaderRef;
	}

	@Environment(EnvType.CLIENT)
	public static enum Type {
		VERTEX("vertex", ".vsh", GlConst.GL_VERTEX_SHADER),
		FRAGMENT("fragment", ".fsh", GlConst.GL_FRAGMENT_SHADER);

		private final String name;
		private final String fileExtension;
		private final int glType;
		private final Map<String, Program> programCache = Maps.<String, Program>newHashMap();

		private Type(String name, String extension, int glType) {
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
		 * Gets a map of loaded shaders.
		 */
		public Map<String, Program> getProgramCache() {
			return this.programCache;
		}
	}
}

package net.minecraft.client.gl;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.lang3.StringUtils;

@Environment(EnvType.CLIENT)
public class Program {
	private static final int field_32037 = 32768;
	private final Program.Type shaderType;
	private final String name;
	private final int shaderRef;

	protected Program(Program.Type shaderType, int shaderRef, String name) {
		this.shaderType = shaderType;
		this.shaderRef = shaderRef;
		this.name = name;
	}

	public void attachTo(GlShader program) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GlStateManager.glAttachShader(program.getProgramRef(), this.getShaderRef());
	}

	public void release() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GlStateManager.glDeleteShader(this.shaderRef);
		this.shaderType.getProgramCache().remove(this.name);
	}

	public String getName() {
		return this.name;
	}

	public static Program createFromResource(Program.Type type, String name, InputStream stream, String domain, GLImportProcessor loader) throws IOException {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		int i = loadProgram(type, name, stream, domain, loader);
		Program program = new Program(type, i, name);
		type.getProgramCache().put(name, program);
		return program;
	}

	protected static int loadProgram(Program.Type type, String name, InputStream stream, String domain, GLImportProcessor loader) throws IOException {
		String string = TextureUtil.readResourceAsString(stream);
		if (string == null) {
			throw new IOException("Could not load program " + type.getName());
		} else {
			int i = GlStateManager.glCreateShader(type.getGlType());
			GlStateManager.glShaderSource(i, loader.readSource(string));
			GlStateManager.glCompileShader(i);
			if (GlStateManager.glGetShaderi(i, 35713) == 0) {
				String string2 = StringUtils.trim(GlStateManager.glGetShaderInfoLog(i, 32768));
				throw new IOException("Couldn't compile " + type.getName() + " program (" + domain + ", " + name + ") : " + string2);
			} else {
				return i;
			}
		}
	}

	private static Program create(Program.Type shaderType, String name, int shaderRef) {
		return new Program(shaderType, shaderRef, name);
	}

	protected int getShaderRef() {
		return this.shaderRef;
	}

	@Environment(EnvType.CLIENT)
	public static enum Type {
		VERTEX("vertex", ".vsh", 35633),
		FRAGMENT("fragment", ".fsh", 35632);

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

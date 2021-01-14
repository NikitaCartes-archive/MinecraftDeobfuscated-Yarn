package net.minecraft.client.gl;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.TextureUtil;
import org.apache.commons.lang3.StringUtils;

@Environment(EnvType.CLIENT)
public class Program {
	private final Program.Type shaderType;
	private final String name;
	private final int shaderRef;
	private int refCount;

	private Program(Program.Type shaderType, int shaderRef, String name) {
		this.shaderType = shaderType;
		this.shaderRef = shaderRef;
		this.name = name;
	}

	public void attachTo(GlShader program) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		this.refCount++;
		GlStateManager.attachShader(program.getProgramRef(), this.shaderRef);
	}

	public void release() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		this.refCount--;
		if (this.refCount <= 0) {
			GlStateManager.deleteShader(this.shaderRef);
			this.shaderType.getLoadedShaders().remove(this.name);
		}
	}

	public String getName() {
		return this.name;
	}

	public static Program createFromResource(Program.Type type, String name, InputStream sourceCode, String domain) throws IOException {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		String string = TextureUtil.readAllToString(sourceCode);
		if (string == null) {
			throw new IOException("Could not load program " + type.getName());
		} else {
			int i = GlStateManager.createShader(type.getGlType());
			GlStateManager.shaderSource(i, string);
			GlStateManager.compileShader(i);
			if (GlStateManager.getShader(i, 35713) == 0) {
				String string2 = StringUtils.trim(GlStateManager.getShaderInfoLog(i, 32768));
				throw new IOException("Couldn't compile " + type.getName() + " program (" + domain + ", " + name + ") : " + string2);
			} else {
				Program program = new Program(type, i, name);
				type.getLoadedShaders().put(name, program);
				return program;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum Type {
		VERTEX("vertex", ".vsh", 35633),
		FRAGMENT("fragment", ".fsh", 35632);

		private final String name;
		private final String fileExtension;
		private final int glType;
		private final Map<String, Program> loadedShaders = Maps.<String, Program>newHashMap();

		private Type(String name, String fileExtension, int glType) {
			this.name = name;
			this.fileExtension = fileExtension;
			this.glType = glType;
		}

		public String getName() {
			return this.name;
		}

		public String getFileExtension() {
			return this.fileExtension;
		}

		private int getGlType() {
			return this.glType;
		}

		public Map<String, Program> getLoadedShaders() {
			return this.loadedShaders;
		}
	}
}

package net.minecraft.client.gl;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringUtils;

@Environment(EnvType.CLIENT)
public class CompiledShader implements AutoCloseable {
	private static final int CLOSED = -1;
	private final Identifier id;
	private int handle;

	private CompiledShader(int handle, Identifier path) {
		this.id = path;
		this.handle = handle;
	}

	public static CompiledShader compile(Identifier id, CompiledShader.Type type, String source) throws ShaderLoader.LoadException {
		RenderSystem.assertOnRenderThread();
		int i = GlStateManager.glCreateShader(type.getGlType());
		GlStateManager.glShaderSource(i, source);
		GlStateManager.glCompileShader(i);
		if (GlStateManager.glGetShaderi(i, GlConst.GL_COMPILE_STATUS) == 0) {
			String string = StringUtils.trim(GlStateManager.glGetShaderInfoLog(i, 32768));
			throw new ShaderLoader.LoadException("Couldn't compile " + type.getName() + " shader (" + id + ") : " + string);
		} else {
			return new CompiledShader(i, id);
		}
	}

	public void close() {
		if (this.handle == -1) {
			throw new IllegalStateException("Already closed");
		} else {
			RenderSystem.assertOnRenderThread();
			GlStateManager.glDeleteShader(this.handle);
			this.handle = -1;
		}
	}

	public Identifier getId() {
		return this.id;
	}

	public int getHandle() {
		return this.handle;
	}

	/**
	 * Whether a vertex shader or a fragment shader.
	 */
	@Environment(EnvType.CLIENT)
	public static enum Type {
		VERTEX("vertex", ".vsh", 35633),
		FRAGMENT("fragment", ".fsh", 35632);

		private static final CompiledShader.Type[] VALUES = values();
		private final String name;
		private final String fileExtension;
		private final int glType;

		private Type(final String name, final String extension, final int glType) {
			this.name = name;
			this.fileExtension = extension;
			this.glType = glType;
		}

		@Nullable
		public static CompiledShader.Type fromId(Identifier id) {
			for (CompiledShader.Type type : VALUES) {
				if (id.getPath().endsWith(type.fileExtension)) {
					return type;
				}
			}

			return null;
		}

		public String getName() {
			return this.name;
		}

		public int getGlType() {
			return this.glType;
		}

		public ResourceFinder createFinder() {
			return new ResourceFinder("shaders", this.fileExtension);
		}
	}
}

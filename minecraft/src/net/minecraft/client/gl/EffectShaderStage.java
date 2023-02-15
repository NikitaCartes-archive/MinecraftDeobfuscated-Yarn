package net.minecraft.client.gl;

import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.io.InputStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * A shader stage that can be attached to multiple shader programs. This
 * shader stage will only be released once an equal number of shader
 * programs referencing it have been released.
 */
@Environment(EnvType.CLIENT)
public class EffectShaderStage extends ShaderStage {
	private static final GlImportProcessor LOADER = new GlImportProcessor() {
		@Override
		public String loadImport(boolean inline, String name) {
			return "#error Import statement not supported";
		}
	};
	private int refCount;

	private EffectShaderStage(ShaderStage.Type type, int glRef, String name) {
		super(type, glRef, name);
	}

	public void attachTo(EffectShaderProgram program) {
		RenderSystem.assertOnRenderThread();
		this.refCount++;
		this.attachTo(program);
	}

	@Override
	public void release() {
		RenderSystem.assertOnRenderThread();
		this.refCount--;
		if (this.refCount <= 0) {
			super.release();
		}
	}

	public static EffectShaderStage createFromResource(ShaderStage.Type type, String name, InputStream stream, String domain) throws IOException {
		RenderSystem.assertOnRenderThread();
		int i = load(type, name, stream, domain, LOADER);
		EffectShaderStage effectShaderStage = new EffectShaderStage(type, i, name);
		type.getLoadedShaders().put(name, effectShaderStage);
		return effectShaderStage;
	}
}

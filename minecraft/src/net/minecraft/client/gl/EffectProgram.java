package net.minecraft.client.gl;

import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.io.InputStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * A program that can be applied to multiple GLShaders.
 * This program will only be released once an equal number of programs referencing it have been released.
 */
@Environment(EnvType.CLIENT)
public class EffectProgram extends Program {
	private static final GLImportProcessor LOADER = new GLImportProcessor() {
		@Override
		public String loadImport(boolean inline, String name) {
			return "#error Import statement not supported";
		}
	};
	private int refCount;

	private EffectProgram(Program.Type type, int shaderRef, String name) {
		super(type, shaderRef, name);
	}

	public void attachTo(EffectGlShader program) {
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

	public static EffectProgram createFromResource(Program.Type type, String name, InputStream stream, String domain) throws IOException {
		RenderSystem.assertOnRenderThread();
		int i = loadProgram(type, name, stream, domain, LOADER);
		EffectProgram effectProgram = new EffectProgram(type, i, name);
		type.getProgramCache().put(name, effectProgram);
		return effectProgram;
	}
}

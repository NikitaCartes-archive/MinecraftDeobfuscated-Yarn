package net.minecraft.client.gl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface GlProgram {
	int getProgramRef();

	void markUniformsDirty();

	GlShader getVertexShader();

	GlShader getFragmentShader();
}

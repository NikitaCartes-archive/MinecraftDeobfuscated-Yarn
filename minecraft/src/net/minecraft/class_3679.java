package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.GlShader;

@Environment(EnvType.CLIENT)
public interface class_3679 {
	int getProgramRef();

	void markUniformStateDirty();

	GlShader getVertexShader();

	GlShader getFragmentShader();
}

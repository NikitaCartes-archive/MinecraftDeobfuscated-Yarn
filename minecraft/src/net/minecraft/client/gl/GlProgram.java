package net.minecraft.client.gl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface GlProgram {
	int getProgramRef();

	void markUniformsDirty();

	GlShader method_1274();

	GlShader method_1278();
}

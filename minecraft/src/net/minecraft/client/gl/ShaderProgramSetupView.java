package net.minecraft.client.gl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * A superinterface of {@link ShaderProgram} that exposes methods necessary
 * for linking or deleting this shader program.
 */
@Environment(EnvType.CLIENT)
public interface ShaderProgramSetupView {
	int getGlRef();

	void markUniformsDirty();

	ShaderStage getVertexShader();

	ShaderStage getFragmentShader();

	void attachReferencedShaders();
}

/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.ShaderStage;

/**
 * A superinterface of {@link ShaderProgram} that exposes methods necessary
 * for linking or deleting this shader program.
 */
@Environment(value=EnvType.CLIENT)
public interface ShaderProgramSetupView {
    public int getGlRef();

    public void markUniformsDirty();

    public ShaderStage getVertexShader();

    public ShaderStage getFragmentShader();

    public void attachReferencedShaders();
}


/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.GlShader;

@Environment(value=EnvType.CLIENT)
public interface GlProgram {
    public int getProgramRef();

    public void markUniformsDirty();

    public GlShader getVertexShader();

    public GlShader getFragmentShader();

    public void method_34418();
}


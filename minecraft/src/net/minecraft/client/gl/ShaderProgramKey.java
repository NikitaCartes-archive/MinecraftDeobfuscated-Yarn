package net.minecraft.client.gl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public record ShaderProgramKey(Identifier configId, VertexFormat vertexFormat, Defines defines) {
	public String toString() {
		String string = this.configId + " (" + this.vertexFormat + ")";
		return !this.defines.isEmpty() ? string + " with " + this.defines : string;
	}
}

package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public record Fog(float start, float end, FogShape shape, float red, float green, float blue, float alpha) {
	public static final Fog DUMMY = new Fog(Float.MAX_VALUE, 0.0F, FogShape.SPHERE, 0.0F, 0.0F, 0.0F, 0.0F);
}

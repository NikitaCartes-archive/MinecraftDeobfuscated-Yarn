package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class NoRenderParticle extends Particle {
	protected NoRenderParticle(World world, double d, double e, double f) {
		super(world, d, e, f);
	}

	protected NoRenderParticle(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f, g, h, i);
	}

	@Override
	public final void buildGeometry(BufferBuilder bufferBuilder, Camera camera, float f, float g, float h, float i, float j, float k) {
	}

	@Override
	public ParticleTextureSheet getTextureSheet() {
		return ParticleTextureSheet.NO_RENDER;
	}
}

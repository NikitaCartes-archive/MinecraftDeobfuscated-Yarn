package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;

/**
 * A {@link Particle} with no rendered texture. Useful for emitter particles (such as {@link EmitterParticle})
 * that spawn other particles while ticking, but do not render anything themselves.
 */
@Environment(EnvType.CLIENT)
public class NoRenderParticle extends Particle {
	protected NoRenderParticle(ClientWorld clientWorld, double d, double e, double f) {
		super(clientWorld, d, e, f);
	}

	protected NoRenderParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
		super(clientWorld, d, e, f, g, h, i);
	}

	@Override
	public final void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.NO_RENDER;
	}
}

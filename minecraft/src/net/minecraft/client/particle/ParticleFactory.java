package net.minecraft.client.particle;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public interface ParticleFactory<T extends ParticleEffect> {
	@Nullable
	Particle createParticle(T particleEffect, World world, double d, double e, double f, double g, double h, double i);
}

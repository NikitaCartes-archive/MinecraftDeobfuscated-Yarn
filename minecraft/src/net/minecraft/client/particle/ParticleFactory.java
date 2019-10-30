package net.minecraft.client.particle;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public interface ParticleFactory<T extends ParticleEffect> {
	@Nullable
	Particle createParticle(T parameters, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ);
}

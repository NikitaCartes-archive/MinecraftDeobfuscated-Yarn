package net.minecraft.client.particle;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public interface FactoryParticle<T extends net.minecraft.particle.Particle> {
	@Nullable
	Particle createParticle(T particle, World world, double d, double e, double f, double g, double h, double i);
}

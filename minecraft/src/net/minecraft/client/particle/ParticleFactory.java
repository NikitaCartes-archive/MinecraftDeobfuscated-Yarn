package net.minecraft.client.particle;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public interface ParticleFactory<T extends ParticleParameters> {
	@Nullable
	Particle method_3090(T particleParameters, World world, double d, double e, double f, double g, double h, double i);
}

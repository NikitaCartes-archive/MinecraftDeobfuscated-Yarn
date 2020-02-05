/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome;

import java.util.Random;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.DefaultParticleType;

public class BiomeParticleConfig {
    private final DefaultParticleType type;
    private final float chance;
    private final Function<Random, Double> velocityXFactory;
    private final Function<Random, Double> velocityYFactory;
    private final Function<Random, Double> velocityZFactory;

    public BiomeParticleConfig(DefaultParticleType type, float chance, Function<Random, Double> xFactory, Function<Random, Double> yFactory, Function<Random, Double> zFactory) {
        this.type = type;
        this.chance = chance;
        this.velocityXFactory = xFactory;
        this.velocityYFactory = yFactory;
        this.velocityZFactory = zFactory;
    }

    @Environment(value=EnvType.CLIENT)
    public DefaultParticleType getParticleType() {
        return this.type;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean shouldAddParticle(Random random) {
        return random.nextFloat() <= this.chance;
    }

    @Environment(value=EnvType.CLIENT)
    public double generateVelocityX(Random random) {
        return this.velocityXFactory.apply(random);
    }

    @Environment(value=EnvType.CLIENT)
    public double generateVelocityY(Random random) {
        return this.velocityYFactory.apply(random);
    }

    @Environment(value=EnvType.CLIENT)
    public double generateVelocityZ(Random random) {
        return this.velocityZFactory.apply(random);
    }
}


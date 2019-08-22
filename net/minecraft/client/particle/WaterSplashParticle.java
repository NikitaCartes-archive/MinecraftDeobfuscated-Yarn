/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.RainSplashParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.world.World;

@Environment(value=EnvType.CLIENT)
public class WaterSplashParticle
extends RainSplashParticle {
    private WaterSplashParticle(World world, double d, double e, double f, double g, double h, double i) {
        super(world, d, e, f);
        this.gravityStrength = 0.04f;
        if (h == 0.0 && (g != 0.0 || i != 0.0)) {
            this.velocityX = g;
            this.velocityY = 0.1;
            this.velocityZ = i;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class SplashFactory
    implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public SplashFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle method_3102(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
            WaterSplashParticle waterSplashParticle = new WaterSplashParticle(world, d, e, f, g, h, i);
            waterSplashParticle.setSprite(this.spriteProvider);
            return waterSplashParticle;
        }
    }
}


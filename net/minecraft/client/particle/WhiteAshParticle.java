/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.particle;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.AscendingParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

@Environment(value=EnvType.CLIENT)
public class WhiteAshParticle
extends AscendingParticle {
    protected WhiteAshParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, float scaleMultiplier, SpriteProvider spriteProvider) {
        super(world, x, y, z, 0.1f, -0.1f, 0.1f, velocityX, velocityY, velocityZ, scaleMultiplier, spriteProvider, 0.0f, 20, -5.0E-4, false);
        this.colorRed = 0.7294118f;
        this.colorGreen = 0.69411767f;
        this.colorBlue = 0.7607843f;
    }

    @Environment(value=EnvType.CLIENT)
    public static class Factory
    implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            Random random = clientWorld.random;
            double j = (double)random.nextFloat() * -1.9 * (double)random.nextFloat() * 0.1;
            double k = (double)random.nextFloat() * -0.5 * (double)random.nextFloat() * 0.1 * 5.0;
            double l = (double)random.nextFloat() * -1.9 * (double)random.nextFloat() * 0.1;
            return new WhiteAshParticle(clientWorld, d, e, f, j, k, l, 1.0f, this.spriteProvider);
        }
    }
}


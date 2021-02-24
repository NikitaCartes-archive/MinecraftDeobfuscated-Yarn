/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.particle;

import java.util.Optional;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5878;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class WaterSuspendParticle
extends SpriteBillboardParticle {
    private WaterSuspendParticle(ClientWorld world, SpriteProvider spriteProvider, double d, double e, double f) {
        super(world, d, e - 0.125, f);
        this.setBoundingBoxSpacing(0.01f, 0.01f);
        this.setSprite(spriteProvider);
        this.scale *= this.random.nextFloat() * 0.6f + 0.2f;
        this.maxAge = (int)(16.0 / (Math.random() * 0.8 + 0.2));
        this.collidesWithWorld = false;
        this.field_28786 = 1.0f;
        this.gravityStrength = 0.0f;
    }

    private WaterSuspendParticle(ClientWorld world, SpriteProvider spriteProvider, double d, double e, double f, double g, double h, double i) {
        super(world, d, e - 0.125, f, g, h, i);
        this.setBoundingBoxSpacing(0.01f, 0.01f);
        this.setSprite(spriteProvider);
        this.scale *= this.random.nextFloat() * 0.6f + 0.6f;
        this.maxAge = (int)(16.0 / (Math.random() * 0.8 + 0.2));
        this.collidesWithWorld = false;
        this.field_28786 = 1.0f;
        this.gravityStrength = 0.0f;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

    @Environment(value=EnvType.CLIENT)
    public static class WarpedSporeFactory
    implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public WarpedSporeFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            double j = (double)clientWorld.random.nextFloat() * -1.9 * (double)clientWorld.random.nextFloat() * 0.1;
            WaterSuspendParticle waterSuspendParticle = new WaterSuspendParticle(clientWorld, this.spriteProvider, d, e, f, 0.0, j, 0.0);
            waterSuspendParticle.setColor(0.1f, 0.1f, 0.3f);
            waterSuspendParticle.setBoundingBoxSpacing(0.001f, 0.001f);
            return waterSuspendParticle;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class CrimsonSporeFactory
    implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public CrimsonSporeFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            Random random = clientWorld.random;
            double j = random.nextGaussian() * (double)1.0E-6f;
            double k = random.nextGaussian() * (double)1.0E-4f;
            double l = random.nextGaussian() * (double)1.0E-6f;
            WaterSuspendParticle waterSuspendParticle = new WaterSuspendParticle(clientWorld, this.spriteProvider, d, e, f, j, k, l);
            waterSuspendParticle.setColor(0.9f, 0.4f, 0.5f);
            return waterSuspendParticle;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class class_5877
    implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider field_29073;

        public class_5877(SpriteProvider spriteProvider) {
            this.field_29073 = spriteProvider;
        }

        @Override
        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            WaterSuspendParticle waterSuspendParticle = new WaterSuspendParticle(clientWorld, this.field_29073, d, e, f, 0.0, (double)-0.8f, 0.0){

                @Override
                public Optional<class_5878> method_34019() {
                    return Optional.of(class_5878.field_29077);
                }
            };
            waterSuspendParticle.maxAge = MathHelper.nextBetween(clientWorld.random, 500, 1000);
            waterSuspendParticle.gravityStrength = 0.01f;
            waterSuspendParticle.setColor(0.32f, 0.5f, 0.22f);
            return waterSuspendParticle;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class UnderwaterFactory
    implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public UnderwaterFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            WaterSuspendParticle waterSuspendParticle = new WaterSuspendParticle(clientWorld, this.spriteProvider, d, e, f);
            waterSuspendParticle.setColor(0.4f, 0.4f, 0.7f);
            return waterSuspendParticle;
        }
    }
}


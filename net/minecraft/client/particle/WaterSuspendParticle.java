/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.particle;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
    private WaterSuspendParticle(ClientWorld world, double x, double y, double z) {
        super(world, x, y - 0.125, z);
        this.colorRed = 0.4f;
        this.colorGreen = 0.4f;
        this.colorBlue = 0.7f;
        this.setBoundingBoxSpacing(0.01f, 0.01f);
        this.scale *= this.random.nextFloat() * 0.6f + 0.2f;
        this.maxAge = (int)(16.0 / (Math.random() * 0.8 + 0.2));
        this.collidesWithWorld = false;
    }

    private WaterSuspendParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        super(world, x, y - 0.125, z, velocityX, velocityY, velocityZ);
        this.setBoundingBoxSpacing(0.01f, 0.01f);
        this.scale *= this.random.nextFloat() * 0.6f + 0.6f;
        this.maxAge = (int)(16.0 / (Math.random() * 0.8 + 0.2));
        this.collidesWithWorld = false;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
        if (this.maxAge-- <= 0) {
            this.markDead();
            return;
        }
        this.move(this.velocityX, this.velocityY, this.velocityZ);
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
            WaterSuspendParticle waterSuspendParticle = new WaterSuspendParticle(clientWorld, d, e, f, 0.0, j, 0.0);
            waterSuspendParticle.setSprite(this.spriteProvider);
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
            WaterSuspendParticle waterSuspendParticle = new WaterSuspendParticle(clientWorld, d, e, f, j, k, l);
            waterSuspendParticle.setSprite(this.spriteProvider);
            waterSuspendParticle.setColor(0.9f, 0.4f, 0.5f);
            return waterSuspendParticle;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class SporeBlossomAirFactory
    implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;
        private final Random random;

        public SporeBlossomAirFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
            this.random = new Random();
        }

        @Override
        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            WaterSuspendParticle waterSuspendParticle = new WaterSuspendParticle(clientWorld, d, e, f);
            waterSuspendParticle.maxAge = MathHelper.nextBetween(this.random, 1000, 2000);
            waterSuspendParticle.setSprite(this.spriteProvider);
            waterSuspendParticle.gravityStrength = 0.001f;
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
            WaterSuspendParticle waterSuspendParticle = new WaterSuspendParticle(clientWorld, d, e, f);
            waterSuspendParticle.setSprite(this.spriteProvider);
            return waterSuspendParticle;
        }
    }
}


/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.BackgroundHelper;
import net.minecraft.client.particle.AnimatedParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.BlockPos;

@Environment(value=EnvType.CLIENT)
public class SquidInkParticle
extends AnimatedParticle {
    SquidInkParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int color, SpriteProvider spriteProvider) {
        super(world, x, y, z, spriteProvider, 0.0f);
        this.velocityMultiplier = 0.92f;
        this.scale = 0.5f;
        this.setColorAlpha(1.0f);
        this.setColor(BackgroundHelper.ColorMixer.getRed(color), BackgroundHelper.ColorMixer.getGreen(color), BackgroundHelper.ColorMixer.getBlue(color));
        this.maxAge = (int)((double)(this.scale * 12.0f) / (Math.random() * (double)0.8f + (double)0.2f));
        this.setSpriteForAge(spriteProvider);
        this.collidesWithWorld = false;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.velocityZ = velocityZ;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.dead) {
            this.setSpriteForAge(this.spriteProvider);
            if (this.age > this.maxAge / 2) {
                this.setColorAlpha(1.0f - ((float)this.age - (float)(this.maxAge / 2)) / (float)this.maxAge);
            }
            if (this.world.getBlockState(new BlockPos(this.x, this.y, this.z)).isAir()) {
                this.velocityY -= (double)0.0074f;
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class GlowSquidInkFactory
    implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public GlowSquidInkFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return new SquidInkParticle(clientWorld, d, e, f, g, h, i, BackgroundHelper.ColorMixer.getArgb(255, 204, 31, 102), this.spriteProvider);
        }
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
            return new SquidInkParticle(clientWorld, d, e, f, g, h, i, BackgroundHelper.ColorMixer.getArgb(255, 255, 255, 255), this.spriteProvider);
        }
    }
}


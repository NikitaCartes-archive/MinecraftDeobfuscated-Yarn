/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@Environment(value=EnvType.CLIENT)
public class RedDustParticle
extends SpriteBillboardParticle {
    private final SpriteProvider spriteProvider;

    private RedDustParticle(World world, double d, double e, double f, double g, double h, double i, DustParticleEffect dustParticleEffect, SpriteProvider spriteProvider) {
        super(world, d, e, f, g, h, i);
        this.spriteProvider = spriteProvider;
        this.velocityX *= (double)0.1f;
        this.velocityY *= (double)0.1f;
        this.velocityZ *= (double)0.1f;
        float j = (float)Math.random() * 0.4f + 0.6f;
        this.colorRed = ((float)(Math.random() * (double)0.2f) + 0.8f) * dustParticleEffect.getRed() * j;
        this.colorGreen = ((float)(Math.random() * (double)0.2f) + 0.8f) * dustParticleEffect.getGreen() * j;
        this.colorBlue = ((float)(Math.random() * (double)0.2f) + 0.8f) * dustParticleEffect.getBlue() * j;
        this.scale *= 0.75f * dustParticleEffect.getAlpha();
        int k = (int)(8.0 / (Math.random() * 0.8 + 0.2));
        this.maxAge = (int)Math.max((float)k * dustParticleEffect.getAlpha(), 1.0f);
        this.setSpriteForAge(spriteProvider);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public float getSize(float f) {
        return this.scale * MathHelper.clamp(((float)this.age + f) / (float)this.maxAge * 32.0f, 0.0f, 1.0f);
    }

    @Override
    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
        if (this.age++ >= this.maxAge) {
            this.markDead();
            return;
        }
        this.setSpriteForAge(this.spriteProvider);
        this.move(this.velocityX, this.velocityY, this.velocityZ);
        if (this.y == this.prevPosY) {
            this.velocityX *= 1.1;
            this.velocityZ *= 1.1;
        }
        this.velocityX *= (double)0.96f;
        this.velocityY *= (double)0.96f;
        this.velocityZ *= (double)0.96f;
        if (this.onGround) {
            this.velocityX *= (double)0.7f;
            this.velocityZ *= (double)0.7f;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Factory
    implements ParticleFactory<DustParticleEffect> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle method_3022(DustParticleEffect dustParticleEffect, World world, double d, double e, double f, double g, double h, double i) {
            return new RedDustParticle(world, d, e, f, g, h, i, dustParticleEffect, this.spriteProvider);
        }
    }
}


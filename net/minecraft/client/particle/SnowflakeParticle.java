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
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

@Environment(value=EnvType.CLIENT)
public class SnowflakeParticle
extends SpriteBillboardParticle {
    private final SpriteProvider spriteProvider;

    protected SnowflakeParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider) {
        super(world, x, y, z);
        this.spriteProvider = spriteProvider;
        this.velocityX = velocityX + (Math.random() * 2.0 - 1.0) * (double)0.05f;
        this.velocityY = velocityY + (Math.random() * 2.0 - 1.0) * (double)0.05f;
        this.velocityZ = velocityZ + (Math.random() * 2.0 - 1.0) * (double)0.05f;
        this.scale = 0.1f * (this.random.nextFloat() * this.random.nextFloat() * 1.0f + 1.0f);
        this.maxAge = (int)(16.0 / ((double)this.random.nextFloat() * 0.8 + 0.2)) + 2;
        this.setSpriteForAge(spriteProvider);
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
        if (this.age++ >= this.maxAge) {
            this.markDead();
            return;
        }
        this.setSpriteForAge(this.spriteProvider);
        this.velocityY -= 0.009;
        this.move(this.velocityX, this.velocityY, this.velocityZ);
        this.velocityX *= (double)0.95f;
        this.velocityY *= (double)0.9f;
        this.velocityZ *= (double)0.95f;
        if (this.onGround) {
            this.velocityX *= (double)0.7f;
            this.velocityZ *= (double)0.7f;
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
            SnowflakeParticle snowflakeParticle = new SnowflakeParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
            snowflakeParticle.setColor(0.923f, 0.964f, 0.999f);
            return snowflakeParticle;
        }
    }
}


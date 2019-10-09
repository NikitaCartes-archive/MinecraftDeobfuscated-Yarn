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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@Environment(value=EnvType.CLIENT)
public class CloudParticle
extends SpriteBillboardParticle {
    private final SpriteProvider field_17862;

    private CloudParticle(World world, double d, double e, double f, double g, double h, double i, SpriteProvider spriteProvider) {
        super(world, d, e, f, 0.0, 0.0, 0.0);
        float k;
        this.field_17862 = spriteProvider;
        float j = 2.5f;
        this.velocityX *= (double)0.1f;
        this.velocityY *= (double)0.1f;
        this.velocityZ *= (double)0.1f;
        this.velocityX += g;
        this.velocityY += h;
        this.velocityZ += i;
        this.colorRed = k = 1.0f - (float)(Math.random() * (double)0.3f);
        this.colorGreen = k;
        this.colorBlue = k;
        this.scale *= 1.875f;
        int l = (int)(8.0 / (Math.random() * 0.8 + 0.3));
        this.maxAge = (int)Math.max((float)l * 2.5f, 1.0f);
        this.collidesWithWorld = false;
        this.setSpriteForAge(spriteProvider);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public float getSize(float f) {
        return this.scale * MathHelper.clamp(((float)this.age + f) / (float)this.maxAge * 32.0f, 0.0f, 1.0f);
    }

    @Override
    public void tick() {
        double d;
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
        if (this.age++ >= this.maxAge) {
            this.markDead();
            return;
        }
        this.setSpriteForAge(this.field_17862);
        this.move(this.velocityX, this.velocityY, this.velocityZ);
        this.velocityX *= (double)0.96f;
        this.velocityY *= (double)0.96f;
        this.velocityZ *= (double)0.96f;
        PlayerEntity playerEntity = this.world.getClosestPlayer(this.x, this.y, this.z, 2.0, false);
        if (playerEntity != null && this.y > (d = playerEntity.getY())) {
            this.y += (d - this.y) * 0.2;
            this.velocityY += (playerEntity.getVelocity().y - this.velocityY) * 0.2;
            this.setPos(this.x, this.y, this.z);
        }
        if (this.onGround) {
            this.velocityX *= (double)0.7f;
            this.velocityZ *= (double)0.7f;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class SneezeFactory
    implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider field_17864;

        public SneezeFactory(SpriteProvider spriteProvider) {
            this.field_17864 = spriteProvider;
        }

        public Particle method_3089(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
            CloudParticle particle = new CloudParticle(world, d, e, f, g, h, i, this.field_17864);
            particle.setColor(200.0f, 50.0f, 120.0f);
            particle.setColorAlpha(0.4f);
            return particle;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class CloudFactory
    implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider field_17863;

        public CloudFactory(SpriteProvider spriteProvider) {
            this.field_17863 = spriteProvider;
        }

        public Particle method_3088(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
            return new CloudParticle(world, d, e, f, g, h, i, this.field_17863);
        }
    }
}


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
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@Environment(value=EnvType.CLIENT)
public class DamageParticle
extends SpriteBillboardParticle {
    private DamageParticle(World world, double d, double e, double f, double g, double h, double i) {
        super(world, d, e, f, 0.0, 0.0, 0.0);
        float j;
        this.velocityX *= (double)0.1f;
        this.velocityY *= (double)0.1f;
        this.velocityZ *= (double)0.1f;
        this.velocityX += g * 0.4;
        this.velocityY += h * 0.4;
        this.velocityZ += i * 0.4;
        this.colorRed = j = (float)(Math.random() * (double)0.3f + (double)0.6f);
        this.colorGreen = j;
        this.colorBlue = j;
        this.scale *= 0.75f;
        this.maxAge = Math.max((int)(6.0 / (Math.random() * 0.8 + 0.6)), 1);
        this.collidesWithWorld = false;
        this.tick();
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
        this.move(this.velocityX, this.velocityY, this.velocityZ);
        this.colorGreen = (float)((double)this.colorGreen * 0.96);
        this.colorBlue = (float)((double)this.colorBlue * 0.9);
        this.velocityX *= (double)0.7f;
        this.velocityY *= (double)0.7f;
        this.velocityZ *= (double)0.7f;
        this.velocityY -= (double)0.02f;
        if (this.onGround) {
            this.velocityX *= (double)0.7f;
            this.velocityZ *= (double)0.7f;
        }
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

    @Environment(value=EnvType.CLIENT)
    public static class DefaultFactory
    implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider field_17790;

        public DefaultFactory(SpriteProvider spriteProvider) {
            this.field_17790 = spriteProvider;
        }

        public Particle method_3013(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
            DamageParticle damageParticle = new DamageParticle(world, d, e, f, g, h + 1.0, i);
            damageParticle.setMaxAge(20);
            damageParticle.setSprite(this.field_17790);
            return damageParticle;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class EnchantedHitFactory
    implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider field_17791;

        public EnchantedHitFactory(SpriteProvider spriteProvider) {
            this.field_17791 = spriteProvider;
        }

        public Particle method_3014(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
            DamageParticle damageParticle = new DamageParticle(world, d, e, f, g, h, i);
            damageParticle.colorRed *= 0.3f;
            damageParticle.colorGreen *= 0.8f;
            damageParticle.setSprite(this.field_17791);
            return damageParticle;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Factory
    implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider field_18291;

        public Factory(SpriteProvider spriteProvider) {
            this.field_18291 = spriteProvider;
        }

        public Particle method_17580(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
            DamageParticle damageParticle = new DamageParticle(world, d, e, f, g, h, i);
            damageParticle.setSprite(this.field_18291);
            return damageParticle;
        }
    }
}


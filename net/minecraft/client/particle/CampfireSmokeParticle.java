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
import net.minecraft.world.World;

@Environment(value=EnvType.CLIENT)
public class CampfireSmokeParticle
extends SpriteBillboardParticle {
    private CampfireSmokeParticle(World world, double d, double e, double f, double g, double h, double i, boolean bl) {
        super(world, d, e, f);
        this.method_3087(3.0f);
        this.setBoundingBoxSpacing(0.25f, 0.25f);
        this.maxAge = bl ? this.random.nextInt(50) + 280 : this.random.nextInt(50) + 80;
        this.gravityStrength = 3.0E-6f;
        this.velocityX = g;
        this.velocityY = h + (double)(this.random.nextFloat() / 500.0f);
        this.velocityZ = i;
    }

    @Override
    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
        if (this.age++ >= this.maxAge || this.colorAlpha <= 0.0f) {
            this.markDead();
            return;
        }
        this.velocityX += (double)(this.random.nextFloat() / 5000.0f * (float)(this.random.nextBoolean() ? 1 : -1));
        this.velocityZ += (double)(this.random.nextFloat() / 5000.0f * (float)(this.random.nextBoolean() ? 1 : -1));
        this.velocityY -= (double)this.gravityStrength;
        this.move(this.velocityX, this.velocityY, this.velocityZ);
        if (this.age >= this.maxAge - 60 && this.colorAlpha > 0.01f) {
            this.colorAlpha -= 0.015f;
        }
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Environment(value=EnvType.CLIENT)
    public static class SignalSmokeFactory
    implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider field_17789;

        public SignalSmokeFactory(SpriteProvider spriteProvider) {
            this.field_17789 = spriteProvider;
        }

        public Particle method_18820(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
            CampfireSmokeParticle campfireSmokeParticle = new CampfireSmokeParticle(world, d, e, f, g, h, i, true);
            campfireSmokeParticle.setColorAlpha(0.95f);
            campfireSmokeParticle.setSprite(this.field_17789);
            return campfireSmokeParticle;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class CosySmokeFactory
    implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider field_18290;

        public CosySmokeFactory(SpriteProvider spriteProvider) {
            this.field_18290 = spriteProvider;
        }

        public Particle method_17579(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
            CampfireSmokeParticle campfireSmokeParticle = new CampfireSmokeParticle(world, d, e, f, g, h, i, false);
            campfireSmokeParticle.setColorAlpha(0.9f);
            campfireSmokeParticle.setSprite(this.field_18290);
            return campfireSmokeParticle;
        }
    }
}


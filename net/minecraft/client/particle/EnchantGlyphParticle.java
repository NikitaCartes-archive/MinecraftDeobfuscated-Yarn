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
public class EnchantGlyphParticle
extends SpriteBillboardParticle {
    private final double startX;
    private final double startY;
    private final double startZ;

    private EnchantGlyphParticle(World world, double d, double e, double f, double g, double h, double i) {
        super(world, d, e, f);
        this.velocityX = g;
        this.velocityY = h;
        this.velocityZ = i;
        this.startX = d;
        this.startY = e;
        this.startZ = f;
        this.prevPosX = d + g;
        this.prevPosY = e + h;
        this.prevPosZ = f + i;
        this.x = this.prevPosX;
        this.y = this.prevPosY;
        this.z = this.prevPosZ;
        this.scale = 0.1f * (this.random.nextFloat() * 0.5f + 0.2f);
        float j = this.random.nextFloat() * 0.6f + 0.4f;
        this.colorRed = 0.9f * j;
        this.colorGreen = 0.9f * j;
        this.colorBlue = j;
        this.collidesWithWorld = false;
        this.maxAge = (int)(Math.random() * 10.0) + 30;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void move(double d, double e, double f) {
        this.setBoundingBox(this.getBoundingBox().offset(d, e, f));
        this.repositionFromBoundingBox();
    }

    @Override
    public int getColorMultiplier(float f) {
        int i = super.getColorMultiplier(f);
        float g = (float)this.age / (float)this.maxAge;
        g *= g;
        g *= g;
        int j = i & 0xFF;
        int k = i >> 16 & 0xFF;
        if ((k += (int)(g * 15.0f * 16.0f)) > 240) {
            k = 240;
        }
        return j | k << 16;
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
        float f = (float)this.age / (float)this.maxAge;
        f = 1.0f - f;
        float g = 1.0f - f;
        g *= g;
        g *= g;
        this.x = this.startX + this.velocityX * (double)f;
        this.y = this.startY + this.velocityY * (double)f - (double)(g * 1.2f);
        this.z = this.startZ + this.velocityZ * (double)f;
    }

    @Environment(value=EnvType.CLIENT)
    public static class EnchantFactory
    implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider field_17803;

        public EnchantFactory(SpriteProvider spriteProvider) {
            this.field_17803 = spriteProvider;
        }

        public Particle method_3021(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
            EnchantGlyphParticle enchantGlyphParticle = new EnchantGlyphParticle(world, d, e, f, g, h, i);
            enchantGlyphParticle.setSprite(this.field_17803);
            return enchantGlyphParticle;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class NautilusFactory
    implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider field_17804;

        public NautilusFactory(SpriteProvider spriteProvider) {
            this.field_17804 = spriteProvider;
        }

        public Particle method_3020(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
            EnchantGlyphParticle enchantGlyphParticle = new EnchantGlyphParticle(world, d, e, f, g, h, i);
            enchantGlyphParticle.setSprite(this.field_17804);
            return enchantGlyphParticle;
        }
    }
}


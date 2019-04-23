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
public class PortalParticle
extends SpriteBillboardParticle {
    private final double startX;
    private final double startY;
    private final double startZ;

    private PortalParticle(World world, double d, double e, double f, double g, double h, double i) {
        super(world, d, e, f);
        this.velocityX = g;
        this.velocityY = h;
        this.velocityZ = i;
        this.x = d;
        this.y = e;
        this.z = f;
        this.startX = this.x;
        this.startY = this.y;
        this.startZ = this.z;
        this.scale = 0.1f * (this.random.nextFloat() * 0.2f + 0.5f);
        float j = this.random.nextFloat() * 0.6f + 0.4f;
        this.colorRed = j * 0.9f;
        this.colorGreen = j * 0.3f;
        this.colorBlue = j;
        this.maxAge = (int)(Math.random() * 10.0) + 40;
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
    public float getSize(float f) {
        float g = ((float)this.age + f) / (float)this.maxAge;
        g = 1.0f - g;
        g *= g;
        g = 1.0f - g;
        return this.scale * g;
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
        float f;
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
        if (this.age++ >= this.maxAge) {
            this.markDead();
            return;
        }
        float g = f = (float)this.age / (float)this.maxAge;
        f = -f + f * f * 2.0f;
        f = 1.0f - f;
        this.x = this.startX + this.velocityX * (double)f;
        this.y = this.startY + this.velocityY * (double)f + (double)(1.0f - g);
        this.z = this.startZ + this.velocityZ * (double)f;
    }

    @Environment(value=EnvType.CLIENT)
    public static class Factory
    implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider field_17865;

        public Factory(SpriteProvider spriteProvider) {
            this.field_17865 = spriteProvider;
        }

        public Particle method_3094(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
            PortalParticle portalParticle = new PortalParticle(world, d, e, f, g, h, i);
            portalParticle.setSprite(this.field_17865);
            return portalParticle;
        }
    }
}


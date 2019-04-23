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
public class SweepAttackParticle
extends SpriteBillboardParticle {
    private final SpriteProvider field_17781;

    private SweepAttackParticle(World world, double d, double e, double f, double g, SpriteProvider spriteProvider) {
        super(world, d, e, f, 0.0, 0.0, 0.0);
        float h;
        this.field_17781 = spriteProvider;
        this.maxAge = 4;
        this.colorRed = h = this.random.nextFloat() * 0.6f + 0.4f;
        this.colorGreen = h;
        this.colorBlue = h;
        this.scale = 1.0f - (float)g * 0.5f;
        this.setSpriteForAge(spriteProvider);
    }

    @Override
    public int getColorMultiplier(float f) {
        return 0xF000F0;
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
        this.setSpriteForAge(this.field_17781);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_LIT;
    }

    @Environment(value=EnvType.CLIENT)
    public static class Factory
    implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider field_17782;

        public Factory(SpriteProvider spriteProvider) {
            this.field_17782 = spriteProvider;
        }

        public Particle method_3006(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
            return new SweepAttackParticle(world, d, e, f, g, this.field_17782);
        }
    }
}


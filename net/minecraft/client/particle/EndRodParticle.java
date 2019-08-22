/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.AnimatedParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.world.World;

@Environment(value=EnvType.CLIENT)
public class EndRodParticle
extends AnimatedParticle {
    private EndRodParticle(World world, double d, double e, double f, double g, double h, double i, SpriteProvider spriteProvider) {
        super(world, d, e, f, spriteProvider, -5.0E-4f);
        this.velocityX = g;
        this.velocityY = h;
        this.velocityZ = i;
        this.scale *= 0.75f;
        this.maxAge = 60 + this.random.nextInt(12);
        this.setTargetColor(15916745);
        this.setSpriteForAge(spriteProvider);
    }

    @Override
    public void move(double d, double e, double f) {
        this.setBoundingBox(this.getBoundingBox().offset(d, e, f));
        this.repositionFromBoundingBox();
    }

    @Environment(value=EnvType.CLIENT)
    public static class Factory
    implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider field_17805;

        public Factory(SpriteProvider spriteProvider) {
            this.field_17805 = spriteProvider;
        }

        public Particle method_3024(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
            return new EndRodParticle(world, d, e, f, g, h, i, this.field_17805);
        }
    }
}


/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.FireSmokeParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.world.World;

@Environment(value=EnvType.CLIENT)
public class FireSmokeLargeParticle
extends FireSmokeParticle {
    protected FireSmokeLargeParticle(World world, double d, double e, double f, double g, double h, double i, SpriteProvider spriteProvider) {
        super(world, d, e, f, g, h, i, 2.5f, spriteProvider);
    }

    @Environment(value=EnvType.CLIENT)
    public static class Factory
    implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider field_17817;

        public Factory(SpriteProvider spriteProvider) {
            this.field_17817 = spriteProvider;
        }

        public Particle method_3040(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
            return new FireSmokeLargeParticle(world, d, e, f, g, h, i, this.field_17817);
        }
    }
}


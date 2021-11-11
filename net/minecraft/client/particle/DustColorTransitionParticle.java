/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.AbstractDustParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DustColorTransitionParticleEffect;
import net.minecraft.util.math.Vec3f;

@Environment(value=EnvType.CLIENT)
public class DustColorTransitionParticle
extends AbstractDustParticle<DustColorTransitionParticleEffect> {
    private final Vec3f startColor;
    private final Vec3f endColor;

    protected DustColorTransitionParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, DustColorTransitionParticleEffect parameters, SpriteProvider spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ, parameters, spriteProvider);
        float f = this.random.nextFloat() * 0.4f + 0.6f;
        this.startColor = this.darken(parameters.getFromColor(), f);
        this.endColor = this.darken(parameters.getToColor(), f);
    }

    private Vec3f darken(Vec3f color, float multiplier) {
        return new Vec3f(this.darken(color.getX(), multiplier), this.darken(color.getY(), multiplier), this.darken(color.getZ(), multiplier));
    }

    private void updateColor(float tickDelta) {
        float f = ((float)this.age + tickDelta) / ((float)this.maxAge + 1.0f);
        Vec3f vec3f = this.startColor.copy();
        vec3f.lerp(this.endColor, f);
        this.colorRed = vec3f.getX();
        this.colorGreen = vec3f.getY();
        this.colorBlue = vec3f.getZ();
    }

    @Override
    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        this.updateColor(tickDelta);
        super.buildGeometry(vertexConsumer, camera, tickDelta);
    }

    @Environment(value=EnvType.CLIENT)
    public static class Factory
    implements ParticleFactory<DustColorTransitionParticleEffect> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(DustColorTransitionParticleEffect dustColorTransitionParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return new DustColorTransitionParticle(clientWorld, d, e, f, g, h, i, dustColorTransitionParticleEffect, this.spriteProvider);
        }
    }
}


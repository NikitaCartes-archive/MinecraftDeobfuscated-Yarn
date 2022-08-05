/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.particle;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ShriekParticleEffect;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

@Environment(value=EnvType.CLIENT)
public class ShriekParticle
extends SpriteBillboardParticle {
    private static final Vec3f field_38334 = Util.make(new Vec3f(0.5f, 0.5f, 0.5f), Vec3f::normalize);
    private static final Vec3f field_38335 = new Vec3f(-1.0f, -1.0f, 0.0f);
    private static final float X_ROTATION = 1.0472f;
    private int delay;

    ShriekParticle(ClientWorld world, double x, double y, double z, int delay) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        this.scale = 0.85f;
        this.delay = delay;
        this.maxAge = 30;
        this.gravityStrength = 0.0f;
        this.velocityX = 0.0;
        this.velocityY = 0.1;
        this.velocityZ = 0.0;
    }

    @Override
    public float getSize(float tickDelta) {
        return this.scale * MathHelper.clamp(((float)this.age + tickDelta) / (float)this.maxAge * 0.75f, 0.0f, 1.0f);
    }

    @Override
    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        if (this.delay > 0) {
            return;
        }
        this.alpha = 1.0f - MathHelper.clamp(((float)this.age + tickDelta) / (float)this.maxAge, 0.0f, 1.0f);
        this.buildGeometry(vertexConsumer, camera, tickDelta, quaternion -> {
            quaternion.hamiltonProduct(Vec3f.POSITIVE_Y.getRadialQuaternion(0.0f));
            quaternion.hamiltonProduct(Vec3f.POSITIVE_X.getRadialQuaternion(-1.0472f));
        });
        this.buildGeometry(vertexConsumer, camera, tickDelta, quaternion -> {
            quaternion.hamiltonProduct(Vec3f.POSITIVE_Y.getRadialQuaternion((float)(-Math.PI)));
            quaternion.hamiltonProduct(Vec3f.POSITIVE_X.getRadialQuaternion(1.0472f));
        });
    }

    private void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta, Consumer<Quaternion> rotator) {
        int j;
        Vec3d vec3d = camera.getPos();
        float f = (float)(MathHelper.lerp((double)tickDelta, this.prevPosX, this.x) - vec3d.getX());
        float g = (float)(MathHelper.lerp((double)tickDelta, this.prevPosY, this.y) - vec3d.getY());
        float h = (float)(MathHelper.lerp((double)tickDelta, this.prevPosZ, this.z) - vec3d.getZ());
        Quaternion quaternion = new Quaternion(field_38334, 0.0f, true);
        rotator.accept(quaternion);
        field_38335.rotate(quaternion);
        Vec3f[] vec3fs = new Vec3f[]{new Vec3f(-1.0f, -1.0f, 0.0f), new Vec3f(-1.0f, 1.0f, 0.0f), new Vec3f(1.0f, 1.0f, 0.0f), new Vec3f(1.0f, -1.0f, 0.0f)};
        float i = this.getSize(tickDelta);
        for (j = 0; j < 4; ++j) {
            Vec3f vec3f = vec3fs[j];
            vec3f.rotate(quaternion);
            vec3f.scale(i);
            vec3f.add(f, g, h);
        }
        j = this.getBrightness(tickDelta);
        this.vertex(vertexConsumer, vec3fs[0], this.getMaxU(), this.getMaxV(), j);
        this.vertex(vertexConsumer, vec3fs[1], this.getMaxU(), this.getMinV(), j);
        this.vertex(vertexConsumer, vec3fs[2], this.getMinU(), this.getMinV(), j);
        this.vertex(vertexConsumer, vec3fs[3], this.getMinU(), this.getMaxV(), j);
    }

    private void vertex(VertexConsumer vertexConsumer, Vec3f pos, float u, float v, int light) {
        vertexConsumer.vertex(pos.getX(), pos.getY(), pos.getZ()).texture(u, v).color(this.red, this.green, this.blue, this.alpha).light(light).next();
    }

    @Override
    public int getBrightness(float tint) {
        return 240;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        if (this.delay > 0) {
            --this.delay;
            return;
        }
        super.tick();
    }

    @Environment(value=EnvType.CLIENT)
    public static class Factory
    implements ParticleFactory<ShriekParticleEffect> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(ShriekParticleEffect shriekParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            ShriekParticle shriekParticle = new ShriekParticle(clientWorld, d, e, f, shriekParticleEffect.getDelay());
            shriekParticle.setSprite(this.spriteProvider);
            shriekParticle.setAlpha(1.0f);
            return shriekParticle;
        }
    }
}


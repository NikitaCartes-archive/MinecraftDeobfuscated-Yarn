/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.particle;

import java.util.Optional;
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
import net.minecraft.particle.VibrationParticleEffect;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.event.PositionSource;

@Environment(value=EnvType.CLIENT)
public class VibrationParticle
extends SpriteBillboardParticle {
    private final PositionSource vibration;
    private float field_28250;
    private float field_28248;

    VibrationParticle(ClientWorld world, double x, double y, double z, PositionSource vibration, int maxAge) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        this.scale = 0.3f;
        this.vibration = vibration;
        this.maxAge = maxAge;
    }

    @Override
    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        float f = MathHelper.sin(((float)this.age + tickDelta - (float)Math.PI * 2) * 0.05f) * 2.0f;
        float g = MathHelper.lerp(tickDelta, this.field_28248, this.field_28250);
        float h = 1.0472f;
        this.render(vertexConsumer, camera, tickDelta, quaternion -> {
            quaternion.hamiltonProduct(Vec3f.POSITIVE_Y.getRadialQuaternion(g));
            quaternion.hamiltonProduct(Vec3f.POSITIVE_X.getRadialQuaternion(-1.0472f));
            quaternion.hamiltonProduct(Vec3f.POSITIVE_Y.getRadialQuaternion(f));
        });
        this.render(vertexConsumer, camera, tickDelta, quaternion -> {
            quaternion.hamiltonProduct(Vec3f.POSITIVE_Y.getRadialQuaternion((float)(-Math.PI) + g));
            quaternion.hamiltonProduct(Vec3f.POSITIVE_X.getRadialQuaternion(1.0472f));
            quaternion.hamiltonProduct(Vec3f.POSITIVE_Y.getRadialQuaternion(f));
        });
    }

    private void render(VertexConsumer vertexConsumer, Camera camera, float tickDelta, Consumer<Quaternion> transforms) {
        Vec3d vec3d = camera.getPos();
        float f = (float)(MathHelper.lerp((double)tickDelta, this.prevPosX, this.x) - vec3d.getX());
        float g = (float)(MathHelper.lerp((double)tickDelta, this.prevPosY, this.y) - vec3d.getY());
        float h = (float)(MathHelper.lerp((double)tickDelta, this.prevPosZ, this.z) - vec3d.getZ());
        Vec3f vec3f = new Vec3f(0.5f, 0.5f, 0.5f);
        vec3f.normalize();
        Quaternion quaternion = new Quaternion(vec3f, 0.0f, true);
        transforms.accept(quaternion);
        Vec3f vec3f2 = new Vec3f(-1.0f, -1.0f, 0.0f);
        vec3f2.rotate(quaternion);
        Vec3f[] vec3fs = new Vec3f[]{new Vec3f(-1.0f, -1.0f, 0.0f), new Vec3f(-1.0f, 1.0f, 0.0f), new Vec3f(1.0f, 1.0f, 0.0f), new Vec3f(1.0f, -1.0f, 0.0f)};
        float i = this.getSize(tickDelta);
        for (int j = 0; j < 4; ++j) {
            Vec3f vec3f3 = vec3fs[j];
            vec3f3.rotate(quaternion);
            vec3f3.scale(i);
            vec3f3.add(f, g, h);
        }
        float k = this.getMinU();
        float l = this.getMaxU();
        float m = this.getMinV();
        float n = this.getMaxV();
        int o = this.getBrightness(tickDelta);
        vertexConsumer.vertex(vec3fs[0].getX(), vec3fs[0].getY(), vec3fs[0].getZ()).texture(l, n).color(this.red, this.green, this.blue, this.alpha).light(o).next();
        vertexConsumer.vertex(vec3fs[1].getX(), vec3fs[1].getY(), vec3fs[1].getZ()).texture(l, m).color(this.red, this.green, this.blue, this.alpha).light(o).next();
        vertexConsumer.vertex(vec3fs[2].getX(), vec3fs[2].getY(), vec3fs[2].getZ()).texture(k, m).color(this.red, this.green, this.blue, this.alpha).light(o).next();
        vertexConsumer.vertex(vec3fs[3].getX(), vec3fs[3].getY(), vec3fs[3].getZ()).texture(k, n).color(this.red, this.green, this.blue, this.alpha).light(o).next();
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
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
        if (this.age++ >= this.maxAge) {
            this.markDead();
            return;
        }
        Optional<Vec3d> optional = this.vibration.getPos(this.world);
        if (optional.isEmpty()) {
            this.markDead();
            return;
        }
        int i = this.maxAge - this.age;
        double d = 1.0 / (double)i;
        Vec3d vec3d = optional.get();
        this.x = MathHelper.lerp(d, this.x, vec3d.getX());
        this.y = MathHelper.lerp(d, this.y, vec3d.getY());
        this.z = MathHelper.lerp(d, this.z, vec3d.getZ());
        this.field_28248 = this.field_28250;
        this.field_28250 = (float)MathHelper.atan2(this.x - vec3d.getX(), this.z - vec3d.getZ());
    }

    @Environment(value=EnvType.CLIENT)
    public static class Factory
    implements ParticleFactory<VibrationParticleEffect> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(VibrationParticleEffect vibrationParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            VibrationParticle vibrationParticle = new VibrationParticle(clientWorld, d, e, f, vibrationParticleEffect.getVibration(), vibrationParticleEffect.getArrivalInTicks());
            vibrationParticle.setSprite(this.spriteProvider);
            vibrationParticle.setAlpha(1.0f);
            return vibrationParticle;
        }
    }
}

